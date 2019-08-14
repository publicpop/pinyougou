package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.MemberDao;
import com.health.pojo.Member;
import com.health.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
/**
 * 用户登录
 * @param
 * @Return:
 */
public class LoginServiceImpl implements LoginService{


    @Autowired
    private MemberDao memberDao;

    /**
     * 用户登录
     * @param telephone
     * @Return:
     */
    public Member login(String telephone) {

        return memberDao.findByTelephone(telephone);
    }

}
