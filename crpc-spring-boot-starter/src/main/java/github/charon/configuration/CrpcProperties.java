package github.charon.configuration;

import github.charon.constraints.CrpcPort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ConfigurationProperties("crpc")
@Validated
@Component
public class CrpcProperties {

    @CrpcPort
    private Integer port = 10000;


}
