package com.persida.pathogenicity_calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PathogenicityCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PathogenicityCalculatorApplication.class, args);
	}
}
