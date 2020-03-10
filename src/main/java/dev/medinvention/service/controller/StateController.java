package dev.medinvention.service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.medinvention.core.model.Node;
import dev.medinvention.core.model.Pod;
import dev.medinvention.core.model.State;
import dev.medinvention.core.service.NodeService;
import dev.medinvention.core.service.PodService;
import dev.medinvention.core.service.StateService;
import io.kubernetes.client.openapi.ApiException;

@RestController
@RequestMapping("/state")
public class StateController {

	private final StateService stateService;
	private final PodService podService;
	private final NodeService nodeService;
	
	public StateController(StateService stateService,NodeService nodeService,PodService podService) {
		this.stateService = stateService;
		this.nodeService = nodeService;
		this.podService = podService;
	}
	
	@GetMapping("/global")
	public State state() throws ApiException {
		return this.stateService.get();
	}
	
	@GetMapping("/pods")
	public List<Pod> pods() throws ApiException {
		return this.podService.get();
	}
	
	@GetMapping("/nodes")
	public List<Node> nodes() throws ApiException {
		return this.nodeService.get();
	}
}
