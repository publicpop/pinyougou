package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageHtml;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;

public class pageDeleteListener implements MessageListener{

    @Autowired
    private ItemPageHtml itemPageHtml;

    @Override
    public void onMessage(Message message) {

        //获取队列中的信息并转换
        ObjectMessage textMessage = (ObjectMessage) message;

        try {
            //获取消息中的文本信息
            Long[] text = (Long[]) textMessage.getObject();
            //调用方法生成静态化网页
            boolean pageHtml = itemPageHtml.deletePageHtml(text);
            System.out.println(pageHtml);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
