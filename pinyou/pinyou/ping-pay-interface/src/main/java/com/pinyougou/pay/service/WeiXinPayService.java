package com.pinyougou.pay.service;

import java.util.Map;

public interface WeiXinPayService {


    /**
    * 生成支付二维码
    * @param
    * @Return:
    */
     Map createNative(String out_trade_no,String total_fee);

     /**
     * 查询用户支付状态
     * @param
     * @Return:
     */
     Map queryPayStatus(String out_trade_no);

     /**
     * 超时关闭支付
     * @param out_trade_no
     * @Return:
     */
     Map closePay(String out_trade_no);
}
