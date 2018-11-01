package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticalUnitServiceUnitTest {

    @Autowired
    private StatisticalUnitService statisticalUnitService;
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
}
