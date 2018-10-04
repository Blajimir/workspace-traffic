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
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.service.CameraService;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Log
public class CameraControllerIntegrationTest {

    @Autowired
    private CameraService cameraService;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void createTest(){

        Camera camera = buildCamera();

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Camera> entity = new HttpEntity<Camera>(camera, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/camera"),
                HttpMethod.POST,entity,String.class);
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }

    @Test
    public void getAllTest(){

        Camera camera = cameraService.addCamera(buildCamera());
        Assert.assertNotNull(camera);
        camera = cameraService.addCamera(buildCamera());
        Assert.assertNotNull(camera);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/camera"),
                HttpMethod.GET,entity,String.class);
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }

    @Test
    public void getTest(){
        Camera camera = cameraService.addCamera(buildCamera());
        Assert.assertNotNull(camera);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/camera/{id}"),
                HttpMethod.GET,entity,String.class,camera.getId());
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }

    private Camera buildCamera(){
        Camera camera = new Camera();
        camera.setUrl("http://220.240.123.205/mjpg/video.mjpg");

        List<WorkspaceZone> zones = new ArrayList<>();
        WorkspaceZone zone = new WorkspaceZone()
                .withName("test1")
                .withLeft(0.1).withTop(0.1)
                .withWidth(0.3).withHeight(0.2)
                .withCamera(camera);
        zones.add(zone);
        zone = new WorkspaceZone()
                .withName("test2")
                .withLeft(0.4).withTop(0.2)
                .withWidth(0.3).withHeight(0.2)
                .withCamera(camera);
        zones.add(zone);

        camera.setZones(zones);
        return camera;
    }
    private String getURL(String path){
        return String.format("http://localhost:%d%s",8085,path);
    }
}
