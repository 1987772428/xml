package com.ag.xml.dao;

import com.ag.xml.model.Hunter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HunterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Hunter record);

    int insertSelective(Hunter record);

    Hunter selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Hunter record);

    int updateByPrimaryKey(Hunter record);
}