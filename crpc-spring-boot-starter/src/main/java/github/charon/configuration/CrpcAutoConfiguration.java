package github.charon.configuration;


import github.charon.config.CustomShutdownHook;
import github.charon.provider.impl.ZkServiceProviderImpl;
import github.charon.registry.ServiceDiscovery;
import github.charon.registry.zk.ZkServiceDiscoveryImpl;
import github.charon.registry.zk.ZkServiceRegistryImpl;
import github.charon.remote.handler.RpcRequestHandler;
import github.charon.remote.transport.netty.client.NettyRpcClient;
import github.charon.remote.transport.netty.server.NettyRpcServer;
import github.charon.remote.transport.netty.server.NettyRpcServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CrpcProperties.class, ZookeeperProperties.class, SerializeProperties.class})
public class CrpcAutoConfiguration {

    @Autowired
    CrpcProperties crpcProperties;

    @Autowired
    ZookeeperProperties zookeeperProperties;

    @Autowired
    SerializeProperties serializeProperties;

//    @Bean
//    @ConditionalOnMissingBean(CrpcConfig.class)
//    public CrpcConfig crpcConfig() {
//        CrpcConfig crpcConfig = new CrpcConfig();
//        crpcConfig.setPort(crpcProperties.getPort());
//        crpcConfig.setZkHost(zookeeperProperties.getHost());
//        crpcConfig.setZkPort(zookeeperProperties.getPort());
//        crpcConfig.setSerializeProtocol(serializeProperties.getProtocol());
//        return crpcConfig;
//    }

    @Bean
    @ConditionalOnMissingBean
    public ZkServiceProviderImpl zkServiceProvider() {
        return new ZkServiceProviderImpl(crpcProperties.getPort(), zkServiceRegistry());
    }


    @Bean
    @ConditionalOnMissingBean
    public CustomShutdownHook customShutdownHook() {
        return new CustomShutdownHook(crpcProperties.getPort(), zookeeperProperties.getHost(), zookeeperProperties.getPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public ZkServiceRegistryImpl zkServiceRegistry() {
        return new ZkServiceRegistryImpl(zookeeperProperties.getHost(), zookeeperProperties.getPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public ZkServiceDiscoveryImpl zkServiceDiscovery() {
        return new ZkServiceDiscoveryImpl(zookeeperProperties.getHost(), zookeeperProperties.getPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyRpcClient nettyRpcClient() {
        return new NettyRpcClient(zkServiceDiscovery(), serializeProperties.getProtocol());
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyRpcServer nettyRpcServer() {
        return new NettyRpcServer(serializeProperties.getProtocol());
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcRequestHandler rpcRequestHandler() {
        return new RpcRequestHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyRpcServerHandler nettyRpcServerHandler() {
        return new NettyRpcServerHandler(serializeProperties.getProtocol(), rpcRequestHandler());
    }
}
