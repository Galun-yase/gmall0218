package com.atguigu.gmall0218.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

//@EnableDubbo
@SpringBootApplication
@EnableTransactionManagement//开启事务
@ComponentScan(basePackages = "com.atguigu.gmall0218")//扫描组件，主要是扫描redisUtil
@MapperScan(basePackages = "com.atguigu.gmall0218")//扫描mapper
public class GmallOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOrderServiceApplication.class, args);
    }

}
