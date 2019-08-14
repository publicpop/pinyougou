package com.health.mapper;


import java.util.List;

public interface SetmealAndCheckGroup {

    /**
     * 根据grouId查询checkItemid
     *
     * @param id
     * @Return:
     */
    List<Integer> searchSetmealIdByCheckGroupId(Integer id);


    /**
     * 根据grouId删除checkItemid
     *
     * @param id
     * @Return:void
     */
    void deleteSetmealIdByCheckGroupId(Integer id);


    /**
     * 保存检查项与检查组对应id
     *
     * @param setmealId
     * @param checkGroupid
     * @Return:
     */
    void saveSetmealIdAndCheckGroupId(Integer checkGroupid, Integer setmealId);

}
