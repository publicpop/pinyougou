package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.MenuMapper;
import com.health.pojo.Menu;
import com.health.pojo.Role;
import com.health.pojo.User;
import com.health.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class MenuServiceImpl implements MenuService {


    @Autowired
    private MenuMapper menuMapper;

    @Override
    /**
     * 查询用户可用的下拉菜单
     * @param user
     * @Return:
     */
    public List<Menu> findMenus(User user) {
        //获取用户对应角色
        Set<Role> roles = user.getRoles();
        //创建set集合,用于去重
        Set<Menu> set = new HashSet<>();
        //获取角色对应的父级Id
        for (Role role : roles) {
            Set<Menu> menus = menuMapper.findPMenuById(role.getId());
            set.addAll(menus);
        }
        //创建List集合用于用于存储实际发送数据
        List<Menu> list = new ArrayList<>();
        //根据父级id查询子集id,并存入集合
        for (Menu menu : set) {
            List<Menu> cmenu = menuMapper.findCmenuByPid(menu.getId());
            menu.setChildren(cmenu);
            list.add(menu);
        }
        //存储对象的list集合collection排序,其对象必须实现comparable接口,不实现,则会报错,实现接口,则当前排序值-形参,为升序,反之为降序***
        Collections.sort(list);
        return list;
    }


}
