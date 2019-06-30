package ru.blaj.workspacetraffic;

import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/workspace-traffic"
        ,"spring.jpa.hibernate.ddl-auto=create-drop"
        ,"spring.jpa.show-sql=true"
        ,"app.own-tf-od-service.url=http://localhost:8087"})
@Log
public class StatisticalUnitControllerIntegrationTest {
    @Autowired
    CameraService cameraService;
    @Autowired
    StatisticalUnitService unitService;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void getTest(){
        Camera camera = cameraService.addCamera(UtilTest.buildCamera());
        Assert.assertNotNull(camera);
        UtilTest.buildUnits(camera).forEach(unit -> {
            StatisticalUnit u = unitService.addUnit(unit);
            Assert.assertNotNull(u);
        });

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                UtilTest.getURL("/api/statics/{id}"),
                HttpMethod.GET,entity,String.class,camera.getId());
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }

    @Test
    public void testGetPredictionsFromCamera() {
        String url = "https://image.shutterstock.com/z/stock-photo-group-of-people-602783837.jpg";
        Camera camera = cameraService.addCamera(UtilTest.buildCamera(url, true));
        Assert.assertNotNull(camera);
        IntStream.range(0,5).forEach(value -> unitService.saveUnitFromCamera(camera));

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                UtilTest.getURL("/api/statics/{id}"),
                HttpMethod.GET,entity,String.class,camera.getId());
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }


}
