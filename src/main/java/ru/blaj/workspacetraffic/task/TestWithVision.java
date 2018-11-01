package ru.blaj.workspacetraffic.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.service.CameraService;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Component
@Profile("tst-with-vision")
@Log
public class TestWithVision {
    @Autowired
    private CameraService cameraService;
    @PostConstruct
    public void init(){
        Camera camera = new Camera();
        camera.setUrl("http://tokyodiner.axiscam.net/jpg/image.jpg?d=1539860762288");
        camera.setZones(Collections.emptyList());
        camera.setActive(true);
        camera.setUseZone(false);
        camera = cameraService.addCamera(camera);
        assert camera != null;
        log.info("Init camera: "+camera.toString());
    }
}
