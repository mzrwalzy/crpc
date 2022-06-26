package github.charon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrpcConfig {
    private Integer port;
    private Integer zkPort;
    private String zkHost;
    private String serializeProtocol;
}
