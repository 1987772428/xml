package com.ag.xml.service.impl;

import com.ag.xml.dao.LogsMapper;
import com.ag.xml.model.Logs;
import com.ag.xml.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogsServiceImpl implements LogsService {

    @Autowired
    LogsMapper logsMapper;

    @Override
    public int addLogs(Logs logs) {

        return logsMapper.insertSelective(logs);
    }

    @Override
    public Logs selectByPrimaryKey(Long id) {

        return logsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int selectLine(String path, String filename) {

        return logsMapper.selectLine(path, filename);
    }
}
