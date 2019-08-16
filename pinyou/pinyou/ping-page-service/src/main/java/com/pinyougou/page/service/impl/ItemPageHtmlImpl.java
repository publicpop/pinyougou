package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageHtml;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageHtmlImpl implements ItemPageHtml {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    /**
     * 创建商品详细页静态化页面
     * @param goodsId
     * @Return:boolean
     */
    public boolean getItemPageHtml(Long goodsId) {

        try {
            //获取freemarker配置
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //获取静态化页面模板
            Template template = configuration.getTemplate("item.ftl");
            //创建集合存放静态化页面
            Map dataModel = new HashMap();

            //获取商品信息
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);
            //获取商品分类信息
            String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("category1", category1);
            dataModel.put("category2", category2);
            dataModel.put("category3", category3);
            //获取商品SKU信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");//第一个保持默认
            List<TbItem> items = itemMapper.selectByExample(example);
            dataModel.put("items", items);
            //获取文件输出流(浏览器打开乱码)
            //FileWriter fileWriter = new FileWriter(pagedir + goodsId + ".html");
            //转换流(解决浏览器打开乱码)
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(pagedir + goodsId + ".html")), "utf-8"));
            //将模板与数据整合并写入输出流
            template.process(dataModel, out);
            //关闭资源e
            out.close();
            //执行成功返回true
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //失败返回fals
            return false;
        }
    }


    @Override
    /**
     * 根据商品id删除相关静态化页面
     * @param goodsId
     * @Return:boolean
     */
    public boolean deletePageHtml(Long[] goodsId) {

        try {
            for (Long id : goodsId) {
                File file = new File(pagedir + id + ".html");
//                删除文件
                file.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
