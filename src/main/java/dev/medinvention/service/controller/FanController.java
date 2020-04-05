package dev.medinvention.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.medinvention.service.model.FanStatus;
import dev.medinvention.service.service.TemperatureService;

@RestController
@RequestMapping("/fan")
public class FanController {
	
	@Autowired
	TemperatureService temperatureService;

	@PostMapping("/on")
	public FanStatus on() {
		return this.temperatureService.on();
	}
	
	@PostMapping("/off")
	public FanStatus off() {
		return this.temperatureService.off();
	}
	
	@GetMapping("/status")
	public FanStatus status() {
		return this.temperatureService.status();
	}
}
