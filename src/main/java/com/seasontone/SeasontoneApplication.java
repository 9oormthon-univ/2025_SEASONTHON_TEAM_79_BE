package com.seasontone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SeasontoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeasontoneApplication.class, args);
	}

}
