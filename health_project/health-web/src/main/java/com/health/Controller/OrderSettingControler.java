package com.health.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.constant.POIUtils;
import com.health.pojo.OrderSetting;
import com.health.pojo.Result;
import com.health.service.OrderSettingService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ordersetting")
public class OrderSettingControler {

    @Reference
    private OrderSettingService orderSettingService;

    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile")MultipartFile excelFile){

        try {
            //获取表格中单行数据(每行都看作成一个数组)
            List<String[]> excel = POIUtils.readExcel(excelFile);
            //如果表格行不为空
            if(excel!=null&&excel.size()>0){
                //创建预约集合
                List<OrderSetting> orderSttingList = new ArrayList<>();
                //遍历表格集合
                for (String[] strings : excel) {
                    //创建预约设置,并添加表格中的行数据
                    OrderSetting orderSetting = new OrderSetting(new Date(strings[0]), Integer.parseInt(strings[1]));
                    //将上述中的预约添加预约集合中
                    orderSttingList.add(orderSetting);
                }
                //保存预约设置
                orderSettingService.add(orderSttingList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
        return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
    }

    @RequestMapping("/findOrderSettingByMonth")
    /**
     * 查询当月预约详情
     *
     * @param data
     * @Return:
     */
    public Result findOrderSettingByMonth(String date){
        try {
            List<Map> month = orderSettingService.findOrderSettingByMonth(date);
            return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,month);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

}
