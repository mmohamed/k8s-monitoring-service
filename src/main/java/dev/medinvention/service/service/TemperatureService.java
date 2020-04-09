package dev.medinvention.service.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.medinvention.service.Application;
import dev.medinvention.service.model.FanStatus;
import dev.medinvention.service.model.NodeMetrics;
import dev.medinvention.service.model.Response;
import dev.medinvention.service.model.StateRequest;

@Service
public class TemperatureService {

	@Value("${fan.server.url}")
	private String fanServerURL;

	@Value("${fan.autostart}")
	private String fanAutoStart;

	private RestTemplate restTemplate = new RestTemplate();

	public int collect(String node, Integer value) {
		String ramMetricName = node + ".cputemperature";
		Integer previousValue = Application.ramMetrics.containsKey(ramMetricName)
				? Application.ramMetrics.get(ramMetricName)
				: 0;
		Application.ramMetrics.put(ramMetricName, value);
		this.autoCheck();
		return previousValue;
	}

	public float get(String node) {
		String ramMetricName = node + ".cputemperature";
		if (Application.ramMetrics.containsKey(ramMetricName)) {
			return (Application.ramMetrics.get(ramMetricName) / 1000F);
		}
		return 0F;
	}

	public boolean isAutoEnabled() {
		return this.fanAutoStart != null && !this.fanAutoStart.isEmpty();
	}

	public boolean isFanEnabled() {
		return this.fanServerURL != null && !this.fanServerURL.isEmpty();
	}

	public FanStatus on() {
		if (!this.isFanEnabled()) {
			return FanStatus.disabled();
		}

		Response response = restTemplate.getForObject(fanServerURL + "/fan/start", Response.class);

		return FanStatus.fromOnResponse(response, this.isAutoEnabled(),
				this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) : null,
				this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) * 0.9F : null);
	}

	public FanStatus off() {
		if (!this.isFanEnabled()) {
			return FanStatus.disabled();
		}

		Response response = restTemplate.getForObject(fanServerURL + "/fan/stop", Response.class);

		return FanStatus.fromOffResponse(response, this.isAutoEnabled(),
				this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) : null,
				this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) * 0.9F : null);
	}

	public FanStatus status() {
		if (!this.isFanEnabled()) {
			return FanStatus.disabled();
		}

		HashMap<String, Float> temperatures = new HashMap<String, Float>();

		for (String key : Application.ramMetrics.keySet()) {
			if (key.endsWith(".cputemperature")) {
				String node = key.replace(".cputemperature", "");
				temperatures.put(node, this.get(node));
			}
		}

		StateRequest stateRequest = new StateRequest();
		stateRequest.setTemperatures(temperatures);
		stateRequest.setAutoMode(this.isAutoEnabled());
		stateRequest.setMaxTemperature(this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) : null);
		stateRequest.setMinTemperature(this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) * 0.9F : null);

		HttpEntity<StateRequest> request = new HttpEntity<StateRequest>(stateRequest);

		Response response = restTemplate.postForObject(fanServerURL + "/fan/status", request, Response.class);

		return FanStatus.fromOnResponse(response, this.isAutoEnabled(),
				this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) : null,
				this.isAutoEnabled() ? Float.valueOf(this.fanAutoStart) * 0.9F : null);
	}

	private void autoCheck() {
		if (this.isFanEnabled() && this.isAutoEnabled()) {
			float maxTemperature = Float.valueOf(this.fanAutoStart);
			float minTemperature = maxTemperature * 0.9F;
			boolean isRunning = this.status().getIsRunning();
			// get max nodes temperatures
			float maxNodes = this.getMaxNodeTemperature();
			// check to run
			if (maxNodes >= maxTemperature && !isRunning) {
				this.on();
			}
			// check to stop
			if (maxNodes <= minTemperature && isRunning) {
				this.off();
			}
		}
	}

	private float getMaxNodeTemperature() {
		float max = 0F;
		for (String key : Application.ramMetrics.keySet()) {
			if (key.endsWith(".cputemperature") && !key.startsWith("master.")) {
				max = Math.max(max, Application.ramMetrics.get(key) / 1000F);
			}
		}
		return max;
	}
}
