package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.mapper.TCheckgroupMapper;
import com.health.pojo.CheckGroup;
import com.health.pojo.PageResult;
import com.health.pojo.QueryPagebean;
import com.health.pojo.TCheckgroupExample;
import com.health.service.CheckgroupService;
import com.health.service.CheckitemAndCheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class CheckgroupServiceImpl implements CheckgroupService {

    @Autowired
    private TCheckgroupMapper checkgroupMapper;

    /**
     * 查询全部
     */
    @Override
    public List<CheckGroup> findAll() {
        return checkgroupMapper.selectByExample(null);
    }

    @Autowired
    private CheckitemAndCheckGroupService checkitemAndCheckGroupService;

    /**
     * 增加
     */
    @Override
    public void add(QueryPagebean<CheckGroup> queryPagebean) {
        CheckGroup checkGroup = queryPagebean.getQueryString();
        List<Integer> list = queryPagebean.getList();
        checkgroupMapper.insert(checkGroup);
        for (Integer checkitmId : list) {
            checkitemAndCheckGroupService.saveCheckItemIdAndCheckGroupId(checkGroup.getId(), checkitmId);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(QueryPagebean<CheckGroup> queryPagebean) {
        CheckGroup checkGroup = queryPagebean.getQueryString();//获取检查组对象
        List<Integer> list = queryPagebean.getList();//获取检查项对应id集合
        checkgroupMapper.updateByPrimaryKey(checkGroup);//更新检查组
        checkitemAndCheckGroupService.deleteCheckitemIdByCheckGroupId(checkGroup.getId());//删除旧检查组与检查相对应关系
        for (Integer checkitmId : list) {//添加新检查组与检查项对应关系
            checkitemAndCheckGroupService.saveCheckItemIdAndCheckGroupId(checkGroup.getId(), checkitmId);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public CheckGroup findOne(Integer id) {
        return checkgroupMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Integer id) {//并发问题!!!
        checkitemAndCheckGroupService.deleteCheckitemIdByCheckGroupId(id);//主键优先删除***否则外键关联数据无法删除!!!
        checkgroupMapper.deleteByPrimaryKey(id);
    }


    @Override
    /**
     * 条件查询+分页
     * @param checkitem
     * @param pageNum
     * @param pageSize
     * @Return:com.health.pojo.PageResult
     */
    public PageResult findPage(Map<String, String> map, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        TCheckgroupExample example = new TCheckgroupExample();
        TCheckgroupExample.Criteria criteria = example.createCriteria();

        if (map != null) {
            if (map.get("code") != null && map.get("code") != "") {
                criteria.andCodeLike("%" + map.get("code") + "%");
            }
            if (map.get("name") != null && map.get("name") != "") {
                criteria.andNameLike("%" + map.get("name") + "%");
            }
            if (map.get("helpcode") != null && map.get("helpcode") != "") {
                criteria.andHelpcodeLike("%" + map.get("helpcode") + "%");
            }

        }

        Page<CheckGroup> page = (Page<CheckGroup>) checkgroupMapper.selectByExample(example);
        List<CheckGroup> result = page.getResult();
        long total = page.getTotal();
        return new PageResult(page.getTotal(), page.getResult());
    }


}
