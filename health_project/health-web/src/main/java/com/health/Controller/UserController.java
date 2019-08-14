package com.health.Controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user")
public class UserController {

    @RequestMapping("/findUserName")
    /**
     * 查询登陆用户名
     * @param
     * @Return:
     */
    public String findUserName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return name;
    }


}
