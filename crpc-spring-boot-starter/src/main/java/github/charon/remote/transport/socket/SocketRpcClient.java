package github.charon.remote.transport.socket;

import github.charon.registry.ServiceDiscovery;
import github.charon.remote.dto.RpcRequest;
import github.charon.remote.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        return null;
    }
}
