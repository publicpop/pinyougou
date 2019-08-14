package com.health.mapper;

import com.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约Dao
 */
public interface OrderSettingMapper {
    /**
     * 保存预约设置
     *
     * @param orderSetting
     * @Return:
     */
    void add(OrderSetting orderSetting);

    /**
     * 更新预约设置
     *
     * @param orderSetting
     * @Return:
     */
    void update(OrderSetting orderSetting);

    /**
     * 查询预约设置是否存在
     *
     * @param orderDate
     * @Return:
     */
    Long findCountByOrderDate(Date orderDate);

    /**
     * 查询当月的预约设置信息
     *
     * @param date
     * @Return:
     */
    List<OrderSetting> findOrderSettingByMonth(String date);

    /**
     * 根据日期查询当天的预约设置
     * @param date
     * @Return:
     */
    OrderSetting findOrderSettingByDate(Date date);


    /**
     * 查询可预约时间
     * @param date
     * @Return:
     */
    List<String> selectDate(String date);

    /**
    * 查询设置预约人数
    * @param date
    * @Return:
    */
    Integer findByNumber(String date);

    /**
     * 查询已预约人数
     * @param date
     * @Return:
     */
    Integer findByReservations(String date);
}
