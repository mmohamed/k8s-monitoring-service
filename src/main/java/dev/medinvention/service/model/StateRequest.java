package dev.medinvention.service.model;

import java.util.Map;

public class StateRequest {

	private Map<String, Float> temperatures;
	private boolean autoMode;
	private Float minTemperature;
	private Float maxTemperature;

	public Map<String, Float> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Map<String, Float> temperatures) {
		this.temperatures = temperatures;
	}

	public boolean isAutoMode() {
		return autoMode;
	}

	public void setAutoMode(boolean autoMode) {
		this.autoMode = autoMode;
	}

	public Float getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(Float minTemperature) {
		this.minTemperature = minTemperature;
	}

	public Float getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(Float maxTemperature) {
		this.maxTemperature = maxTemperature;
	}
}
