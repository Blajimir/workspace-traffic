package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.PredictionZone;
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.util.ImageUtil;

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

    @Autowired
    private ImageUtil imageUtil;

    private boolean busy;

    @PostConstruct
    private void init(){
        generateBusy();

        Camera camera = new Camera();
        camera.setUrl("http://220.240.123.205/mjpg/video.mjpg");
        BufferedImage bi = cameraService.getImageFromCamera(camera);
        /*
            <area target="" alt="" title="" href="" coords="468,190,646,463" shape="rect">
            <area target="" alt="" title="" href="" coords="722,247,874,511" shape="rect">
        */

        List<WorkspaceZone> zones = new ArrayList<>();
        WorkspaceZone zone = new WorkspaceZone()
                .withName("test1")
                .withLeft(imageUtil.absoluteToRelative(468, bi.getWidth()))
                .withTop(imageUtil.absoluteToRelative(190, bi.getHeight()))
                .withWidth(imageUtil.absoluteToRelative(646 - 468, bi.getWidth()))
                .withHeight(imageUtil.absoluteToRelative(463 - 190, bi.getHeight()))
                .withCamera(camera);
        zones.add(zone);
        zone = new WorkspaceZone()
                .withName("test2")
                .withLeft(imageUtil.absoluteToRelative(722, bi.getWidth()))
                .withTop(imageUtil.absoluteToRelative(247, bi.getHeight()))
                .withWidth(imageUtil.absoluteToRelative(874 - 722, bi.getWidth()))
                .withHeight(imageUtil.absoluteToRelative(511 - 247, bi.getHeight()))
                .withCamera(camera);
        zones.add(zone);

        camera.setZones(zones);

        cameraService.saveCamera(camera);
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
