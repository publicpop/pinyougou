package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

public class ItemDeleteListener implements MessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            //强转消息中的信息为所需的类型
            ObjectMessage objectMessage = (ObjectMessage) message;
            //获取其中的id数组
            Long[] ids = (Long[]) objectMessage.getObject();
            //更具id信息删除solr中的相关数据
            itemSearchService.deleteByGoodsId(Arrays.asList(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
