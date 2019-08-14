package com.health.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.mapper.TCheckitemMapper;
import com.health.pojo.CheckItem;
import com.health.pojo.TCheckitemExample;
import com.health.pojo.TCheckitemExample.Criteria;
import com.health.service.CheckitemService;

import com.health.pojo.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class CheckitemServiceImpl implements CheckitemService {

    @Autowired
    private TCheckitemMapper checkitemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<CheckItem> findAll() {
        return checkitemMapper.selectByExample(null);
    }


    /**
     * 增加
     */
    @Override
    public void add(CheckItem checkitem) {
        checkitemMapper.insert(checkitem);
    }


    /**
     * 修改
     */
    @Override
    public void update(CheckItem checkitem) {
        checkitemMapper.updateByPrimaryKey(checkitem);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public CheckItem findOne(Integer id) {
        return checkitemMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Integer id) {
            checkitemMapper.deleteByPrimaryKey(id);
    }


    @Override
    /**
     * 条件查询+分页
     * @param checkitem
     * @param pageNum
     * @param pageSize
     * @Return:com.health.pojo.PageResult
     */
    public PageResult findPage(Map<String,String> map, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        TCheckitemExample example = new TCheckitemExample();
        Criteria criteria = example.createCriteria();

        if (map != null) {
            if (map.get("code") != null && map.get("code")!="") {
                criteria.andCodeLike("%" + map.get("code") + "%");
            }
            if (map.get("name") != null && map.get("name")!="") {
                criteria.andNameLike("%" + map.get("name") + "%");
            }

        }

        Page<CheckItem> page = (Page<CheckItem>) checkitemMapper.selectByExample(example);
        List<CheckItem> result = page.getResult();
        long total = page.getTotal();
        return new PageResult(page.getTotal(), page.getResult());
    }

}
