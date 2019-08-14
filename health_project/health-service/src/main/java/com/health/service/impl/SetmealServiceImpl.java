package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.constant.RedisConstant;
import com.health.mapper.TSetmealMapper;
import com.health.pojo.*;
import com.health.pojo.TSetmealExample.Criteria;
import com.health.service.SetmealAndCheckGroupService;
import com.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private TSetmealMapper setmealMapper;

    /**
     * 查询全部
     */
    @Override
    public List<Setmeal> findAll() {
        return setmealMapper.selectByExample(null);
    }

    @Autowired
    private SetmealAndCheckGroupService setmealAndCheckGroupService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 增加
     */
    @Override
    public void add(QueryPagebean<Setmeal> queryPagebean) {
        Setmeal setmeal = queryPagebean.getQueryString();//获取套餐对象
        List<Integer> list = queryPagebean.getList();//获取映射关系集合
        setmealMapper.insert(setmeal);//添加新增套餐对象
        //添加套餐与检查组映射关系
        for (Integer id : list) {
            setmealAndCheckGroupService.saveSetmealIdAndCheckGroupId(setmeal.getId(), id);
        }
        //将上传成功的后的图片保存到redis中,用于清除垃圾图片,set集合
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCE, queryPagebean.getQueryString().getImg());
    }


    /**
     * 修改
     */
    @Override
    public void update(QueryPagebean<Setmeal> queryPagebean) {
        Setmeal setmeal = queryPagebean.getQueryString();
        List<Integer> list = queryPagebean.getList();
        setmealMapper.updateByPrimaryKey(setmeal);
        setmealAndCheckGroupService.deleteSetmealIdByCheckGroupId(setmeal.getId());
        for (Integer id : list) {
            setmealAndCheckGroupService.saveSetmealIdAndCheckGroupId(setmeal.getId(), id);
        }
        //删除更新的图片
        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCE, setmeal.getImg());
        //将上传成功的后的图片保存到redis中,用于清除垃圾图片,set集合
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCE, setmeal.getImg());
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Setmeal findOne(Integer id) {
        return setmealMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Integer id) {
        setmealAndCheckGroupService.deleteSetmealIdByCheckGroupId(id);
        Setmeal setmeal = findOne(id);
        String img = setmeal.getImg();
        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCE, img);
        setmealMapper.deleteByPrimaryKey(id);
    }


    @Override
    /**
     * 条件查询+分页
     * @param setmeal
     * @param pageNum
     * @param pageSize
     * @Return:com.health.pojo.PageResult
     */
    public PageResult findPage(Map<String,String> map, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        TSetmealExample example = new TSetmealExample();
        Criteria criteria = example.createCriteria();

        if (map != null) {
            if (map.get("code") != null &&map.get("code") != "") {
                criteria.andCodeLike("%" + map.get("code") + "%");
            }
            if (map.get("name") != null &&map.get("name") != "") {
                criteria.andNameLike("%" + map.get("name") + "%");
            }
            if (map.get("helpCode") != null &&map.get("helpCode") != "") {
                criteria.andHelpcodeLike("%" + map.get("helpCode") + "%");
            }

        }

        Page<Setmeal> page = (Page<Setmeal>) setmealMapper.selectByExample(example);
        List<Setmeal> result = page.getResult();
        long total = page.getTotal();
        return new PageResult(page.getTotal(), page.getResult());
    }



}
