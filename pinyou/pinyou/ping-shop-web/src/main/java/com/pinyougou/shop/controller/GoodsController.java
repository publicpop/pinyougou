package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        //获取商家id
        //报空指针时,****注意对象中的元素是否有不存在的!!!!!!!!!!!
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.getGoods().setSellerId(sellerId);
        goods.getGoods().setIsMarketable("0");
        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        Goods one = goodsService.findOne(goods.getGoods().getId());
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!one.getGoods().getSellerId().equals(name) ||
                !goods.getGoods().getSellerId().equals(name)) {
            return new Result(false, "非法操作");
        }

        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    @RequestMapping("/updateMarkStatus")
    /**
     * 更改商品上下架状态
     * @param
     * @Return:
     */
    public Result updateMarkStatus(long[] ids, String status) {
        String s = "";
        for (long id : ids) {
            Goods one = goodsService.findOne(id);
            s += "1".equals(one.getGoods().getAuditStatus());
        }
        if (!s.contains("false")) {
            try {
                goodsService.updateMarkStatus(ids, status);
                return new Result(true, "操作成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "操作失败");
            }
        }
        return new Result(false, "操作失败");
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        //获取商家id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //添加搜索条件
        goods.setSellerId(name);
        return goodsService.findPage(goods, page, rows);
    }

}
