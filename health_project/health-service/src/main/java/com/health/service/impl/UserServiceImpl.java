package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.UserMapper;
import com.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    /**
     * 根据时间查询会员数量
     * @param date
     * @Return:
     */
    public List<Integer> findUserByDate(List<String> date) {
        //查询当前12个月的会员数据
        List<Integer> list = new ArrayList<>();
        for (String months : date) {
            Integer count = userMapper.findUserByDate(months + "%");
            list.add(count);
        }
        return list;
    }
}
