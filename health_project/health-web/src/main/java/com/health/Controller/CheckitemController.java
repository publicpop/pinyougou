package com.health.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.pojo.CheckItem;
import com.health.pojo.PageResult;
import com.health.pojo.QueryPagebean;
import com.health.pojo.Result;
import com.health.service.CheckitemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("checkItem")
public class CheckitemController {

    @Reference
    private CheckitemService checkitemService;

    /**
     * 使用@PreAuthorize注解权限管理时,不得使用私用方法,否则会导致dubbo无法注入的问题!!***
     *
     * 返回全部列表
     * @return
     */
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    @RequestMapping("/findAll")
    public List<CheckItem> findAll(){
        return checkitemService.findAll();
    }

    /**
     * 使用@PreAuthorize注解权限管理时,不得使用私用方法,否则会导致dubbo无法注入的问题!!***
     *
     * 增加
     * @param checkitem
     * @returnc
     */
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkitem){
        try {
            checkitemService.add(checkitem);
            return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_CHECKGROUP_FAIL);
        }
    }

    /**
     * 使用@PreAuthorize注解权限管理时,不得使用私用方法,否则会导致dubbo无法注入的问题!!***
     *
     *
     * 修改
     * @param checkitem
     * @return
     */
    @PreAuthorize("hasAuthority('CHECKITEM_UPDATE')")
    @RequestMapping("/update")
    public Result update(@RequestBody CheckItem checkitem){
        try {
            checkitemService.update(checkitem);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    @RequestMapping("/findOne")
    public CheckItem findOne(Integer id){
        return checkitemService.findOne(id);
    }

    /**
     * 使用@PreAuthorize注解权限管理时,不得使用私用方法,否则会导致dubbo无法注入的问题!!***
     *
     *
     * 批量删除
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try {
            checkitemService.delete(id);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 使用@PreAuthorize注解权限管理时,不得使用私用方法,否则会导致dubbo无法注入的问题!!***
     *
     * 查询+分页
     * @param bean
     * @return
     */
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")
    @RequestMapping("/search")
    public PageResult search(@RequestBody QueryPagebean<Map<String,String>> bean){
        return checkitemService.findPage(bean.getQueryString(), bean.getCurrentPage(), bean.getPageSize());
    }
}
