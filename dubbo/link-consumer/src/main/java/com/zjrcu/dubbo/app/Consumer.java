package com.zjrcu.dubbo.app;

import com.zjrcu.dubbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    @Autowired
    private UserService userService;
    public static void main(String[] args) {
        ApplicationContext context= new ClassPathXmlApplicationContext("spring-config.xml");
        Consumer app=context.getBean(Consumer.class);
        System.out.println(app.userService.getUserById(3).getName());
    }
}
