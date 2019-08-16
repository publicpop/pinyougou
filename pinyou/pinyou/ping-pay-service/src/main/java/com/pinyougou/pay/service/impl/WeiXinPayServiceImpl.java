package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeiXinPayService{

   @Value("${appid}")
   private String appid;

   @Value("${partner}")
   private String partner;
    @Value("${partnerkey}")
    private String partnerkey;


    @Override
    /**
    * 生成支付二维码
    * @param out_trade_no
    * @param total_fee
    * @Return:java.util.Map
    */
    public Map createNative(String out_trade_no, String total_fee) {
         //创建参数
        Map<String,String> param = new HashMap<>();
        param.put("appid",appid);//公众号
        param.put("mch_id",partner);//商户id
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字串
        param.put("body","pinyougou");//商品描述
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("total_fee",total_fee);//总金额(分)
        param.put("spbill_create_ip","127.0.0.1");//ip
        param.put("notify_url","http://pinyougou.com");//回调地址
        param.put("trade_type","NATIVE");//交易类型

        try {
            //生成需要发送的xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            //http客户端
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //获取结果
            String content = httpClient.getContent();
            Map<String,String> xml = WXPayUtil.xmlToMap(content);
            System.out.println(xml);
            Map<String,String> map = new HashMap<>();
            map.put("code_url",xml.get("code_url"));
            map.put("total_fee",total_fee);
            map.put("out_trade_no",out_trade_no);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }



    @Override
    /**
    * 查询用户的支付状态
    * @param out_trade_no
    * @Return:java.util.Map
    */
    public Map queryPayStatus(String out_trade_no) {
        //创建参数
        Map<String,String> param = new HashMap<>();
        param.put("appid",appid);//公众号
        param.put("mch_id",partner);//商户id
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随即字串
        param.put("out_trade_no",out_trade_no);//商户订单号
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            //生成需要发送的xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            //http客户端
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //获取结果
            String content = httpClient.getContent();
            //将结果转话为map集合
            Map<String,String> xml = WXPayUtil.xmlToMap(content);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    /**
    * 超时订单关闭
    * @param out_trade_no
    * @Return:java.util.Map
    */
    public Map closePay(String out_trade_no) {

        //创建参数
        Map<String,String> param = new HashMap<>();
        param.put("appid",appid);//公众号
        param.put("mch_id",partner);//商户id
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随即字串
        param.put("out_trade_no",out_trade_no);//商户订单号
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";

        try {
            //生成需要发送的xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            //http客户端
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //获取结果
            String content = httpClient.getContent();
            //将结果转话为map集合
            Map<String,String> xml = WXPayUtil.xmlToMap(content);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }
}
