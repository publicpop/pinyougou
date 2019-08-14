package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.health.constant.DateUtils;
import com.health.mapper.MemberDao;
import com.health.mapper.OrderDao;
import com.health.mapper.TSetmealMapper;
import com.health.pojo.Result;
import com.health.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TSetmealMapper setmealMapper;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    /**
     * 查询套餐与订单量的关系
     * @param
     * @Return:
     */
    public List<Map<String, Object>> findSemealAndOrderCount() {
        return setmealMapper.findSemealAndOrderCount();
    }


    @Override
    /**
     * 查询平台统计数据
     * @param
     * @Return:
     */
    public Map<String, Object> getBusinessReport() throws Exception {

        //获取当前日期
        String today = DateUtils.parseDate2String(DateUtils.getToday());

        //获取本周一的日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());

        //获取本月的日期的第一天
        String firstDay4Month = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        //今日新增会员shu
        Integer todayNewMember = memberDao.findMemberCountByDate(today);

        //会员总数
        Integer totalMember = memberDao.findMemberTotalCount();

        //本周新增会员数量
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);

        //本月新增会员
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4Month);

        //今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);

        //本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(thisWeekMonday);

        //本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(firstDay4Month);

        //今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(today);

        //本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(thisWeekMonday);

        //本月到诊数
        Integer thisMonthVisitsNUmber = orderDao.findVisitsCountAfterDate(firstDay4Month);

        //热门套餐
        List<Map> hotSetmeal = orderDao.findHotSetmeal();

        //创建集合,储存上述数据
        Map<String, Object> map = new HashMap<>();
        map.put("reportDate", today);
        map.put("todayNewMember", todayNewMember);
        map.put("totalMember", totalMember);
        map.put("thisWeekNewMember", thisWeekNewMember);
        map.put("thisMonthNewMember", thisMonthNewMember);
        map.put("todayOrderNumber", todayOrderNumber);
        map.put("thisWeekOrderNumber", thisWeekOrderNumber);
        map.put("thisMonthOrderNumber", thisMonthOrderNumber);
        map.put("todayVisitsNumber", todayVisitsNumber);
        map.put("thisWeekVisitsNumber", thisWeekVisitsNumber);
        map.put("thisMonthVisitsNumber", thisMonthVisitsNUmber);
        map.put("hotSetmeal", hotSetmeal);

        return map;
    }

}
