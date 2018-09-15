package com.ag.xml.service;

import com.ag.xml.model.Logs;

public interface LogsService {

    int addLogs(Logs log);

    Logs selectByPrimaryKey(Long id);

    int selectLine(String path, String filename);
}
