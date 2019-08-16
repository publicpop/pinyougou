package com.pinyougou.page.service;

public interface ItemPageHtml {

    /**
    * 创建商品详细页静态化页面
    * @param
    * @Return:
    */
    boolean getItemPageHtml(Long goodsId);

    /**
    * 根据商品id删除相关静态化页面
    * @param goodsId
    * @Return:boolean
    */
    boolean deletePageHtml(Long[] goodsId);
}
