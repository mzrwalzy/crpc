package github.charon.registry.zk;

import github.charon.common.enums.RpcErrorMessage;
import github.charon.common.exception.RpcException;
import github.charon.common.extension.ExtensionLoader;
import github.charon.common.utils.CollectionUtil;
import github.charon.loadbalance.LoadBalance;
import github.charon.registry.ServiceDiscovery;
import github.charon.registry.zk.util.CuratorUtils;
import github.charon.remote.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    private final String zkHost;

    private final int zkPort;

    public ZkServiceDiscoveryImpl(String zkHost, int zkPort) {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
        this.zkHost = zkHost;
        this.zkPort = zkPort;
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient(zkHost + ":" + zkPort);
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);

        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, request);
        if (null == targetServiceUrl) throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
