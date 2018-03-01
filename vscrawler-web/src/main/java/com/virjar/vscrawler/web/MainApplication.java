package com.virjar.vscrawler.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by virjar on 2018/1/23.<br>
 * SpringBoot启动入口<br>
 * 如果需要在idea(一个IDE)运行这个主函数,请开启spring-boot-starter-tomcat的scope依赖,由provided修改为compile<br>
 * 这是因为idea不会将provided的依赖放到ide运行的classpath下面,但是我们这里确实希望spring-boot-starter-tomcat的scope为provided<br>
 * 具体方式为修改web项目的pom文件里面的spring-boot-starter-tomcat的scope
 *
 */
@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MainApplication.class);
    }
}
