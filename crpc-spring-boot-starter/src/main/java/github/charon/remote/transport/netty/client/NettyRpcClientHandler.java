package github.charon.remote.transport.netty.client;

import github.charon.common.enums.CompressType;
import github.charon.common.enums.SerializationType;
import github.charon.common.factory.SingletonFactory;
import github.charon.remote.constant.RpcConstants;
import github.charon.remote.dto.RpcMessage;
import github.charon.remote.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;

    private final String serializeProtocol;

    private final ChannelProvider channelProvider;

    private final AtomicInteger writerIdleCount = new AtomicInteger(0);

    private final int MAX_IDLE_COUNT = 10;

    public NettyRpcClientHandler(String serializeProtocol) {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        this.serializeProtocol = serializeProtocol;
    }

    /**
     * Read the message transmitted by the server
     */
    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", tmp.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    this.writerIdleCount.getAndSet(0);
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                log.info("write idle happen [{}]", address);
                Channel channel = channelProvider.get(address);
                if (writerIdleCount.get() > MAX_IDLE_COUNT) {
                    channel.close();
                    channelProvider.remove(address);
                    log.info("[{}] connection closed because too many idle times", address);
                    return;
                }
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationType.getCode(this.serializeProtocol));
                rpcMessage.setCompress(CompressType.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.channel().close();
                        channelProvider.remove(address);
                    } else {
                        writerIdleCount.getAndIncrement();
                        if (writerIdleCount.get() > MAX_IDLE_COUNT) {
                            future.channel().close();
                            channelProvider.remove(address);
                            log.info("[{}] connection closed because too many idle times", address);
                        }
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
