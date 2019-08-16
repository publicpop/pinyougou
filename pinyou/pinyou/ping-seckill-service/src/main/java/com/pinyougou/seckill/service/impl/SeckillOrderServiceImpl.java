package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;


    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Override
    /**
    * 提交秒杀订单
    * @param seckill
    * @param UserId
    * @Return:void
    */
    public void submitOrder(Long seckillId, String UserId) {

        //查询redis中秒杀商品的信息//判断其有效性
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if(seckillGoods==null){
            throw new RuntimeException("商品不存在");
        }
        if(seckillGoods.getStockCount()<=0){
            throw new RuntimeException("该商品已售罄");
        }
        //如果商品在缓存中存在测扣减库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);
        //若此商品剩余数量为0,则将此商品存入sql数据库//并删除redis中此商品数据
        if(seckillGoods.getStockCount()==0){
            tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
        }
        // 将生成的订单添加至redis中//便于减轻储存服务器压力//提升秒杀商品的更新速度
        long orderId = idWorker.nextId();//创建订单id
        TbSeckillOrder seckillOrder = new TbSeckillOrder();//创建订单对象
        seckillOrder.setId(orderId);//添加订单id
        seckillOrder.setCreateTime(new Date());//添加订单创建时间
        seckillOrder.setMoney(seckillGoods.getCostPrice());//添加商品秒杀价格
        seckillOrder.setSeckillId(seckillId);//添加秒杀商品id
        seckillOrder.setSellerId(seckillGoods.getSellerId());//添加秒杀商品对应的商家id
        seckillOrder.setUserId(UserId);//添加购买用户id
        seckillOrder.setStatus("0");//添加交易状态

        redisTemplate.boundHashOps("seckillOrder").put(UserId,seckillOrder);
    }


    @Override
    /**
    * 查询redis中订单信息
    * @param userId
    * @Return:com.pinyougou.pojo.TbSeckillOrder
    */
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }


    @Override
    /**
    * 秒杀支付订单持久化
    * @param userId
    * @param orderId
    * @param transaction
    * @Return:void
    */
    public void saveorderFromRedisToDb(String userId, Long orderId, String transactionId) {
        //根据用户id查询其相关订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        //判断订单是否有效
        if(seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        if(seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单不相符");
        }
        seckillOrder.setTransactionId(transactionId);//支付流水号
        seckillOrder.setPayTime(new Date());//支付日期
        seckillOrder.setStatus("1");//支付状态
        seckillOrderMapper.insert(seckillOrder);//支付订单持久化
        redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除redis中的成功支付的订单

    }

    @Override
    /**
    * 删除超时订单
    * @param userid
    * @param orderId
    * @Return:void
    */
    public void deleteorderByredis(String userid, Long orderId) {
        //获得用户存在于redis中的订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(orderId);
        //如果用户的已超时订单存在
        if(seckillOrder!=null&&seckillOrder.getId().longValue()==orderId.longValue()){
            //则删除此订单
            redisTemplate.boundHashOps("seckillOrder").delete(userid);
            //回复秒杀商品库存
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSellerId());
            //如果该商品存在,则将缓存中秒杀
            if(seckillOrder!=null){
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder,seckillGoods);
            }

        }
    }
}
