package com.atguigu.gmall0218.user;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.atguigu.gmall0218")
@MapperScan(basePackages = "com.atguigu.gmall0218.user.mapper")
@ComponentScan(basePackages = "com.atguigu.gmall0218")//扫描组件，以及扫描service-util下的redis
public class GmallUserManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserManageApplication.class, args);
    }

}
