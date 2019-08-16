package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	public List<TbGoods> findAll();


	/**
	 * 返回分页列表
	 *
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	 */
	public void add(Goods goods);


	/**
	 * 修改
	 */
	public void update(Goods goods);


	/**
	 * 根据ID获取实体
	 *
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);


	/**
	 * 批量删除
	 *
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 *
	 * @param pageNum  当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 审核商品
	 *
	 * @param
	 * @Return:
	 */
	void updateStatus(Long[] ids, String status);

	/**
	 * 商家上下架状态
	 *
	 * @param
	 * @Return:
	 */
	void updateMarkStatus(long[] ids, String status);

	/**
	* 根据商品id及状态查询商品的详情(SKU)
	* @param
	* @Return:
	*/
	List<TbItem> findByGoodsIdAndStatus(Long[] goodsId, String status);
}