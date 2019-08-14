package com.health.service;

import com.health.pojo.Menu;
import com.health.pojo.User;

import java.util.List;

public interface MenuService {

    /**
     * 查询用户可用的下拉菜单
     * @param user
     * @Return:
     */
    public List<Menu> findMenus(User user);
}
