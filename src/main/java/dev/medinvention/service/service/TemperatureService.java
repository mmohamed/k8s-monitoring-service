package dev.medinvention.service.service;

import java.util.HashMap;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.medinvention.service.Application;
import dev.medinvention.service.model.FanStatus;
import dev.medinvention.service.model.Response;
import dev.medinvention.service.model.StateRequest;

@Service
public class TemperatureService {
    public static final int FAN_UNCHANGED = 2;
    public static final int FAN_STARTED = 1;
    public static final int FAN_STOPPED = 0;
    public static final int FAN_STOPPED_FOR_RESTARTING = -1;

    @Value("${fan.server.url}")
    private String fanServerURL;

    @Value("${fan.autostart}")
    private String fanAutoStart;

    private RestTemplate restTemplate;

    public void collect(String node, Integer value) {        
        this.push(node, value);
        this.autoCheck();
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

    public RestTemplate getRestTemplate() {
        if (null == this.restTemplate) {
            this.restTemplate = new RestTemplate();
        }
        return this.restTemplate;
    }
    
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    protected int push(String node, Integer value) {
        String ramMetricName = node + ".cputemperature";
        Integer previousValue = Application.ramMetrics.containsKey(ramMetricName)
                ? Application.ramMetrics.get(ramMetricName)
                : 0;
        Application.ramMetrics.put(ramMetricName, value);    
        return previousValue;
    }
    
    protected void clear() {
        Application.ramMetrics.clear();
        Application.historicData.clear();
    }
    
    protected int autoCheck() {
        if (this.isFanEnabled() && this.isAutoEnabled()) {
            // get max nodes temperatures
            float maxNodes = this.getMaxNodeTemperature();

            // running status
            boolean isRunning = this.status().getIsRunning();

            // update historic data
            Application.historicData.put(System.currentTimeMillis() / 1000, maxNodes);
            while (Application.historicData.size() > 3) {
                Application.historicData.pollFirstEntry();
            }

            // calculate range
            float maxTemperature = Float.parseFloat(this.fanAutoStart);
            float minTemperature = maxTemperature * 0.9F;

            // check inefficience running status
            if (maxNodes >= minTemperature && isRunning && Application.historicData.size() >= 3) {                
                Entry<Long, Float> n = Application.historicData.lastEntry(); // n
                Entry<Long, Float> n1 = Application.historicData.lowerEntry(n.getKey()); // n-1
                Entry<Long, Float> n2 = Application.historicData.lowerEntry(n1.getKey()); // n-2

                Float dt = (n.getValue() - n1.getValue()) / (n.getKey() - n1.getKey())
                        + (n1.getValue() - n2.getValue()) / (n1.getKey() - n2.getKey());                
                if (dt / 2 > 0) {
                    this.off();
                    Application.historicData.clear();
                    return TemperatureService.FAN_STOPPED_FOR_RESTARTING;
                }
            }

            // check to run
            if (maxNodes >= maxTemperature && !isRunning) {
                this.on();
                return TemperatureService.FAN_STARTED;
            }

            // check to stop
            if (maxNodes <= minTemperature && isRunning) {
                this.off();
                Application.historicData.clear();
                return TemperatureService.FAN_STOPPED;
            }
        }
        return TemperatureService.FAN_UNCHANGED;
    }
    
    protected float getMaxNodeTemperature() {
        float max = 0F;
        for (String key : Application.ramMetrics.keySet()) {
            if (key.endsWith(".cputemperature") && !key.startsWith("master.")) {
                max = Math.max(max, Application.ramMetrics.get(key) / 1000F);
            }
        }
        return max;
    }
}
