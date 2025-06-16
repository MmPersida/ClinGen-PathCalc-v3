package com.persida.pathogenicity_calculator.scheduled_tasks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTasks {
    private static Logger logger = Logger.getLogger(StartupTasks.class);

    @Value("${executeDataUpdateAtStartup}")
    private boolean executeDataUpdateAtStartup;

    @Autowired
    private DataLoadingTasks dataLoadingTasks;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        if(executeDataUpdateAtStartup){
            logger.info("Loading Disease data on startup!");
            dataLoadingTasks.loadAndCompareDiseaseInfo();
        }

        if(executeDataUpdateAtStartup){
            logger.info("Loading CSpec Engine data on startup!");
            dataLoadingTasks.loadAndCompareVCPEsInfo();
        }
    }
}
