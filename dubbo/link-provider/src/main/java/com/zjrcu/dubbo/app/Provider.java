package com.zjrcu.dubbo.app;

import com.zjrcu.dubbo.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context=new ClassPathXmlApplicationContext("dubbo-link-provider.xml");
        UserService userService=context.getBean(UserService.class);
        Thread.sleep(30000000);
    }
}
