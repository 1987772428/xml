package com.ag.xml;

import com.ag.xml.controller.XmlController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    XmlController xmlController;

    @Scheduled(cron = "${scheduled.cron}")
    public void scheduleTaskXml()
    {
        logger.info("开始导入xml");
        xmlController.list();
    }

}
