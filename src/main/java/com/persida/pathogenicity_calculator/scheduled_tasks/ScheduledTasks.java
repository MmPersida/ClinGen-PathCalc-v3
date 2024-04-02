package com.persida.pathogenicity_calculator.scheduled_tasks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private static Logger logger = Logger.getLogger(ScheduledTasks.class);

    @Value("${executeScheduledDataUpdate}")
    private boolean executeScheduledDataUpdate;

    @Autowired
    private DataLoadingTasks dataLoadingTasks;

    @Scheduled(cron = "${diseaseCall.cron.expression}")
    public void loadDiseaseInfoScheduled(){
        if(executeScheduledDataUpdate){
            logger.info("Executing scheduled loading of Disease data!");
            dataLoadingTasks.loadDiseaseInfo();
        }
    }

    @Scheduled(cron = "${cspecEngineCall.cron.expression}")
    public void loadCSpecEngineInfoScheduled(){
        if(executeScheduledDataUpdate){
            logger.info("Executing scheduled loading of CSpec Engine data!");
            dataLoadingTasks.loadCSpecEngineInfo();
        }
    }
}
