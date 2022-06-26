package github.charon.loadbalance;

import github.charon.common.utils.CollectionUtil;
import github.charon.remote.dto.RpcRequest;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest RpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddresses)) return null;
        if (serviceAddresses.size() == 1) return serviceAddresses.get(0);
        return doSelect(serviceAddresses, RpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest RpcRequest);
}
