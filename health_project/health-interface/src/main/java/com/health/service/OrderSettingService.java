package com.health.service;

import com.health.pojo.OrderSetting;

import java.util.List;
import java.util.Map;


public interface OrderSettingService {
    /**
     * 保存预约设置
     *
     * @param orderSttingList
     * @Return:
     */
    void add(List<OrderSetting> orderSttingList);

    /**
     * 获取当月预约设置详情
     *
     * @param date
     * @Return:
     */
    List<Map> findOrderSettingByMonth(String date);


    /**
     * 修改预约人数
     *
     * @param orderSetting
     * @Return:
     */
    void editNumberByDate(OrderSetting orderSetting);
}
