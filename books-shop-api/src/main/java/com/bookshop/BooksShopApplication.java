package com.bookshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@MapperScan("com.bookshop.mapper")
@Validated  // 启用方法级校验（校验 @PathVariable）
@EnableCaching //启用缓存
public class BooksShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooksShopApplication.class, args);
    }
}
