package com.terran4j.demo.api2doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import com.terran4j.commons.api2doc.config.EnableApi2Doc;
//import com.terran4j.commons.restpack.EnableRestPack;

//  文档访问地址：
//  http://localhost:8080/api2doc/home.html
//  API 元数据
//  http://localhost:8080/api2doc/meta/classes
@EnableApi2Doc
//@EnableRestPack
@SpringBootApplication
@ImportResource(locations={"classpath:spring-security.xml"})
public class Api2DocDemoApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Api2DocDemoApp.class, args);
        //CodeGenerator.genAndroidCode(context);
    }

}