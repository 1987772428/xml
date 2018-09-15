package com.ag.xml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlApplication.class, args);
    }
}
