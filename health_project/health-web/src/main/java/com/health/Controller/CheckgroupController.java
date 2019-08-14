package com.health.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.pojo.*;
import com.health.service.CheckgroupService;
import com.health.service.CheckitemAndCheckGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("checkgroup")
public class CheckgroupController {

    @Reference
    private CheckgroupService checkGroupService;

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<CheckGroup> findAll(){
        return checkGroupService.findAll();
    }

    /**
     * 增加
     * @param queryPagebean
     * @returnc
     */
    @RequestMapping("/add")
    public Result add(@RequestBody QueryPagebean<CheckGroup> queryPagebean){   ///形参错误,非对象形参只有[arg0,arg1,param1,param2]这四个!!!**
        try {
            checkGroupService.add(queryPagebean);
            return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_CHECKGROUP_FAIL);
        }
    }

    /**
     * 修改
     * @param queryPagebean
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody QueryPagebean<CheckGroup> queryPagebean){
        try {
            checkGroupService.update(queryPagebean);
            return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }




    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Map findOne(Integer id){
        List<Integer> checkItemIds = searchCheckitemIdByCheckGroupId(id);//获取去检查组对应的检查项id
        CheckGroup checkGroup = checkGroupService.findOne(id);//获取检查组信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("ids",checkItemIds);
        map.put("group",checkGroup);
        return map;


    }

    /**
     * 批量删除
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try {
            checkGroupService.delete(id);
            return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
    }

    /**
     * 查询+分页
     * @param bean
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody QueryPagebean<Map<String,String>> bean){
        return checkGroupService.findPage(bean.getQueryString(), bean.getCurrentPage(), bean.getPageSize());
    }


    @Reference
    private CheckitemAndCheckGroupService checkitemAndCheckGroupService;


    /**
     * 根据grouId查询checkItemid
     * @param id
     * @Return:java.util.List<java.lang.Integer>
     */
    private List<Integer> searchCheckitemIdByCheckGroupId(Integer id){
        List<Integer> checkItemIds = checkitemAndCheckGroupService.searchCheckitemIdByCheckGroupId(id);
        return checkItemIds;
    }

}
