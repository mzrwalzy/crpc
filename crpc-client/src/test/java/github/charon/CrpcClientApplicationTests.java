package github.charon;

import github.charon.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrpcClientApplicationTests {

    @Autowired
    private HelloController helloController;

    @Test
    void contextLoads() throws InterruptedException {
//        HelloController helloController = new HelloController();
        helloController.test();
    }

}
