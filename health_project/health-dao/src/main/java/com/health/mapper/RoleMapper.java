package com.health.mapper;


import com.health.pojo.Role;

import java.util.Set;

public interface RoleMapper {

    Set<Role> findRoleByUserId(Integer id);
}