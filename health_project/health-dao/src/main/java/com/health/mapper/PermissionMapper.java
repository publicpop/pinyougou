package com.health.mapper;

import com.health.pojo.Permission;

import java.util.Set;

public interface PermissionMapper {

    Set<Permission> findPermissionByRoleId(Integer id);

}