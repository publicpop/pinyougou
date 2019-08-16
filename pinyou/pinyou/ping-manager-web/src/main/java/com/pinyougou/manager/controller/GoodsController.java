package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}

	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		Goods one = goodsService.findOne(goods.getGoods().getId());
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if(!one.getGoods().getSellerId().equals(name)||
				!goods.getGoods().getSellerId().equals(name)){
			return new Result(false, "非法操作");
		}

		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}

	@Autowired
	private Destination queueSolrDeleteDestination;

	@Autowired
	private Destination topicPageDeleteDestination;

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			//消息队列删除solr中的数据
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
//			itemSearchService.deleteByGoodsId(Arrays.asList(ids));
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}


//	@Reference
//	private ItemSearchService itemSearchService;

	@Autowired
	private Destination queueTextDestination;//用于发送的消息

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination topicPageDestination;


	@RequestMapping("/updateStatus")
	/**
	* 更改商品状态
	* @param
	* @Return:
	*/
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);

			if(status.equals("1")){//通过审核
				List<TbItem> items = goodsService.findByGoodsIdAndStatus(ids, status);
				if(items.size()>0){
//					itemSearchService.importList(items);
					//消息中间件执行
					final String jsonString = JSON.toJSONString(items);
					//               发送信息对象    发送的信息
					jmsTemplate.send(queueTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
				}
				//生成静态化页面
				for (final Long id : ids) {
//					itemPageHtml.getItemPageHtml(id);//未使用消息中间件
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
//							返回文本消息
							return session.createTextMessage(id+"");
						}
					});
				}
			}
			return new Result(true,"审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"审核失败");
		}
	}

//	@Reference(timeout = 40000)
//	private ItemPageHtml itemPageHtml;

	/**
	* 生成静态化页面(测试)
	* @param
	* @Return:
	*/
	@RequestMapping("/buildHtml")
	public void buildHtml(Long goodsId){
//		itemPageHtml.getItemPageHtml(goodsId);
	}
	
}
