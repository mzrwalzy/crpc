package github.charon.provider;

import github.charon.config.RpcServiceConfig;

public interface ServiceProvider {
    void addService(RpcServiceConfig rpcServiceConfig);
    Object getService(String rpcServiceName);
    void publishService(RpcServiceConfig rpcServiceConfig);
}
