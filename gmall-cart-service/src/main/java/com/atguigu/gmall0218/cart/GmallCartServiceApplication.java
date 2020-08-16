package com.atguigu.gmall0218.cart;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
@ComponentScan(basePackages = "com.atguigu.gmall0218")//扫描组件，主要是扫描redisUtil
@MapperScan(basePackages = "com.atguigu.gmall0218")//扫描mapper
public class GmallCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallCartServiceApplication.class, args);
    }

}
