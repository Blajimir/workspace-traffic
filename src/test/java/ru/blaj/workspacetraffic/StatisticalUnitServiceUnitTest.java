package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.util.Collections;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticalUnitServiceUnitTest {

    @Autowired
    private StatisticalUnitService statisticalUnitService;
    @Autowired
    private StatisticalUnitRepository unitRepository;
    @Autowired
    private CameraService cameraService;

    @Test
    public void testProccessUnitService(){

        Camera camera = new Camera();
        camera.setUrl("http://tokyodiner.axiscam.net/jpg/image.jpg?d=1539860762288");
        camera.setZones(Collections.emptyList());
        camera.setActive(true);
        camera.setUseZone(false);
        camera = cameraService.addCamera(camera);
        Assert.assertNotNull(camera);
        System.out.println("\n\nCamera saved\n\n");
        StatisticalUnit statisticalUnit = statisticalUnitService.saveUnitFromCamera(camera);

        Assert.assertNotNull(statisticalUnit);

    }

    @Test
    public void testDeleteCameraAfterSaveStatisticalUnit(){
        Camera camera = new Camera();
        camera.setUrl("http://tokyodiner.axiscam.net/jpg/image.jpg?d=1539860762288");
        camera.setZones(Collections.emptyList());
        camera.setActive(true);
        camera.setUseZone(false);
        camera = cameraService.addCamera(camera);
        Assert.assertNotNull(camera);

        StatisticalUnit statisticalUnit = new StatisticalUnit()
                .withCamera(camera)
                .withCount(4L)
                .withDate(new Date());

        statisticalUnit = statisticalUnitService.addUnit(statisticalUnit);

        Assert.assertNotNull(statisticalUnit.getId());

        long cid = camera.getId();

        cameraService.deleteCamera(cid);
        Assert.assertNull(cameraService.getCamera(cid));

        long suid = statisticalUnit.getId();
        statisticalUnit = unitRepository.findById(suid).orElse(null);
        Assert.assertNull(statisticalUnit);
    }
}
