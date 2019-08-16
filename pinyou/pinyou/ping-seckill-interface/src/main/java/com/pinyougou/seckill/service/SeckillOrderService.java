package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);


	/**
	* 提交秒杀订单
	* @param
	* @Return:
	*/
	void submitOrder(Long seckillId,String UserId);


	/**
	* 查询redis中订单信息
	* @param userId
	* @Return:
	*/
	TbSeckillOrder searchOrderFromRedisByUserId(String userId);

	/**
	* 支付订单保存至Db中
	* @param userId
	* @param orderId
	* @param transactionId
	* @Return:
	*/
	void saveorderFromRedisToDb(String userId,Long orderId,String  transactionId);

	/**
	* 删除超时订单
	* @param
	* @Return:
	*/
	void deleteorderByredis(String userid,Long orderId);
}
