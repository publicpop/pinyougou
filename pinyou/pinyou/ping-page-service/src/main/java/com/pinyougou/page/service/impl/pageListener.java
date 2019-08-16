package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageHtml;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class pageListener implements MessageListener{

    @Autowired
    private ItemPageHtml itemPageHtml;

    @Override
    public void onMessage(Message message) {

        //获取队列中的信息并转换
        TextMessage textMessage = (TextMessage) message;

        try {
            //获取消息中的文本信息
            String text = textMessage.getText();
            //调用方法生成静态化网页
            boolean pageHtml = this.itemPageHtml.getItemPageHtml(Long.parseLong(text));
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
