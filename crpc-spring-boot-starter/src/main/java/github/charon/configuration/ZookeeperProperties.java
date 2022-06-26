package github.charon.configuration;

import github.charon.constraints.IP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Component
@ConfigurationProperties(prefix = "crpc.zk")
@Validated
public class ZookeeperProperties {

    @IP
    private String host = "127.0.0.1";

    @Max(value = 65535)
    @Min(value = 0)
    private Integer port = 2181;
}
