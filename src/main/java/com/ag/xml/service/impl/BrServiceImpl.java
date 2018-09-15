package com.ag.xml.service.impl;

import com.ag.xml.dao.BrMapper;
import com.ag.xml.model.Br;
import com.ag.xml.service.BrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrServiceImpl implements BrService {

    @Autowired
    BrMapper brMapper;

    @Override
    public int addBr(Br br) {

        return brMapper.insertSelective(br);
    }

    @Override
    public Br selectByPrimaryKey(Long id) {

        return brMapper.selectByPrimaryKey(id);
    }
}
