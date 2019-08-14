package com.health.service;

import com.health.pojo.PageResult;
import com.health.pojo.Result;

import java.util.Map;

public interface OrderService {

    /**
     * 添加预约信息
     *
     * @param map
     * @Return:
     */
    Result add(Map map) throws Exception;


    /**
     * 查询订单信息
     *
     * @param id
     * @Return:
     */
    Map<String, String> getorderInfo(Integer id);

    /**
     * 条件查询+分页
     *
     * @param map
     * @param pageNum
     * @param pageSize
     * @Return:com.health.pojo.PageResult
     */
    PageResult findPage(Map map, Integer pageNum, Integer pageSize);

    /**
     * 查询套餐及可预约时间
     *
     * @param
     * @Return:
     */
    Map findDateAndSetmealName();

    /**
     * 查询单个预约信息
     *
     * @param username
     * @param telephone
     * @Return:
     */
    Map findOne(String username, String telephone);

    /**
     * 删除预约
     *
     * @param setmealName
     * @param telephone
     * @Return:
     */
    void delete(String setmealName, String telephone);

    /**
    * 电话预约
    * @param map
    * @Return:
    */
    Result save(Map map) throws Exception;


    /**
    * 修改订单数据
    * @param map
    * @Return:
    */
    void update(Map map) throws Exception;
}
