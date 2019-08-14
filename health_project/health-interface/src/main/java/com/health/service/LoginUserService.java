package com.health.service;


import com.health.pojo.User;

public interface LoginUserService {


    /**
     * 根据用户名查询用户
     *
     * @param username
     * @Return:
     */
     User findUserByUserName(String username);

}
