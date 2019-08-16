package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //插入数据
        specificationMapper.insert(specification.getSpecification());
        //规格参数
        List<TbSpecificationOption> list = specification.getSpecificationOptionList();

        //将规格参数循环插入数据库
        for (TbSpecificationOption option : list) {
            //设置规格表的id
            option.setSpecId(specification.getSpecification().getId());
            specificationOptionMapper.insert(option);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        //保存修改的规格名称
        specificationMapper.updateByPrimaryKey(specification.getSpecification());
        //删除原有的规格详情
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpecification().getId());
        specificationOptionMapper.deleteByExample(example);
        //重新循环插入修改后的规格详情
        List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption option : optionList) {
            option.setSpecId(specification.getSpecification().getId());
            specificationOptionMapper.insert(option);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //根据id查找规格
        TbSpecification select = specificationMapper.selectByPrimaryKey(id);
        //根据id查询规格具体详情
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
        //将查询到的规格名称及其详情封装为Specification,并返回
        Specification specification = new Specification();
        specification.setSpecification(select);
        specification.setSpecificationOptionList(list);
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格名称
            specificationMapper.deleteByPrimaryKey(id);
            //删除与规格名称对应的规格详情
            //创建规格详情例对象
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            //获取例对象的条件域
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            //向例对象的条件域中添加条件
            criteria.andSpecIdEqualTo(id);
            //根据例对象删除对应的规格详情
            specificationOptionMapper.deleteByExample(example);

        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectSpecList() {
        return specificationMapper.selectSpecList();
    }
}
