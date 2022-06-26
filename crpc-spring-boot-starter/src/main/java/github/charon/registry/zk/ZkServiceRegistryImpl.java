package github.charon.registry.zk;

import github.charon.registry.ServiceRegistry;
import github.charon.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZkServiceRegistryImpl implements ServiceRegistry {

    private String zkHost;

    private int zkPort;

    public ZkServiceRegistryImpl(String zkHost, int zkPort) {
        this.zkHost = zkHost;
        this.zkPort = zkPort;
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient(zkHost + ":" + zkPort);
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
