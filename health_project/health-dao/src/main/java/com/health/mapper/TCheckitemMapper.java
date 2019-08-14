package com.health.mapper;

import com.health.pojo.CheckItem;
import com.health.pojo.TCheckitemExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TCheckitemMapper {
    int countByExample(TCheckitemExample example);

    int deleteByExample(TCheckitemExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CheckItem record);

    int insertSelective(CheckItem record);

    List<CheckItem> selectByExample(TCheckitemExample example);

    CheckItem selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CheckItem record, @Param("example") TCheckitemExample example);

    int updateByExample(@Param("record") CheckItem record, @Param("example") TCheckitemExample example);

    int updateByPrimaryKeySelective(CheckItem record);

    int updateByPrimaryKey(CheckItem record);
}