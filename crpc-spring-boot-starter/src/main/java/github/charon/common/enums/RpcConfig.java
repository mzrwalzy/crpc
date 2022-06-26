package github.charon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfig {
    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("crpc.zk.address");

    private final String propertyValue;
}
