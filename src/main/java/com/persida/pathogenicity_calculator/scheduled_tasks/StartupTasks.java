package com.persida.pathogenicity_calculator.scheduled_tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTasks {

    @Value("${executeDataUpdateAtStartup}")
    private boolean executeDataUpdateAtStartup;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        if(executeDataUpdateAtStartup){
            scheduledTasks.loadDiseaseInfo();
        }

        if(executeDataUpdateAtStartup){
            scheduledTasks.loadCSpecEngineInfo();
        }
    }
}
