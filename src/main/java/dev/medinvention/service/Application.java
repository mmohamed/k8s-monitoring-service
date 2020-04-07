package dev.medinvention.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.medinvention")
public class Application {
	
	public static ConcurrentMap<String, Integer> ramMetrics = new ConcurrentHashMap<String, Integer>();
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
