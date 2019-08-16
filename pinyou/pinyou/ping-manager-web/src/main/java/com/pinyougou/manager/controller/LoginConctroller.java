package com.pinyougou.manager.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginConctroller {

    @RequestMapping("/name")
    public Map name(){
        //获取登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //创建map集合用于装载登录用户名并返回
        Map map = new HashMap();
        map.put("loginName",name);
        return map;
    }

}
