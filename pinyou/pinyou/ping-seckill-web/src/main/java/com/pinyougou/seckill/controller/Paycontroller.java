package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class Paycontroller {

    @Reference(timeout = 10000)
    private WeiXinPayService weiXinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    /**
     * 查询redis中订单信息
     * @param userId
     * @Return:com.pinyougou.pojo.TbSeckillOrder
     */
    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取秒杀商品订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);

        //判断该订单是否存在
        if(seckillOrder!=null){
            long money = (long) (seckillOrder.getMoney().doubleValue() * 100);
            return weiXinPayService.createNative(seckillOrder.getId()+"",money+"");
        }else {
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
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //返回结果
        Result result = null;
        int x = 0;
        while (true) {
            //获取查询结果
            Map map = weiXinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("return_msg").equals("SUCCESS")) {//equals("SUCCESS"),测试待改善
                result = new Result(true, "支付成功");
                //修改订单状态并持久化
                seckillOrderService.saveorderFromRedisToDb(userId,Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //设置二维码超时
            x++;
            if (x >= 5) {
                result = new Result(false, "二维码过期");
                //关闭订单
                Map closePay = weiXinPayService.closePay(out_trade_no);
                //如果订单正常关闭
                if(!"SUCCESS".equals(closePay.get("result_code"))){
                    if("ORDERPAID".equals(closePay.get("err_code"))){
                        result  =new Result(true,"支付成功");
                        //修改订单状态并持久化
                        seckillOrderService.saveorderFromRedisToDb(userId,Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                    }
                }

                if(result.isSuccess()==false){
                    //删除失效订单
                    seckillOrderService.deleteorderByredis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return result;
    }

}
