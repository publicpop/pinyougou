package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
    * 搜索
    * @param
    * @Return:
    */
    Map<String,Object> search(Map searchMap);

    /**
    * 导入数据
    * @param
    * @Return:
    */
    void importList(List list);

    /**
    * 同步删除商品在solr中的数据
    * @param
    * @Return:
    */
    void deleteByGoodsId(List goodsId);
}
