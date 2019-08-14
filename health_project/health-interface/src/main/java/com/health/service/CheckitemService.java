package com.health.service;
import java.util.List;
import java.util.Map;

import com.health.pojo.CheckItem;

import com.health.pojo.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface CheckitemService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<CheckItem> findAll();


	/**
	 * 增加
	*/
	public void add(CheckItem checkitem);
	
	
	/**
	 * 修改
	 */
	public void update(CheckItem checkitem);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public CheckItem findOne(Integer id);
	
	
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
