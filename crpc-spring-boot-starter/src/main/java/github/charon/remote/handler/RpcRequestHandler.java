package github.charon.remote.handler;

import github.charon.common.exception.RpcException;
import github.charon.common.factory.SingletonFactory;
import github.charon.provider.ServiceProvider;
import github.charon.provider.impl.ZkServiceProviderImpl;
import github.charon.remote.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {
    @Autowired
    private ServiceProvider serviceProvider;

    public RpcRequestHandler() {
//        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParams());
            log.info("service:[{}] successful invoke method:[{}]", request.getInterfaceName(), request.getMethodName());
        }catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
