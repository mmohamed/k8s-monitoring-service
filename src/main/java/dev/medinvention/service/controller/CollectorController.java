package dev.medinvention.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.medinvention.service.Application;

@RestController
@RequestMapping("/collect/${security.token}")
public class CollectorController {

	@GetMapping("/temperature")
	public Integer setTemperature(@RequestParam String node, @RequestParam Integer value) {
		String ramMetricName = node + ".cputemperature";
		Integer previousValue = Application.ramMetrics.containsKey(ramMetricName)
				? Application.ramMetrics.get(ramMetricName)
				: 0;
		Application.ramMetrics.put(ramMetricName, value);
		return previousValue;
	}
}
