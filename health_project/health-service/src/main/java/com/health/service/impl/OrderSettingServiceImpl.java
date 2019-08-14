package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.OrderSettingMapper;
import com.health.pojo.OrderSetting;
import com.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约设置服务
 */
@Service
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingMapper orderSettingMapper;

    @Override
    /**
     * 保存预约设置
     *
     * @param orderSttingList
     * @Return:
     */
    public void add(List<OrderSetting> orderSttingList) {
        //如果需要操作的集合不为空
        if (orderSttingList != null && orderSttingList.size() > 0) {
            //遍历预约指着集合
            for (OrderSetting orderSetting : orderSttingList) {
                //查询预约设置的时间在数据库中是否存在
                Long count = orderSettingMapper.findCountByOrderDate(orderSetting.getOrderDate());
                //如果预约时间存在则,更新预约设置,如果不存在,则添加此预约设置
                if (count != null) {//***
                    orderSettingMapper.update(orderSetting);
                } else {
                    orderSettingMapper.add(orderSetting);
                }
            }
        }
    }

    @Override
    /**
     * 获取当月预约设置详情
     * 这里做模糊查询
     * @param date
     * @Return:
     */
    public List<Map> findOrderSettingByMonth(String date) {
//        String dateBegin = date + "-1";//2019-3-1
//        String dateEnd = date + "-31";//2019-3-31
//        Map map = new HashMap();
//        map.put("dateBegin",dateBegin);
//        map.put("dateEnd",dateEnd);
//        List<OrderSetting> list = orderSettingMapper.findOrderSettingByMonth(map);
        //以上方法也可实现

        //查询当月的预约设置数据
        List<OrderSetting> settingByMonth = orderSettingMapper.findOrderSettingByMonth(date + "%");
        //创建集合存储需要的当月预约数据
        List<Map> list = new ArrayList<>();
        //将需要的预约数据存入list中
        for (OrderSetting orderSetting : settingByMonth) {
            Map orderMap = new HashMap();
            orderMap.put("date", orderSetting.getOrderDate().getDate());
            orderMap.put("number", orderSetting.getNumber());
            orderMap.put("reservations", orderSetting.getReservations());
            list.add(orderMap);
        }
        return list;
    }

    @Override
    /**
     * 更新预约设置
     * @param orderSetting
     * @Return:
     */
    public void editNumberByDate(OrderSetting orderSetting) {
        //查询数据库中是否有该预约设置对应的日期
        Long count = orderSettingMapper.findCountByOrderDate(orderSetting.getOrderDate());
        //如果有则更新
        if(count!=null){
            orderSettingMapper.update(orderSetting);
        }else {//如果没有则添加
            orderSettingMapper.add(orderSetting);
        }
    }
}
