package io.ext.medinvention.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.ext.medinvention.core.Collector;
import io.ext.medinvention.service.model.State;

@RestController
@RequestMapping("/state")
public class StateController {

	private final Collector myService;

	public StateController(Collector myService) {
		this.myService = myService;
	}
	
	@RequestMapping(value = "/global", method = RequestMethod.GET)
	public State state() {
		return (new State()).setIsRunning(myService.isRunning());
	}
}
