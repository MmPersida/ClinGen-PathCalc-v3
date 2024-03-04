package com.persida.pathogenicity_calculator.scheduled_tasks;

import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;

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
