package com.health.mapper;


import com.health.pojo.User;

import java.util.List;

public interface UserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @Return:
     */
    User findUserByUsername(String username);


    /**
     * 用户注册
     *
     * @param user
     * @Return:
     */
    void register(User user);

    /**
     * 保存用户与角色的映射关系
     *
     * @param userId
     * @param roleId
     * @Return:
     */
    void addUserIdAndRoleId(Integer userId, Integer roleId);

    /**
     * 根据时间查询会员数量
     *
     * @param date
     * @Return:
     */
    Integer findUserByDate(String date);

}