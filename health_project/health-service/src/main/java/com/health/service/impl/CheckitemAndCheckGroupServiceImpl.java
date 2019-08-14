package com.health.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.health.mapper.CheckitemAndCheckGroup;
import com.health.service.CheckitemAndCheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CheckitemAndCheckGroupServiceImpl implements CheckitemAndCheckGroupService {

    @Autowired
    private CheckitemAndCheckGroup checkitemAndCheckGroup;

    @Override
    /**
    * 根据grouId查询checkItemid
    * @param id
    * @Return:java.util.List<java.lang.Integer>
    */
    public List<Integer> searchCheckitemIdByCheckGroupId(Integer id) {
        return checkitemAndCheckGroup.searchCheckitemIdByCheckGroupId(id);
    }


    @Override
    /**
    * 根据grouId删除checkItemid
    * @param id
    * @Return:void
    */
    public void deleteCheckitemIdByCheckGroupId(Integer id) {
        checkitemAndCheckGroup.deleteCheckitemIdByCheckGroupId(id);
    }





    @Override
    /**
    * 保存检查项与检查组对应id
    * @param checkItemid
    * @param checkGroupid
    * @Return:void
    */
    public void saveCheckItemIdAndCheckGroupId(Integer checkGroupid,Integer checkItemid) {
        checkitemAndCheckGroup.saveCheckItemIdAndCheckGroupId(checkGroupid,checkItemid); //注意xml形参非对象时只能是{arg0,arg1,param1,param2}这些!!!!
    }
}
