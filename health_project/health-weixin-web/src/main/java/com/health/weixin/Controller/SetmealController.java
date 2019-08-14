package com.health.weixin.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.pojo.CheckGroup;
import com.health.pojo.CheckItem;
import com.health.pojo.Setmeal;
import com.health.service.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/getSetmeal")
    public List<Setmeal> findAll(){
        return setmealService.findAll();
    }

    @Reference
    private SetmealAndCheckGroupService setmealAndCheckGroupService;

    @Reference
    private CheckgroupService checkgroupService;

    @Reference
    private CheckitemService checkitemService;

    @Reference
    private CheckitemAndCheckGroupService checkitemAndCheckGroup;

    @RequestMapping("/findById")
    /**
    * 根据id查询套餐详细信息
    * @param id
    * @Return:
    */
        public Setmeal findOne(Integer id){
        //获取检查组的id
        List<Integer> checkGroupIds = setmealAndCheckGroupService.searchSetmealIdByCheckGroupId(id);//获取套餐对应的检查组id
        Setmeal setmeal = setmealService.findOne(id);//获取套餐信息
        List<CheckGroup> checkGroups = new ArrayList<>();
        //查询检查组并将其添加至检查组集合
        for (Integer checkGroupId : checkGroupIds) {
            List<CheckItem> checkItems = new ArrayList<>();
            //获取检查组
            CheckGroup checkGroup = checkgroupService.findOne(checkGroupId);
            //获取当前检查组对应的检查项
            List<Integer> checkitemIds = checkitemAndCheckGroup.searchCheckitemIdByCheckGroupId(checkGroupId);
            for (Integer checkitemId : checkitemIds) {
                //获取检查项
                CheckItem checkItem = checkitemService.findOne(checkitemId);
                checkItems.add(checkItem);
            }
            //添加检查项集合
            checkGroup.setCheckItems(checkItems);
            checkGroups.add(checkGroup);
        }
        //添加检查组集合
        setmeal.setCheckGroups(checkGroups);
        //返回套餐数据
        return setmeal;
    }


}
