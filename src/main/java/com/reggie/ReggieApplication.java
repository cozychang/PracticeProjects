package com.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan //扫描web注解，创建过滤器
@EnableTransactionManagement//开启事物注解
@EnableCaching//开启cache缓存
public class ReggieApplication {




    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);

    }
}
