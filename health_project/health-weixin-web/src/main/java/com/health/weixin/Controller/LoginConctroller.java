package com.health.weixin.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.RedisMessageConstant;
import com.health.pojo.Member;
import com.health.pojo.Result;
import com.health.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginConctroller {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private LoginService loginService;

    @RequestMapping("/check")
    /**
     * 用户登录
     * @param map
     * @Return:
     */
    public Result check(@RequestBody Map map) {
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        Member member = loginService.login(telephone);
        if (member == null) {
            return new Result(false, "你还没有注册");
        }
        String code = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if (!validateCode.equals(code)) {
            return new Result(false, "验证码错误");
        }
        return new Result(true, "登陆成功");
    }

}
