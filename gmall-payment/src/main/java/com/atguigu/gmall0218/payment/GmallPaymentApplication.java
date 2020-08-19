package com.atguigu.gmall0218.payment;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDubbo
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall0218")
@MapperScan(basePackages = "com.atguigu.gmall0218.payment.mapper")
public class GmallPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPaymentApplication.class, args);
    }

}
