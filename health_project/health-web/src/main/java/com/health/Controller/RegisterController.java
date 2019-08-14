package com.health.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.pojo.Result;
import com.health.service.RegisterService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Reference
    private RegisterService registerService;

    @RequestMapping("/regist")
    public Result regist(@RequestBody Map map) {

        try {
            registerService.register(map);
            return  new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }

}
