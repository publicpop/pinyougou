package com.health.mapper;


import java.util.List;

public interface CheckitemAndCheckGroup {

    /**
     * 根据grouId查询checkItemid
     *
     * @param id
     * @Return:
     */
    List<Integer> searchCheckitemIdByCheckGroupId(Integer id);


    /**
     * 根据grouId删除checkItemid
     *
     * @param id
     * @Return:void
     */
    void deleteCheckitemIdByCheckGroupId(Integer id);


    /**
     * 保存检查项与检查组对应id
     *
     * @param checkItemid
     * @param checkGroupid
     * @Return:
     */
    void saveCheckItemIdAndCheckGroupId(Integer checkGroupid, Integer checkItemid);

}
