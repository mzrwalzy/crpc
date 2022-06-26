package github.charon.configuration;

import github.charon.constraints.ListValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@ConfigurationProperties(prefix = "crpc.serialize")
@Validated
@Component
public class SerializeProperties {

    @ListValue(values = {"hessian", "kryo", "protostuff"})
    private String protocol = "hessian";
}
