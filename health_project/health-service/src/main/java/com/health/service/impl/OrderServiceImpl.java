package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.constant.DateUtils;
import com.health.constant.MessageConstant;
import com.health.mapper.*;
import com.health.pojo.*;
import com.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingMapper orderSettingMapper;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private TSetmealMapper setmealMapper;

    @Autowired
    private MemberDao memberDao;

    @Override
    /**
     * 添加订单信息
     * @param map
     * @Return:
     */
    public Result add(Map map) throws Exception {
        //校验当前日期是否有预约设置
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting orderSetting = orderSettingMapper.findOrderSettingByDate(date);
        //如果预约设置不能存在则不能预约
        if (orderSetting == null) {
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //校验当前预约日期是否已满
        int number = orderSetting.getNumber();
        int reservations = orderSetting.getReservations();
        if (reservations >= number) {
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //校验用户是否是会员
        String telephone = (String) map.get("telephone");
        //根据电话号码查询会员是否存在
        Member member = memberDao.findByTelephone(telephone);
        //如果会员存在
        if (member != null) {
            Integer memberId = member.getId();
            Integer setmealId = Integer.parseInt((String) map.get("setmealId"));
            //新建订单信息
            Order order = new Order(memberId, date, null, null, setmealId);
            //根据带参订单信息查询订单
            List<Order> orders = orderDao.findByCondition(order);
            //如果订单集合信息不为空,则不能预约
            if (orders != null && orders.size() > 0) {
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }
        //创建会员对象
        Member newMember = new Member();
        //如果不是会员,则进行注册
        if (member == null) {
            newMember.setIdCard((String) map.get("idCard"));
            newMember.setName((String) map.get("name"));
            newMember.setPhoneNumber(telephone);
            newMember.setSex((String) map.get("sex"));
            newMember.setRegTime(new Date());
            memberDao.add(newMember);
        }
        //保存订单信息
        Order order = new Order(newMember.getId(), date, (String) map.get("orderType"), Order.ORDERSTATUS_NO, Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);
        //预约人数+1
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingMapper.update(orderSetting);
        return new Result(true, MessageConstant.ORDER_SUCCESS, order.getId());
    }


    @Override
    /**
     * 获取订单信息
     * @param id
     * @Return:
     */
    public Map getorderInfo(Integer id) {
        Map<String, Object> map = new HashMap<>();
        Order order = orderDao.findById(id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        map.put("orderDate", format.format(order.getOrderDate()));
        map.put("orderType", order.getOrderType());
        Member member = memberDao.findById(order.getMemberId());
        map.put("member", member.getName());
        Setmeal setmeal = setmealMapper.selectByPrimaryKey(order.getSetmealId());
        map.put("setmeal", setmeal.getName());
        return map;
    }


    @Override
    /**
     * 条件查询+分页
     * @param List
     * @param pageNum
     * @param pageSize
     * @Return:com.health.pojo.PageResult
     */
    public PageResult findPage(Map map, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Map> page = (Page<Map>) orderDao.searchTerm(map);
        List<Map> result = page.getResult();
        for (Map map1 : result) {
            Integer setmealId = (Integer) map1.get("setmealId");
            Setmeal setmeal = setmealMapper.selectByPrimaryKey(setmealId);
            map1.put("setmealName", setmeal.getName());
        }
        return new PageResult(page.getTotal(), result);
    }


    @Override
    /**
     * 查询套餐及可预约时间
     * @param
     * @Return:
     */
    public Map findDateAndSetmealName() {
        //获取当前时间
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //获取setmealName集合
        List<String> list = setmealMapper.selectSetmealName();
        //装载前端可使用的数据
        List<Map> names = new ArrayList<>();
        for (String s : list) {
            Map<String, String> map = new HashMap<>();
            map.put("setmealName", s);
            names.add(map);
        }
        //获取预约时间集合
        List<String> date = orderSettingMapper.selectDate(format);
        //操作集合
        List<String> dates = orderSettingMapper.selectDate(format);
        //获取迭代器  ///可使用并发集合解决问题(currentHashMap,CopyOnArrayList)
        Iterator<String> iterator = date.iterator();
        while (iterator.hasNext()) {
            //获取迭代到的元素
            String date1 = iterator.next();
            //查询当前日期设置预约数
            Integer number = orderSettingMapper.findByNumber(date1);
            //查询当前时间已预约数
            Integer reservations = orderSettingMapper.findByReservations(date1);
            //如果预约数量大于等于设置预约数,则删除此时间
            if (reservations >= number) {
                dates.remove(date1);
            }
        }
        //装载前端可使用的数据
        List<Map> date2 = new ArrayList<>();
        for (String s : dates) {
            Map<String, String> map = new HashMap<>();
            map.put("orderDate", s);
            date2.add(map);
        }
        //返回数据
        Map<String, List> map = new HashMap<>();
        map.put("setsealNames", names);
        map.put("dates", date2);
        return map;
    }

    @Override
    /**
     * 查询单个预约信息
     * @param username
     * @param telephone
     * @Return:
     */
    public Map findOne(String setmealName, String telephone) {
        //获取套餐
        Setmeal setmeal = setmealMapper.findSetmealBySetmealName(setmealName);
        //获取会员
        Member member = memberDao.findMemberByTelephone(telephone);
        //获取预约详情
        Map order = orderDao.findOrderByMidAndSid(setmeal.getId(), member.getId());
        Map<String, String> map = new HashMap<>();
        map.put("username", member.getName());
        map.put("telephone", member.getPhoneNumber());
        map.put("setmealName", setmeal.getName());
        map.put("usersex", member.getSex());
        map.put("orderDate", (String) order.get("orderDate"));
        map.put("orderStatus", (String) order.get("orderStatus"));
        return map;
    }


    @Override
    /**
     * 删除预约
     * @param setmealName
     * @param telephone
     * @Return:
     */
    public void delete(String setmealName, String telephone) {
        //获取套餐
        Setmeal setmeal = setmealMapper.findSetmealBySetmealName(setmealName);
        //获取会员
        Member member = memberDao.findMemberByTelephone(telephone);
        //根据套餐及会员id删除预约
        orderDao.delete(setmeal.getId(), member.getId());
    }


    @Override
    /**
     * 电话添加订单
     * @param map
     * @Return:
     */
    public Result save(Map map) throws Exception {
        String username = (String) map.get("username");
        String setmealName = (String) map.get("setmealName");
        String usersex = (String) map.get("usersex");
        String orderDate = (String) map.get("orderDate");
        String telephone = (String) map.get("telephone");
        //校验当前日期是否有预约设置
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting orderSetting = orderSettingMapper.findOrderSettingByDate(date);
        //校验用户是否是会员
        //根据电话号码查询会员是否存在
        Member member = memberDao.findByTelephone(telephone);
        //根据套餐名查询套餐
        Setmeal setmeal = setmealMapper.findSetmealBySetmealName(setmealName);
        //如果会员存在
        if (member != null) {
            Integer memberId = member.getId();
            Integer setmealId = setmeal.getId();
            //新建订单信息
            Order order = new Order(memberId, date, null, null, setmealId);
            //根据带参订单信息查询订单
            List<Order> orders = orderDao.findByCondition(order);
            //如果订单集合信息不为空,则不能预约
            if (orders != null && orders.size() > 0) {
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }
        //创建会员对象
        Member newMember = new Member();
        //如果不是会员,则进行注册
        if (member == null) {
            newMember.setName(username);
            newMember.setPhoneNumber(telephone);
            newMember.setSex(usersex);
            newMember.setRegTime(new Date());
            memberDao.add(newMember);
        }
        //保存订单信息
        Order order = new Order(newMember.getId(), date, Order.ORDERTYPE_TELEPHONE, Order.ORDERSTATUS_NO, setmeal.getId());
        orderDao.add(order);
        //预约人数+1
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingMapper.update(orderSetting);
        return new Result(true, MessageConstant.ORDER_SUCCESS, order.getId());
    }

    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 修改订单时间及是否到诊
     *
     * @param map
     * @Return:
     */
    public void update(Map map) throws Exception {
        String setmealName = (String) map.get("setmealName");
        String orderDate = (String) map.get("orderDate");
        String orderStatus = (String) map.get("orderStatus");
        String telephone = (String) map.get("telephone");
        //获取预约设置
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting orderSetting = orderSettingMapper.findOrderSettingByDate(date);
        //根据电话号码查询会员
        Member member = memberDao.findByTelephone(telephone);
        //根据套餐名查询套餐
        Setmeal setmeal = setmealMapper.findSetmealBySetmealName(setmealName);
        //查询订单信息
        Map orderMap = orderDao.findOrderByMidAndSid(setmeal.getId(), member.getId());
        //取出参数
        String orderDate1 = (String) orderMap.get("orderDate");
        Date date1 = DateUtils.parseString2Date(orderDate1);
        String orderStatus1 = (String) orderMap.get("orderStatus");
        //获取旧yuyue设置
        OrderSetting setting = orderSettingMapper.findOrderSettingByDate(date1);
        if (orderDate.equals(orderDate1) && !orderStatus.equals(orderStatus1)) {//如果预约时间没改变,到诊状态改变,则修改状态
            //保存订单信息
            Order order = new Order(member.getId(), date, null, orderStatus, setmeal.getId());
            orderDao.update(order);
        } else if (!orderDate.equals(orderDate1) && orderStatus.equals(orderStatus1)) {//如果预约时间改变,到诊状态没改变
            //旧预约人数-1
            setting.setReservations(setting.getReservations() - 1);
            orderSettingDao.editReservationsByOrderDate(setting);
            //保存订单信息
            Order order = new Order(member.getId(), date, null, orderStatus, setmeal.getId());
            orderDao.update(order);
            //预约人数+1
            orderSetting.setReservations(orderSetting.getReservations() + 1);
            orderSettingDao.editReservationsByOrderDate(orderSetting);
        } else {  //都改变
            //旧预约人数-1
            setting.setReservations(setting.getReservations() - 1);
            orderSettingDao.editReservationsByOrderDate(setting);
            //保存订单信息
            Order order = new Order(member.getId(), date, null, orderStatus, setmeal.getId());
            orderDao.update(order);
            //预约人数+1
            orderSetting.setReservations(orderSetting.getReservations() + 1);
            orderSettingDao.editReservationsByOrderDate(orderSetting);
        }
    }
}
