package com.health.service;

import java.util.List;

public interface UserService {

    /**
     * 根据时间查询会员数量
     *
     * @param date
     * @Return:
     */
    List<Integer> findUserByDate(List<String> date);
}
