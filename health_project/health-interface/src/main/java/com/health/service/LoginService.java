package com.health.service;

import com.health.pojo.Member;

public interface LoginService {


    /**
     * 用户登录
     * @param telephone
     * @Return:
     */
    Member login(String telephone);
}
