package com.ag.xml.service;

import com.ag.xml.model.Br;

public interface BrService {

    int addBr(Br br);

    Br selectByPrimaryKey(Long id);
}
