package com.ag.xml.service;

import com.ag.xml.model.Tr;

public interface TrService {

    int addTr(Tr br);

    Tr selectByPrimaryKey(Long id);
}
