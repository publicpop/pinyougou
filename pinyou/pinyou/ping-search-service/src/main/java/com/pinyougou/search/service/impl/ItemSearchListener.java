package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener{


    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            //将消息转换为文本类型
            TextMessage textMessage = (TextMessage) message;
            //获取消息文本
            String text = textMessage.getText();
            //将消息json格式化
            List<TbItem> items = JSON.parseArray(text, TbItem.class);
            //向带有注解的map集合字段中添加数据
            for (TbItem item : items) {
    //            将item中的spec字段转换成map集合
                Map specMap = JSON.parseObject(item.getSpec());
                //向有域注解的map字段中添加数据
                item.setSpecMap(specMap);
            }
            itemSearchService.importList(items);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
