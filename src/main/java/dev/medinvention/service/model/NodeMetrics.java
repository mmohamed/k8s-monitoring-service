package dev.medinvention.service.model;

import dev.medinvention.core.model.Metrics;
import dev.medinvention.core.model.Node;

public class NodeMetrics extends Node {

	private Metrics metrics;

	public static NodeMetrics fromNode(Node node) {
		NodeMetrics builded = new NodeMetrics();
		
		builded.setAddresse(node.getAddresse());
		builded.setCpu(node.getCpu());
		builded.setIsMaster(node.getIsMaster());
		builded.setMemory(node.getMemory());
		builded.setName(node.getName());
		builded.setPods(node.getPods());
		builded.setStatus(node.getStatus());
		
		return builded;
	}
	
	public Metrics getMetrics() {
		return metrics;
	}

	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}
}
