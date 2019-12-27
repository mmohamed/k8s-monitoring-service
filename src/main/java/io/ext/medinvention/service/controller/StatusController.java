package io.ext.medinvention.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.ext.medinvention.core.Collector;

@RestController
public class StatusController {

	private final Collector myService;

	public StatusController(Collector myService) {
		this.myService = myService;
	}
	
	@GetMapping("/state")
	public String state() {
		return myService.isRunning() ? "OK" : "KO";
	}
}
