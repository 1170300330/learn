package com.zjrcu.dubbo.service.impl;

import com.zjrcu.dubbo.model.User;
import com.zjrcu.dubbo.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Integer id) {
        User user = new User();
        user.setId(id);
        user.setName("userid:" + id);
        return user;
    }
}
