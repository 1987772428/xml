package com.ag.xml.service.impl;

import com.ag.xml.dao.HunterMapper;
import com.ag.xml.model.Hunter;
import com.ag.xml.service.HunterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HunterServiceImpl implements HunterService {

    @Autowired
    HunterMapper hunterMapper;

    @Override
    public int addHunter(Hunter hunter) {

        return hunterMapper.insertSelective(hunter);
    }

    @Override
    public Hunter selectByPrimaryKey(Long id) {

        return hunterMapper.selectByPrimaryKey(id);
    }
}
