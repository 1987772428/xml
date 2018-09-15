package com.ag.xml.service.impl;

import com.ag.xml.dao.TrMapper;
import com.ag.xml.model.Tr;
import com.ag.xml.service.TrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrServiceImpl implements TrService {

    @Autowired
    TrMapper trMapper;

    @Override
    public int addTr(Tr tr) {

        return trMapper.insertSelective(tr);
    }

    @Override
    public Tr selectByPrimaryKey(Long id) {

        return trMapper.selectByPrimaryKey(id);
    }
}
