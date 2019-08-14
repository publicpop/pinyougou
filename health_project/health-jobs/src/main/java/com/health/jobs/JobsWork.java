package com.health.jobs;

import com.health.constant.QiniuUtils;
import com.health.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;

/**
* 定时任务调度
 * 清理上传未保存到数DB据库图片
* @param
* @Return:
*/
public class JobsWork {

    @Autowired
    private JedisPool jedisPool;

    public void run(){
        System.out.println("=======");
        //获取redis中垃圾图片集合(有效图片与无效图片的差集)
         Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCE, RedisConstant.SETMEAL_PIC_DB_RESOURCE);
        //获取迭代器
        Iterator<String> iterator = set.iterator();
        System.out.println("++++++++");
        while (iterator.hasNext()){
            //获取垃圾图片信息
            String pic = iterator.next();
            //删除云服务中的垃圾图片
            QiniuUtils.deleteFileFromQiniu(pic);  ///**注意quartz与qiniu-java-sdk 版本匹配!!!!!!2.2.1与7.2.17冲突导致方法找不到
            //解决方法,将quartz更换为高版本2.3.0
            //删除redis中的垃圾图片
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCE,pic);
            System.out.println("正在执行");
        }
        System.out.println("===+++===");
    }
}