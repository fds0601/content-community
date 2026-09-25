package com.example.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.community.mapper")
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
        System.out.println("=====================================");
        System.out.println("  内容社区后端启动成功！");
        System.out.println("  接口文档: http://localhost:8080/doc.html");
        System.out.println("=====================================");
    }
}
