package com.health.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.pojo.PageResult;
import com.health.pojo.QueryPagebean;
import com.health.pojo.Result;
import com.health.service.OrderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("order")
public class orderCntroller {

    @Reference
    private OrderService orderService;

    /**
     * 查询+分页
     *
     * @param bean
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody QueryPagebean<Map> bean) {
        return orderService.findPage(bean.getQueryString(), bean.getCurrentPage(), bean.getPageSize());
    }

    /**
     * 查询套餐及可预约时间
     *
     * @param
     * @Return:
     */
    @RequestMapping("/findDateAndSetmealName")
    public Map findDateAndSetmealName() {
       Map map = orderService.findDateAndSetmealName();
       return map;
    }

    @RequestMapping("/findOne")
    /**
    * 查询单个预约信息
    * @param  username
    * @param  telephone
    * @Return:
    */
    public Map findOne(String setmealName,String telephone){
        return orderService.findOne(setmealName,telephone);
    }

    @RequestMapping("/delete")
    /**
    * 删除预约
    * @param setmealName
    * @param telephone
    * @Return:
    */
    public Result delete(String setmealName,String telephone){
        try {
            orderService.delete(setmealName,telephone);
            return  new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"删除失败");
        }
    }


    @RequestMapping("/add")
    /**
    * 添加订单数据
    * @param map
    * @Return:
    */
    public Result add(@RequestBody Map map){
        try {
            Result result = orderService.save(map);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加预约失败");
        }
    }


    @RequestMapping("/update")
    /**
    * 修改订单数据
    * @param map
    * @Return:
    */
    public Result update(@RequestBody Map map){
        try {
            orderService.update(map);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

}
