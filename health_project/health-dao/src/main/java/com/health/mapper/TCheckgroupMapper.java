package com.health.mapper;

import com.health.pojo.CheckGroup;
import com.health.pojo.TCheckgroupExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TCheckgroupMapper {
    int countByExample(TCheckgroupExample example);

    int deleteByExample(TCheckgroupExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CheckGroup record);

    int insertSelective(CheckGroup record);

    List<CheckGroup> selectByExample(TCheckgroupExample example);

    CheckGroup selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CheckGroup record, @Param("example") TCheckgroupExample example);

    int updateByExample(@Param("record") CheckGroup record, @Param("example") TCheckgroupExample example);

    int updateByPrimaryKeySelective(CheckGroup record);

    int updateByPrimaryKey(CheckGroup record);
}