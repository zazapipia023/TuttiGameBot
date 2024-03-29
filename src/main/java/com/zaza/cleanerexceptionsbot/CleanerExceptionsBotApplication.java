package com.zaza.cleanerexceptionsbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class CleanerExceptionsBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CleanerExceptionsBotApplication.class, args);
	}

}
