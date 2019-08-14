package com.health.service;

import com.health.pojo.PageResult;
import com.health.pojo.QueryPagebean;
import com.health.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SetmealService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<Setmeal> findAll();


	/**
	 * 增加
	*/
	public void add(QueryPagebean<Setmeal> queryPagebean);
	
	
	/**
	 * 修改
	 */
	public void update(QueryPagebean<Setmeal> queryPagebean);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Setmeal findOne(Integer id);
	
	
	/**
	 * 批量删除
	 * @param id
	 */
	public void delete(Integer id);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(Map<String,String> map, Integer pageNum, Integer pageSize);



}
