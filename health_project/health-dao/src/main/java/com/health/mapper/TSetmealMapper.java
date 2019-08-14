package com.health.mapper;

import com.health.pojo.Setmeal;
import com.health.pojo.TSetmealExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TSetmealMapper {
    int countByExample(TSetmealExample example);

    int deleteByExample(TSetmealExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Setmeal record);

    int insertSelective(Setmeal record);

    List<Setmeal> selectByExample(TSetmealExample example);

    Setmeal selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Setmeal record, @Param("example") TSetmealExample example);

    int updateByExample(@Param("record") Setmeal record, @Param("example") TSetmealExample example);

    int updateByPrimaryKeySelective(Setmeal record);

    int updateByPrimaryKey(Setmeal record);

    List<Map<String,Object>> findSemealAndOrderCount();

    /**
    * 查询套餐名
    * @param
    * @Return:
    */
    List<String> selectSetmealName();

    /**
    * 根据套餐名查询套餐
    * @param setmealName
    * @Return:
    */
    Setmeal findSetmealBySetmealName(String setmealName);

}