package com.health.mapper;

import com.health.pojo.Menu;

import java.util.List;
import java.util.Set;

public interface MenuMapper {

    /**
    * 查询父级菜单
    * @param id
    * @Return:
    */
    Set<Menu> findPMenuById(Integer id);

    /**
    * 根据父级id查询子菜单
    * @param pid
    * @Return:
    */
    List<Menu> findCmenuByPid(Integer pid);

}
