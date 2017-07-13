package org.orh.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ShiroOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(ShiroOauth2Application.class, args);
    }

}
