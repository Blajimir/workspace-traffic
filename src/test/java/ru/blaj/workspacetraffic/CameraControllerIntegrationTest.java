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
import ru.blaj.workspacetraffic.service.CameraService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Log
public class CameraControllerIntegrationTest {

    @Autowired
    private CameraService cameraService;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void createTest(){

        Camera camera = UtilTest.buildCamera();

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Camera> entity = new HttpEntity<Camera>(camera, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                UtilTest.getURL("/api/camera"),
                HttpMethod.POST,entity,String.class);
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }

    @Test
    public void getAllTest(){

        Camera camera = cameraService.addCamera(UtilTest.buildCamera());
        Assert.assertNotNull(camera);
        camera = cameraService.addCamera(UtilTest.buildCamera());
        Assert.assertNotNull(camera);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                UtilTest.getURL("/api/camera"),
                HttpMethod.GET,entity,String.class);
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }

    @Test
    public void getTest(){
        Camera camera = cameraService.addCamera(UtilTest.buildCamera());
        Assert.assertNotNull(camera);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                UtilTest.getURL("/api/camera/{id}"),
                HttpMethod.GET,entity,String.class,camera.getId());
        System.out.println(String.format("%nTest result:%n%s%n", response.getBody()));
    }
}
