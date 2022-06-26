package github.charon.loadbalance;

import github.charon.common.extension.SPI;
import github.charon.remote.dto.RpcRequest;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest RpcRequest);
}
