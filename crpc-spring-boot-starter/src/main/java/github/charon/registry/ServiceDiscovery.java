package github.charon.registry;

import github.charon.common.extension.SPI;
import github.charon.remote.dto.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest request);
}
