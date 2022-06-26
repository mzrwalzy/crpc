package github.charon.remote.transport;

import github.charon.common.extension.SPI;
import github.charon.remote.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest request);
}
