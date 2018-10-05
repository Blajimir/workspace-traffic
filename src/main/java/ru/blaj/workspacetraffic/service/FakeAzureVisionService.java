package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.PredictionZone;
import ru.blaj.workspacetraffic.model.WorkspaceZone;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("test")
@Log
public class FakeAzureVisionService implements VisionService{

    @Autowired
    private CameraService cameraService;

    private boolean busy;

    @PostConstruct
    private void init(){
        generateBusy();

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
    }

    @Override
    public List<PredictionZone> getPrediction(BufferedImage bi) {
        return null;
    }

    @Scheduled(cron = "0 */20 * * * ?")
    public void generateBusy(){
        this.busy = Math.random() > 0.5;
        log.info("Change busy value to "+this.busy);
    }
}
