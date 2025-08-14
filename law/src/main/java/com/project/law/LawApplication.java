package com.project.law;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LawApplication {

	public static void main(String[] args) {
		SpringApplication.run(LawApplication.class, args);
	}

}
