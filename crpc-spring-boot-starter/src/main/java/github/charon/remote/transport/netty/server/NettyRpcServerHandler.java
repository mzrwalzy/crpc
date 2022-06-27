package github.charon.remote.transport.netty.server;

import github.charon.common.enums.CompressType;
import github.charon.common.enums.RpcResponseCode;
import github.charon.common.enums.SerializationType;
import github.charon.remote.constant.RpcConstants;
import github.charon.remote.dto.RpcMessage;
import github.charon.remote.dto.RpcRequest;
import github.charon.remote.dto.RpcResponse;
import github.charon.remote.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final String serializeProtocol;

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler(String serializeProtocol, RpcRequestHandler rpcRequestHandler) {
//        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
        this.rpcRequestHandler = rpcRequestHandler;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationType.getCode(serializeProtocol));
                rpcMessage.setCompress(CompressType.GZIP.getCode());
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    RpcRequest request = (RpcRequest) ((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(request);
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> response = RpcResponse.success(result, request.getRequestId());
                        rpcMessage.setData(response);
                    } else {
                        RpcResponse<Object> response = RpcResponse.fail(RpcResponseCode.FAIL);
                        rpcMessage.setData(response);
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}