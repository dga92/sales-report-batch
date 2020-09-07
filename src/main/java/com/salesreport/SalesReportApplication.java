package com.salesreport;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SalesReportApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesReportApplication.class, args);
	}
}
