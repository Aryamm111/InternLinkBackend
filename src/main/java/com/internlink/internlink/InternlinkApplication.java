package com.internlink.internlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.internlink.internlink.repository")

public class InternlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternlinkApplication.class, args);
	}

}
