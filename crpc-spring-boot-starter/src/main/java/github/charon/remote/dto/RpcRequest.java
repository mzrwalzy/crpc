package github.charon.remote.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;

    private String version;
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + "_" + this.getGroup() + "_" + this.getVersion();
    }

}
