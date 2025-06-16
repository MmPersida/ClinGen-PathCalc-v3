package com.persida.pathogenicity_calculator;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PathogenicityCalculatorApplication {
	private static Logger logger = Logger.getLogger(PathogenicityCalculatorApplication.class);

	public static void main(String[] args) {
		logger.info("--------------------- Starting PC Ver: 3.1 ---------------------");
		SpringApplication.run(PathogenicityCalculatorApplication.class, args);
	}
}
