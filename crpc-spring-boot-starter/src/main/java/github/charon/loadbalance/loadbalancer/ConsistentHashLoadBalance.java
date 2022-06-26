package github.charon.loadbalance.loadbalancer;

import github.charon.loadbalance.AbstractLoadBalance;
import github.charon.remote.dto.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest request) {
        int identityHashCode = System.identityHashCode(serviceAddresses);
        String serviceName = request.getRpcServiceName();
        ConsistentHashSelector selector = selectors.get(serviceName);


        if (null == selector || selector.identityHashCode != identityHashCode) {
            selectors.put(serviceName, new ConsistentHashSelector(serviceAddresses, 160, identityHashCode));
            selector = selectors.get(serviceName);
        }
        return selector.select(serviceName + Arrays.stream(request.getParams()));
    }

    static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;

        private final int identityHashCode;

        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();

            this.identityHashCode = identityHashCode;

            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        Long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md.digest();
        }

        static Long hash(byte[] digest, int i) {
            return ((long) (digest[3 + i * 4] & 255) << 24 | (long) (digest[2 + i * 4] & 255) << 16 | (long) (digest[1 + i * 4] & 255) << 8 | (long) (digest[i * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (null == entry) entry = virtualInvokers.firstEntry();

            return entry.getValue();
        }
    }
}
