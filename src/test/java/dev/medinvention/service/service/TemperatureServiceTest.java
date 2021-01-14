package dev.medinvention.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import dev.medinvention.service.model.Response;

@SuppressWarnings("deprecation")
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemperatureServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Autowired  
    private TemperatureService temperatureService;
    
    private Response responseStatus;
    
    @Before
    public void setUp() {
        this.temperatureService.setRestTemplate(this.restTemplate);

        ReflectionTestUtils.setField(temperatureService, "fanServerURL", "http://fan-server");
        ReflectionTestUtils.setField(temperatureService, "fanAutoStart", "65");
        
        Response responseOn = new Response();
        responseOn.setStatus(true);
        Mockito.when(restTemplate.getForObject(Matchers.eq("http://fan-server/fan/start"), Matchers.eq(Response.class))).thenReturn(responseOn);
        
        
        Response responseOff = new Response();
        responseOff.setStatus(false);
        Mockito.when(restTemplate.getForObject(Matchers.eq("http://fan-server/fan/stop"), Matchers.eq(Response.class))).thenReturn(responseOff);
        
        this.responseStatus = new Response();
        this.responseStatus.setStatus(false);
        Mockito.when(restTemplate.postForObject(Matchers.eq("http://fan-server/fan/status"), Matchers.any(Object.class), Matchers.eq(Response.class))).thenReturn(responseStatus);
    }

    
    @Test
    public void testPush() {
        this.temperatureService.clear();
        
        Integer value = this.temperatureService.push("node-1", 12);        
        assertEquals(value, 0);
        
        value = this.temperatureService.push("node-1", 2);        
        assertEquals(value, 12);
        
        value = this.temperatureService.push("node-2", 12);
        assertEquals(value, 0);
    }
    
    @Test
    public void testAutocheck() throws InterruptedException {    
        this.temperatureService.clear();
        
        int status = this.temperatureService.autoCheck();
        assertEquals(TemperatureService.FAN_UNCHANGED, status);
        responseStatus.setStatus(false);
        
        this.temperatureService.push("node-1", 60000);
        status = this.temperatureService.autoCheck();
        assertEquals(TemperatureService.FAN_UNCHANGED, status);
        
        this.temperatureService.push("node-2", 75000);
        status = this.temperatureService.autoCheck();
        assertEquals(TemperatureService.FAN_STARTED, status);
        responseStatus.setStatus(true);
        
        this.temperatureService.push("node-3", 64000);
        status = this.temperatureService.autoCheck();
        assertEquals(TemperatureService.FAN_UNCHANGED, status);
        
        this.temperatureService.push("node-2", 58000);
        this.temperatureService.push("node-3", 50000);
        this.temperatureService.push("node-1", 48000);
        status = this.temperatureService.autoCheck();

        assertEquals(TemperatureService.FAN_STOPPED, status);
        responseStatus.setStatus(false);
        
        this.temperatureService.push("node-2", 65000);
        status = this.temperatureService.autoCheck();
        assertEquals(TemperatureService.FAN_STARTED, status);
        responseStatus.setStatus(true);
        
        Float maxTemp = this.temperatureService.getMaxNodeTemperature();
        assertEquals(65F,maxTemp);        
        Thread.sleep(2000);
        
        this.temperatureService.push("node-2", 64000);
        status = this.temperatureService.autoCheck();
        assertEquals(TemperatureService.FAN_UNCHANGED, status);
        
        maxTemp = this.temperatureService.getMaxNodeTemperature();
        assertEquals(64F,maxTemp);        
        Thread.sleep(3000);
        
        this.temperatureService.push("node-2", 66000);
        status = this.temperatureService.autoCheck();
        // ((66 - 64) / 3 + (64 - 65) / 2) / 2 => 2/3 - 1/2 => 1/12 > 0 Restart
        assertEquals(TemperatureService.FAN_STOPPED_FOR_RESTARTING, status);
        responseStatus.setStatus(false);
        
        maxTemp = this.temperatureService.getMaxNodeTemperature();
        assertEquals(66F,maxTemp);                
    }
  
}
