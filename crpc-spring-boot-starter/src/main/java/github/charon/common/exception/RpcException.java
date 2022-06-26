package github.charon.common.exception;

import github.charon.common.enums.RpcErrorMessage;

public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessage rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessage rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
