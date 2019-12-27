package io.ext.medinvention.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.ext.medinvention")
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
