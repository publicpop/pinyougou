package com.pinyougou.sellergoods.service;


import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌列表接口
 *
 * @Param:
 * @Return:
 */

public interface BrandService {

    //查询所有品牌
    List<TbBrand> findAll();

    // 分页查询
    PageResult findPage(int pageNum, int pageSize);

    //新增
    void add(TbBrand brand);

    //修改
    void update(TbBrand brand);

    //根据ID查询信息  //id为uuid  数据type  Long
    TbBrand findOne(long id);

    //根据id数组批量删除信息
    void delete(long[] id);

    //条件查询
    PageResult search(TbBrand brand,int pageNum,int pageSize);

    List<Map> selectOptionList();

}
