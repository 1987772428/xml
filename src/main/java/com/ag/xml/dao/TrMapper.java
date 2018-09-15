package com.ag.xml.dao;

import com.ag.xml.model.Tr;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrMapper {
    int deleteByPrimaryKey(Long tid);

    int insert(Tr record);

    int insertSelective(Tr record);

    Tr selectByPrimaryKey(Long tid);

    int updateByPrimaryKeySelective(Tr record);

    int updateByPrimaryKey(Tr record);
}