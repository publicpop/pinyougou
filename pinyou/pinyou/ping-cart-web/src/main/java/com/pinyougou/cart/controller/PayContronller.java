package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayContronller {//通用其他支付方式

    @Reference
    private WeiXinPayService weiXinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 生成二维码
     *
     * @param
     * @Return:
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户登录名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //redis中获取paylog
        TbPayLog payLog = orderService.findPaylogByredis(userId);
        //判断redis中log是否存在
        if (payLog != null && "".equals(payLog)) {
            return weiXinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");  //total_fee待改善
        } else {
            return new HashMap();
        }
    }


    @RequestMapping("/queryPayStatus")
    /**
     * 查询用户支付状态
     * @param out_trade_no
     * @Return:entity.Result
     */
    public Result queryPayStatus(String out_trade_no) {
        //获取查询结果
        Map map = weiXinPayService.queryPayStatus(out_trade_no);
        //返回结果
        Result result = null;
        int x = 0;
        while (true) {
            if (map == null) {
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("return_msg").equals("签名错误")) {//equals("SUCCESS"),测试待改善
                result = new Result(true, "支付成功");
                //修改订单状态
                orderService.updateByOrderStatus(out_trade_no, (String) map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //设置二维码超时
            x++;
            if (x >= 100) {
                result = new Result(false, "二维码过期");
                break;
            }

        }

        return result;
    }
}