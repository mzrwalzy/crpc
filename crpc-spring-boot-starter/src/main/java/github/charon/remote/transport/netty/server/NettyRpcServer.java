package github.charon.remote.transport.netty.server;

import github.charon.common.factory.SingletonFactory;
import github.charon.common.utils.IpUtil;
import github.charon.common.utils.RuntimeUtil;
import github.charon.common.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import github.charon.config.CustomShutdownHook;
import github.charon.config.RpcServiceConfig;
import github.charon.provider.ServiceProvider;
import github.charon.provider.impl.ZkServiceProviderImpl;
import github.charon.remote.transport.netty.codec.RpcMessageDecoder;
import github.charon.remote.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer {

    private final String serializeProtocol;
    @Autowired
    private CustomShutdownHook customShutdownHook;

    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private NettyRpcServerHandler nettyRpcServerHandler;


    public NettyRpcServer(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }


    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        customShutdownHook.clearAll();
//        String host = InetAddress.getLocalHost().getHostAddress();
        String host = IpUtil.getRealIp();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false)
        );

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, nettyRpcServerHandler);
                        }
                    }

                    );
            ChannelFuture f = b.bind(host, customShutdownHook.getPort()).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }

}