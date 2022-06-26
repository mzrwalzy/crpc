package github.charon;

import github.charon.annotation.RpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RpcScan(basePackage = {"github.charon"})
@SpringBootApplication
public class CrpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrpcClientApplication.class, args);

    }

}
