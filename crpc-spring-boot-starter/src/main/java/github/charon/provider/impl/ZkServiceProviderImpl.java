package github.charon.provider.impl;

import github.charon.common.enums.RpcErrorMessage;
import github.charon.common.exception.RpcException;
import github.charon.common.extension.ExtensionLoader;
import github.charon.common.utils.IpUtil;
import github.charon.config.CrpcConfig;
import github.charon.config.RpcServiceConfig;
import github.charon.provider.ServiceProvider;
import github.charon.registry.ServiceRegistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    private int port;

    public ZkServiceProviderImpl(int port, ServiceRegistry zkServiceRegistry) {
        this.port = port;
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = zkServiceRegistry;
//        System.out.println(serviceRegistry);
//        System.out.println("ZkServiceProviderImpl实例化");
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces: {}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND);
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
//            String host = InetAddress.getLocalHost().getHostAddress();
            String host = IpUtil.getRealIp();
            if (null == host) {
                throw new NullPointerException("Empty ip, check if connected to network");
            }
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, this.port));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }


}
