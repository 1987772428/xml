package com.ag.xml.service;

import com.ag.xml.model.Hunter;

public interface HunterService {

    int addHunter(Hunter hunter);

    Hunter selectByPrimaryKey(Long id);
}
