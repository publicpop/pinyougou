package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.UserMapper;
import com.health.pojo.User;
import com.health.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@Service
/**
 * 用户注册
 * @param null
 * @Return:
 */
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper;

    @Override
    /**
     * 用户注册
     * @param user
     * @Return:
     */
    public void register(Map map) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encode);
        userMapper.register(user);
        userMapper.addUserIdAndRoleId(user.getId(), 2);
    }
}
