package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.repository.CameraRepository;
import ru.blaj.workspacetraffic.repository.WorkspaceZoneRepository;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CameraRepositoryUnitTest {

    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private WorkspaceZoneRepository zoneRepository;

    @Test
    public void createTest(){
        Camera camera = new Camera();
        camera.setUrl("http://test.io");

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

        //zoneRepository.saveAll(zones);

        System.out.println(zones);

        camera.setZones(zones);

        camera = cameraRepository.save(camera);

        Assert.assertNotNull(camera.getId());

        zone = camera.getZones().get(0);

        Assert.assertNotNull(zone);

        camera.getZones().remove(zone);

        cameraRepository.save(camera);

        zone.setId(null);
        zone.setName("zone3");

        camera.getZones().add(zone);

        zone = new WorkspaceZone()
                .withName("zone4")
                .withLeft(0.4).withTop(0.2)
                .withWidth(0.3).withHeight(0.2)
                .withCamera(camera);

        camera.getZones().add(zone);

        camera = cameraRepository.save(camera);

        System.out.println("end");

    }
}
