package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idworker;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //获取登录用户购物车信息
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(order.getUserId());
        List<String> orderList = new ArrayList<>();//存放订单列表
        double total_money = 0;//总金额
        //添加订单及其详情信息
        for (Cart cart : cartList) {
            long nextId = idworker.nextId();//生成商品订单号
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(nextId);
            tbOrder.setUserId(order.getUserId());
            tbOrder.setPaymentType(order.getPaymentType());
            tbOrder.setStatus("1");//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价;
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            tbOrder.setReceiver(order.getReceiver());
            tbOrder.setSourceType(order.getSourceType());
            tbOrder.setSellerId(order.getSellerId());
            //定义money接收该商家的总金额
            double money = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idworker.nextId());
                orderItem.setOrderId(nextId);
                orderItem.setSellerId(order.getSellerId());
                //金额累加
                money += orderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(orderItem);
            }
            tbOrder.setPayment(new BigDecimal(money));
            orderMapper.insert(tbOrder);

            orderList.add(nextId+"");//将订单号存入集合
            total_money += money;//计算总金额
        }
        //如果支付方式是微信支付
        if("1".equals(order.getPaymentType())){
            TbPayLog payLog = new TbPayLog();//创建支付日志
            String outTradeNo = idworker.nextId()+"";//生成支付订单号
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setPayType("1");//支付类型
            payLog.setTotalFee((long) total_money*100);//支付总金额
            payLog.setCreateTime(new Date());//创建订单时间
            //将订单集合,以字串形式且订单id间用<,>分隔,并存入支付中
            String orderId = orderList.toString().replace("[", "").replace(" ", "").replace("]", "");
            payLog.setOrderList(orderId);//订单列表
            payLog.setUserId(order.getUserId());//用户id
            payLog.setTradeState("0");//支付状态
            //将日志加入数据库
            payLogMapper.insert(payLog);
            //将日志加入至缓存
            redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);

        }

        redisTemplate.boundHashOps("cart").delete(order.getUserId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Override
    /**
    * 根据用户id查询用户支付订单
    * @param userId
    * @Return:com.pinyougou.pojo.TbPayLog
    */
    public TbPayLog findPaylogByredis(String userId) {

        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    /**
    * 修改订单状态
    * @param out_trade_no
    * @param transaction_id
    * @Return:void
    */
    public void updateByOrderStatus(String out_trade_no, String transaction_id) {
        //修改日志状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());//支付日期
        payLog.setTradeState("1");//"1"已支付
        payLog.setTransactionId(transaction_id);//微信支付流水号
        payLogMapper.updateByPrimaryKey(payLog);
        //修改订单状态
        String orderList = payLog.getOrderList();
        String[] orderId = orderList.split(",");
        for (String id : orderId) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(id));
            //如果订单存在,则修改订单状态
            if(order!=null){
                order.setStatus("2");//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
                orderMapper.updateByPrimaryKey(order);
            }
        }
        //删除缓存中完成交易的信息
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
