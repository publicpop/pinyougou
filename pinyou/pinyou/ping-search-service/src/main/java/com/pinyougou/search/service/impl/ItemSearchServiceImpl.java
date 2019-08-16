package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    /**
     * 分词搜索
     * @param searchMap
     * @Return:java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> search(Map searchMap) {
        //获取关键字
        String keywords = (String) searchMap.get("keywords");
        if(keywords!=null){
            //去除关键字中的空格
            String replace = keywords.replace(" ", "");
            searchMap.put("keywords",replace);
        }
        //创建集合用于存放搜索出的数据
        Map map = new HashMap();

        //将查询结果封装map并返回
        map.putAll(getMap(searchMap));
        //获取商品类型结果并添加至map集合
        List list = getList(searchMap);
        map.put("category", list);
        //如果商品类型结果集不为空
        if(list.size()>0){
            //获取商品类目
            String category = (String) searchMap.get("category");
            if(!"".equals(category)){
                map.putAll(searchBrandAndSpec(category));
            }else {
                //将品牌及规格结果集存入最终集合
                map.putAll(searchBrandAndSpec((String) list.get(0)));
            }
        }
        //返回最终结果集
        return map;


    }

    private List getList(Map searchMap) {
        //创建**list**集合,存放查询数据
        List list = new ArrayList();
        //创建查询对象
        SimpleQuery query = new SimpleQuery();
        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项   ***分组选项可以有多个add可以有多个
        GroupOptions group = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(group);
        //获取分组页
        GroupPage<TbItem> items = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列获得分组结果集
        GroupResult<TbItem> category = items.getGroupResult("item_category");
        //获取结果分组页入口集合并遍历    //********分组页集合可以直接遍历
        for (GroupEntry<TbItem> entry : category.getGroupEntries()) {
            list.add(entry.getGroupValue());
        }
//        for (GroupEntry<TbItem> entry : category.getGroupEntries().getContent()) {
//            //获得分组集合并遍历
//            list.add(entry.getGroupValue());
//            //将结果集的名称循环添加到list的集合中
//        }
        //返回最终结果
        return list;
    }


    /**
     * 查询商品展示分页数据,及查询条件高亮显示
     *
     * @param
     * @Return:
     */
    private Map<String, Object> getMap(Map searchMap) {
        //创建集合存放查询到的分页数据
        Map<String, Object> map = new HashMap<>();

        //创建查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //设置高亮域
        HighlightOptions title = new HighlightOptions().addField("item_title");

        //高亮前缀
        title.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        title.setSimplePostfix("</em>");
        query.setHighlightOptions(title);

        //条件过滤
        //商品类目过滤
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //品牌过滤
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //规格过滤
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //价格筛选
        if(!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){
                Criteria criteria = new Criteria("item_price").greaterThanEqual(price[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){
                Criteria criteria = new Criteria("item_price").lessThanEqual(price[1]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }

        }

        //分页查询
        //当前页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if(pageNo==null){
            pageNo=1;
        }
        //每页记录数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);//多少条记录开始查
        query.setRows(pageSize);//每页显示数据数量

        //排序
        //获取排序方式
        String sort = (String) searchMap.get("sort");
        //获取排序字段
        String sortField = (String) searchMap.get("sortField");
        //当排序字段及排序方式都存在时不采用默认排序方式
        if(sort!=null&&!sort.equals("")){
            if(sort.equals("ASC")){//升序
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(orders);
            }
            if(sort.equals("DESC")){//降序
                Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(orders);
            }
        }

        //添加查询条件(高亮处理)
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //将查询的结果中的分页数据
        HighlightPage<TbItem> items = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //遍历高亮入口集
        for (HighlightEntry<TbItem> entry : items.getHighlighted()) {
            TbItem item = entry.getEntity();//获得实体
            //当结果集存在时
            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
                //设置高亮
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }

        map.put("totalPages",items.getTotalPages());//返回总页数
        map.put("total",items.getTotalElements());//返回总记录路数

        map.put("rows", items.getContent());
        return map;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
    * 查询品牌及规格结果集
    * @param
    * @Return:
    */
    private Map searchBrandAndSpec(String category) {
        //创建map集合存放最终结果集
        Map map = new HashMap();
        //获取模板id
        Long itemCatId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //如果模板id不为空
        if(itemCatId!=null){
            //根据模板id查询品牌信息,并添加至结果集
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(itemCatId);
            map.put("brandList",brandList);
            //根据模板id查询规格信息,并添加至结果集
            List specList = (List) redisTemplate.boundHashOps("specList").get(itemCatId);
            map.put("specList",specList);
        }
        //返回结果集
        return map;
    }

    @Override
    /**
    * 同步审核通过的商品到solr索引库中
    * @param list
    * @Return:void
    */
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    /**
    * 同步删除solr索引库中的相应数据
    * @param goodsId
    * @Return:void
    */
    public void deleteByGoodsId(List goodsId) {
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsId").in(goodsId);
        query.addCriteria(criteria);//item_goodsId域中存在的goods集合中的id
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
