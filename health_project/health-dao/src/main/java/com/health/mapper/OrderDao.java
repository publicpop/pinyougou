package com.health.mapper;

import com.health.pojo.Order;
import com.health.pojo.PageResult;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    void add(Order order);

    List<Order> findByCondition(Order order);

    Order findById(Integer id);

    Map findById4Detail(Integer id);

    Integer findOrderCountByDate(String date);

    Integer findOrderCountAfterDate(String date);

    Integer findVisitsCountByDate(String date);

    Integer findVisitsCountAfterDate(String date);

    List<Map> findHotSetmeal();

    List<Map> searchTerm(Map map);

    /**
     * 根据套餐id与会员id查询预约
     *
     * @param sid
     * @param mid
     * @Return:
     */
    Map findOrderByMidAndSid(Integer sid, Integer mid);

    /**
     * 条件删除预约信息
     *
     * @param sid
     * @param mid
     * @Return:
     */
    void delete(Integer sid, Integer mid);

    /**
    * 更改订单数据
    * @param order
    * @Return:
    */
    void update(Order order);
}
