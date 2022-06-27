package github.charon.controller;

import github.charon.annotation.RpcReference;
import github.charon.dto.Hello;
import github.charon.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    @GetMapping("/hello")
    @ResponseBody
    public String test() throws InterruptedException {
//        System.out.println(this.helloService);
        String hello = this.helloService.hello(new Hello("111", "222"));
//        System.out.println(hello);
        return hello;
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
//        assert "Hello description is 222".equals(hello);
//        Thread.sleep(12000);
//        for (int i = 0; i < 10; i++) {
//            System.out.println(helloService.hello(new Hello("111", "222")));
//        }
    }
}