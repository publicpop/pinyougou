package com.health.weixin.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.constant.SMSUtils;
import com.health.pojo.Order;
import com.health.pojo.Result;
import com.health.service.OrderService;
import com.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @Reference
    private SetmealService setmealService;

    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map) {
        //获取用户手机号码
        String telephone = (String) map.get("telephone");
        //获取用户存在于redis验证码
        String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        System.out.println(redisCode);
        System.out.println(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //获取用户输入的验证码
        String validateCode = (String) map.get("validateCode");
        System.out.println(validateCode);
        //如果查询不到redis中的验证码或者于虎输入的验证码与redis中的不匹配,则拒绝预约
        if (redisCode == null || !validateCode.equals(redisCode)) {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //将预约数据保存
        Result result = null;
        try {
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            result = orderService.add(map);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        //如果预约成功则发送成功预约短信
        if(result.isFlag()){
            Map<String,String> param = new HashMap<>();
            String orderDate = (String) map.get("orderDate");
            String name = (String) map.get("name");
            param.put("orderDate",orderDate);
            param.put("name",name);
            //发送短信
//            try {
//                SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,param);
//            } catch (ClientException e) {
//                e.printStackTrace();
//            }
        }
        return result;
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Map findOne(Integer id){
        Map<String,String> map = orderService.getorderInfo(id);
        return map;
    }
}
