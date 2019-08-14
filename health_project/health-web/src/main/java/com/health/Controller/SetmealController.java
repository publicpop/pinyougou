package com.health.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.constant.QiniuUtils;
import com.health.constant.RedisConstant;
import com.health.pojo.*;
import com.health.service.SetmealAndCheckGroupService;
import com.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;


    @Reference
    private SetmealAndCheckGroupService setmealAndCheckGroupService;

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<Setmeal> findAll(){
        return setmealService.findAll();
    }

    /**
     * 增加
     * @param queryPagebean
     * @returnc
     */
    @RequestMapping("/add")
    public Result add(@RequestBody QueryPagebean<Setmeal> queryPagebean){
        try {
            setmealService.add(queryPagebean);
            return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    /**
     * 修改
     * @param queryPagebean
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody QueryPagebean<Setmeal> queryPagebean){
        try {
            setmealService.update(queryPagebean);
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
    public Map findOne(Integer id){
        List<Integer> checkItemIds = setmealAndCheckGroupService.searchSetmealIdByCheckGroupId(id);//获取套餐对应的检查组id
        Setmeal setmeal = setmealService.findOne(id);//获取套餐信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("ids",checkItemIds);
        map.put("group",setmeal);
        return map;

    }

    /**
     * 批量删除
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try {
            setmealService.delete(id);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     * @param bean
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody QueryPagebean<Map<String,String>> bean){
        return setmealService.findPage(bean.getQueryString(), bean.getCurrentPage(), bean.getPageSize());
    }

    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/upload")
    /**
    * 图片上传
    * @param imgFile
    * @Return:com.health.pojo.Result
    */
    public Result upload(@RequestParam("imgFile")MultipartFile imgFile){

        try {
            //获取文件名
            String filename = imgFile.getOriginalFilename();
            int index = filename.lastIndexOf(".");
            String suffix = filename.substring(index);
            //文件重命名避免重复
            String fileName = UUID.randomUUID().toString() + suffix;
            //文件上传
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            Result result = new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS);
            //将文件名返回
            result.setData(fileName);
            //将上传成功的后的图片保存到redis中,用于清除垃圾图片,set集合
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCE,fileName);
            return result;
        } catch (Exception e) {
            //上传失败
            e.printStackTrace();
            Result result = new Result(true, MessageConstant.PIC_UPLOAD_FAIL);
            return result;
        }
    }
}
