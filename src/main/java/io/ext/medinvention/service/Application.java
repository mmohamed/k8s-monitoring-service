package io.ext.medinvention.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.ext.medinvention.core.Collector;

@SpringBootApplication(scanBasePackages = "io.ext.medinvention")
@RestController
public class Application {

	private final Collector myService;

	public Application(Collector myService) {
		this.myService = myService;
	}
	
	@GetMapping("/state")
	public String state() {
		return myService.isRunning() ? "OK" : "KO";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
