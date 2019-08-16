package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "* * * * * ?")
    /**
    *
    * @param
    * @Return:void
    */
    public void refreshSeckillGoods() {
        //查询所有的秒杀商品键集合
        List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        //查询符合秒杀的商品列表
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();//创建条件查询
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();//创建条件集
        criteria.andStatusEqualTo("1");//商品通过审核
        criteria.andStockCountGreaterThan(0);//库存数量大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//秒杀开始时间小于等于当前时间
        criteria.andEndTimeGreaterThan(new Date());//秒杀结束时间大于当前时间
        criteria.andIdNotIn(ids);//该商品此时不存在于redis中
        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);//查询所有符合条件的商品
        for (TbSeckillGoods goods : seckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(goods.getId(), goods);//将查询到的 符合条件的商品存入redis
        }
    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods() {
        //获取redis中秒杀商品集
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        //遍历秒杀商品集
        for (TbSeckillGoods seckillGood : seckillGoods) {
            //如果该商品秒杀借宿时间小于当前时间,则将此商品从redis中删除
            if (seckillGood.getEndTime().getTime() < new Date().getTime()) {
                //向数据库中保存秒杀记录
                seckillGoodsMapper.updateByPrimaryKey(seckillGood);
                //删除redis中的秒杀商品
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
            }
        }
    }


}







