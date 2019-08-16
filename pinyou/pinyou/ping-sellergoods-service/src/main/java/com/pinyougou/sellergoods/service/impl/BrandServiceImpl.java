package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;


    /**
     * 查询所有品牌
     *
     * @Param:
     * @Return:
     */
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }


    @Override
    /**
     * 分页查询
     * @Param:[pageNum, pageSize]
     * @Return:entity.PageResult
     */
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    /**
     * 新增品牌
     * @Param:[brand]
     * @Return:void
     */
    public void add(TbBrand brand) {

        tbBrandMapper.insert(brand);
        int i = 1/0;
    }

    @Override
    /**
    * 更新数据
    * @param brand
    * @Return:void
    */
    public void update(TbBrand brand) {
        tbBrandMapper.updateByPrimaryKey(brand);
    }

    @Override
    /**
    * 给根据id查询
    * @param id
    * @Return:com.pinyougou.pojo.TbBrand
    */
    public TbBrand findOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    /**
     * 根据获取的id数组批量删除数据
     * @Param:[id]
     * @Return:void
     */
    public void delete(long[] id) {
        for (long l : id) {
            tbBrandMapper.deleteByPrimaryKey(l);
        }
    }

    @Override
    /**
    * 条件查询
    * @param brand  //品牌
    * @param pageNum  //所选页码
    * @param pageSize  //所选数据长度
    * @Return:entity.PageResult
    */
    public PageResult search(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (brand!=null) {
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if(brand.getFirstChar() !=null&&brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }
}
