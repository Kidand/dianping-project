package com.kidand.dianping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kidand.dianping"})
public class DianpingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DianpingApplication.class,args);
    }
}
