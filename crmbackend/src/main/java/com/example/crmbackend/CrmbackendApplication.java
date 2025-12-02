package com.example.crmbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CrmbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmbackendApplication.class, args);
	}

}
