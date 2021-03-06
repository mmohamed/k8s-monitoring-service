package dev.medinvention.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.medinvention.core.model.Metrics;
import dev.medinvention.core.model.Node;
import dev.medinvention.core.model.Pod;
import dev.medinvention.core.model.State;
import dev.medinvention.core.service.ClusterService;
import dev.medinvention.core.service.NodeService;
import dev.medinvention.core.service.PodService;
import dev.medinvention.core.service.StateService;
import dev.medinvention.service.model.NodeMetrics;
import dev.medinvention.service.service.TemperatureService;
import io.kubernetes.client.openapi.ApiException;

@RestController
@RequestMapping("/state")
public class StateController {

	private final StateService stateService;
	private final PodService podService;
	private final NodeService nodeService;
	private final ClusterService clusterService;
	private final TemperatureService temperatureService;

	public StateController(StateService stateService, NodeService nodeService, PodService podService,
			ClusterService clusterService, TemperatureService temperatureService) {
		this.stateService = stateService;
		this.nodeService = nodeService;
		this.podService = podService;
		this.clusterService = clusterService;
		this.temperatureService = temperatureService;
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
	public List<NodeMetrics> nodes() throws ApiException {
		List<Node> nodes = this.nodeService.get();
		List<Metrics> metricss = this.clusterService.metrics();
		List<NodeMetrics> response = new ArrayList<NodeMetrics>();
		for (Node node : nodes) {
			NodeMetrics nodeMetrics = NodeMetrics.fromNode(node);
			for (Metrics metrics : metricss) {
				if (metrics.getNode().contentEquals(node.getName())) {
					nodeMetrics.setMetrics(metrics);
					break;
				}
			}
			nodeMetrics.setCpuTemperature(this.temperatureService.get(nodeMetrics.getName()));

			response.add(nodeMetrics);
		}
		return response;
	}
}
