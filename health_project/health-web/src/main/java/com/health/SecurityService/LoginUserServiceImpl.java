package com.health.SecurityService;

import com.health.pojo.Permission;
import com.health.pojo.Role;
import com.health.service.LoginUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 用户权限查询
 * @param
 * @Return:
 */
public class LoginUserServiceImpl implements UserDetailsService {

    private LoginUserService loginUserService;

    public void setLoginUserService(LoginUserService loginUserService) {
        this.loginUserService = loginUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户
        com.health.pojo.User user = loginUserService.findUserByUserName(username);
        //如果用户不存在,则返回空
        if(user==null){
            return null;
        }
        //创建授权集合
        List<GrantedAuthority> list = new ArrayList<>();
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                //向授权集合中添加授权时,必须创建授权的实现类***
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        //返回用户的授权信息
        UserDetails userdetails = new User(username, user.getPassword(), list);
        return userdetails;
    }
}
