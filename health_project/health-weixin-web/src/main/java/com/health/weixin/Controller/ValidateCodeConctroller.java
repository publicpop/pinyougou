package com.health.weixin.Controller;

import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.constant.ValidateCodeUtils;
import com.health.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("validateCode")
public class ValidateCodeConctroller {

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping("/sendOrder")
    /**
    * 发送预约验证码短信
    * @param telephone
    * @Return:
    */
    public Result sendOrder(String telephone) {
        //获取验证码
        String code = ValidateCodeUtils.generateValidateCodeString(6);
        //创建内容集合
        Map<String,String> param = new HashMap<>();
        param.put("code",code);
//        try {
//            //发送验证码
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, param);
//        } catch (ClientException e) {//发送失败
//            e.printStackTrace();
//            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
//        }
        //预约验证码保存至redis//以便后续校验
        jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_ORDER, 5 * 60,"1233");
        String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        System.out.println(redisCode);
        //返回短信发送成功通知
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }


    @RequestMapping("/sendLogin")
    /**
     * 发送登录验证码短信
     * @param telephone
     * @Return:
     */
    public Result sendLogin(String telephone) {
        //获取验证码
        String code = ValidateCodeUtils.generateValidateCodeString(6);
        //创建内容集合
        Map<String,String> param = new HashMap<>();
        param.put("code",code);
//        try {
//            //发送验证码
//            SMSUtils.sendShortMessage(SMSUtils.REGISTER_CODE,telephone, param);
//        } catch (ClientException e) {//发送失败
//            e.printStackTrace();
//            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
//        }
        //预约验证码保存至redis//以便后续校验
        jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_LOGIN, 5 * 60,"1233");
        String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        System.out.println(redisCode);
        //返回短信发送成功通知
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }


}
