package ru.blaj.workspacetraffic;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/workspace-traffic"
                ,"spring.jpa.hibernate.ddl-auto=create-drop"
                ,"spring.jpa.show-sql=true"
                ,"app.own-tf-od-service.url=http://localhost:8087"})
@Log
public class HealthCheckControllerIntegrationTest {
    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testHealthCheck(){
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                UtilTest.getURL("/api/health"),
                HttpMethod.GET,entity,String.class);
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }
}
