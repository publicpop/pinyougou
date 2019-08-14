package com.health.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.pojo.Menu;
import com.health.pojo.User;
import com.health.service.LoginUserService;
import com.health.service.MenuService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* 登录相关操作
* @param
* @Return:
*/
@RestController
@RequestMapping("loginMenu")
public class LoginController {

    @Reference
    private LoginUserService loginUserService;

    @Reference
    private MenuService menuService;

    @RequestMapping("/findMenu")
    /**
     * 查询用户可用的菜单信息
     * @param
     * @Return:
     */
    public List<Menu> findMenu() {
        //数据库中User表中username唯一
        //获取当前登陆用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //或当前登录用户
        User user = loginUserService.findUserByUserName(name);
        //查询用户可用的菜单信息
        List<Menu> menus = menuService.findMenus(user);
        return menus;
    }

}
