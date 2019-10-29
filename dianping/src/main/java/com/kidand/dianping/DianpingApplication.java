package com.kidand.dianping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kidand.dianping"})
@MapperScan("com.kidand.dianping.dal")
public class DianpingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DianpingApplication.class,args);
    }
}
