package com.ag.xml.dao;

import com.ag.xml.model.Br;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BrMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Br record);

    int insertSelective(Br record);

    Br selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Br record);

    int updateByPrimaryKey(Br record);
}