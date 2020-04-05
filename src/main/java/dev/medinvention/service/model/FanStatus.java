package dev.medinvention.service.model;

public class FanStatus {

	private Boolean isEnabled;
	private Boolean isRunning;
	private Boolean isAutoMode;
	private Float minTemperature;
	private Float maxTemperature;
	private String message;

	public static FanStatus disabled() {
		FanStatus status = new FanStatus();
		status.setIsEnabled(false);
		status.setIsAutoMode(false);
		status.setIsRunning(false);
		status.setMessage("Disabled Fan service");
		return status;
	}

	public static FanStatus fromOffResponse(Response response, Boolean auto, Float maxTemperature, Float minTemperature) {
		FanStatus status = new FanStatus();
		status.setIsEnabled(true);
		status.setIsAutoMode(auto);
		status.setMaxTemperature(maxTemperature);
		status.setMinTemperature(minTemperature);
		status.setIsRunning(!response.getStatus());
		status.setMessage(response.getMessage());
		return status;
	}

	public static FanStatus fromOnResponse(Response response, Boolean auto, Float maxTemperature, Float minTemperature) {
		FanStatus status = new FanStatus();
		status.setIsEnabled(true);
		status.setIsAutoMode(auto);
		status.setMaxTemperature(maxTemperature);
		status.setMinTemperature(minTemperature);
		status.setIsRunning(response.getStatus());
		status.setMessage(response.getMessage());
		return status;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIsAutoMode() {
		return isAutoMode;
	}

	public void setIsAutoMode(Boolean isAutoMode) {
		this.isAutoMode = isAutoMode;
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
