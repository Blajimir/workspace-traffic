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
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@Profile("test")
@Log
public class FakeAzureVisionService implements VisionService {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ImageUtil imageUtil;

    private Random random;

    private boolean busy;

    @PostConstruct
    private void init() {
        generateBusy();

        random = new Random();

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
        camera = cameraService.addCamera(camera);
        assert camera.getId() != null;
        log.info(String.format("Post process saving camera for fake azure vision service: {id: %d, url: %s}",
                camera.getId(), camera.getUrl()));
    }

    @Override
    public List<PredictionZone> getPrediction(BufferedImage bi) {
        List<PredictionZone> predictions = new ArrayList<>();
        switch (this.random.nextInt(3)) {
            case 0:
                predictions = Collections.emptyList();
                break;
            case 1:
                predictions.add(new PredictionZone().withLeft(0.0).withTop(0.0)
                        .withHeight(0.5).withWidth(0.5)
                        .withTag("busy").withProbability(0.6 * 100.0));
                break;
            case 2:
                predictions.add(new PredictionZone().withLeft(0.0).withTop(0.0)
                        .withHeight(0.4).withWidth(0.5)
                        .withTag("busy").withProbability(0.6 * 100.0));
                predictions.add(new PredictionZone().withLeft(0.5).withTop(0.0)
                        .withHeight(0.5).withWidth(0.5)
                        .withTag("busy").withProbability(0.3 * 100.0));
                break;

        }
        log.info(predictions.toString());
        return predictions;
    }

    @Scheduled(cron = "0 */20 * * * ?")
    public void generateBusy() {
        this.busy = Math.random() > 0.5;
        log.info("Change busy value to " + this.busy);
    }
}
