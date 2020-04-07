package dev.medinvention.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.medinvention.service.service.TemperatureService;

@RestController
@RequestMapping("/collect/${security.token}")
public class CollectorController {

	@Autowired
	TemperatureService temperatureService;

	@GetMapping("/temperature")
	public Integer setTemperature(@RequestParam String node, @RequestParam Integer value) {
		return temperatureService.collect(node, value);
	}
}
