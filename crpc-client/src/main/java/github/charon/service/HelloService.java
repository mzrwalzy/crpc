package github.charon.service;

import github.charon.dto.Hello;
import org.springframework.stereotype.Service;

public interface HelloService {
    String hello(Hello hello);
}
