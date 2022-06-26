package github.charon;

import github.charon.annotation.RpcScan;
import github.charon.annotation.RpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RpcScan(basePackage = {"github.charon"})
@RpcServer
@SpringBootApplication
public class CrpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrpcServerApplication.class, args);
    }

}
