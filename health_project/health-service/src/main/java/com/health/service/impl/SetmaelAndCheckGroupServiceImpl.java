package com.health.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.SetmealAndCheckGroup;
import com.health.service.SetmealAndCheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SetmaelAndCheckGroupServiceImpl implements SetmealAndCheckGroupService {

    @Autowired
    private SetmealAndCheckGroup setmaelAndCheckGroup;

    @Override
    /**
    * 根据SetmealId查询checkGroupid
    * @param id
    * @Return:java.util.List<java.lang.Integer>
    */
    public List<Integer> searchSetmealIdByCheckGroupId(Integer id) {
        return setmaelAndCheckGroup.searchSetmealIdByCheckGroupId(id);
    }


    @Override
    /**
    * 根据SetmealId删除checkGroupid
    * @param id
    * @Return:void
    */
    public void deleteSetmealIdByCheckGroupId(Integer id) {
        setmaelAndCheckGroup.deleteSetmealIdByCheckGroupId(id);
    }





    @Override
    /**
    * 保存套餐与检查组对应id
    * @param checkItemid
    * @param checkGroupid
    * @Return:void
    */
    public void saveSetmealIdAndCheckGroupId(Integer setmealid,Integer checkGroupid) {
        setmaelAndCheckGroup.saveSetmealIdAndCheckGroupId(setmealid,checkGroupid); //注意xml形参非对象时只能是{arg0,arg1,param1,param2}这些!!!!
    }
}
