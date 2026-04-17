package org.example.caffe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CaffeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaffeApplication.class, args);
    }

}
