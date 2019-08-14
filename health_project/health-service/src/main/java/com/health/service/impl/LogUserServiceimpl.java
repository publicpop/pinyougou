package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.PermissionMapper;
import com.health.mapper.RoleMapper;
import com.health.mapper.UserMapper;
import com.health.pojo.Permission;
import com.health.pojo.Role;
import com.health.pojo.User;
import com.health.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Service
public class LogUserServiceimpl implements LoginUserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    /**
     * 根据用户名查询用户
     *
     * @param username
     * @Return:
     */
    public User findUserByUserName(String username) {
        //获取用户信息
        User user = userMapper.findUserByUsername(username);
        //获取用户角色信息
        Set<Role> roles = roleMapper.findRoleByUserId(user.getId());
        //获取用户所对应的权限信息
        for (Role role : roles) {
            Set<Permission> permissions = permissionMapper.findPermissionByRoleId(role.getId());
            //添加角色对应的权限
            role.setPermissions(permissions);
        }
        //添加用户对应的角色
        user.setRoles(roles);
        return user;
    }

}
