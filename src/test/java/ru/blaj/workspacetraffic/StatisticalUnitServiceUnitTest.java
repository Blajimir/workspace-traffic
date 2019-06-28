package ru.blaj.workspacetraffic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.*;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/workspace-traffic"
        ,"spring.jpa.hibernate.ddl-auto=create-drop"
        ,"spring.jpa.show-sql=true"
        ,"app.own-tf-od-service.url=http://localhost:8087"})
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
    public void testGetPredictionFromCamera() throws IOException {
        String url = "https://image.shutterstock.com/z/stock-photo-group-of-people-602783837.jpg";
        Camera camera = cameraService.addCamera(UtilTest.buildCamera(url, true));
        Assert.assertNotNull(camera);
        StatisticalUnit unit = statisticalUnitService.saveUnitFromCamera(camera);
        Assert.assertNotNull(unit);
        unit = statisticalUnitService.getUnit(unit.getId());
        Assert.assertNotNull(unit);
        System.out.println("---JSON---");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(unit));
        System.out.println("----------");
    }
    @Test
    public void testDeleteCameraAfterSaveStatisticalUnit() throws JsonProcessingException {
        Camera camera = new Camera();
        camera.setUrl("http://tokyodiner.axiscam.net/jpg/image.jpg?d=1539860762288");
        camera.setZones(Collections.emptyList());
        camera.setActive(true);
        camera.setUseZone(false);
        camera = cameraService.addCamera(camera);
        Assert.assertNotNull(camera);

        StatisticalUnit statisticalUnit = new StatisticalUnit()
                .withCamera(camera)
                .withDate(new Date());

        statisticalUnit.addDetectedObject(null, new DetectedObject()
                .withAge(DetectedAgeEnum.ADULT).withGender(DetectedGenderEnum.MALE),new DetectedObject()
                .withAge(DetectedAgeEnum.YOUNG).withGender(DetectedGenderEnum.FEMALE));

        statisticalUnit = statisticalUnitService.addUnit(statisticalUnit);

        Assert.assertNotNull(statisticalUnit.getId());

        ObjectMapper mapper = new ObjectMapper();

        System.out.println();
        System.out.println(mapper.writeValueAsString(statisticalUnit));
        System.out.println();

        long cid = camera.getId();

        cameraService.deleteCamera(cid);
        Assert.assertNull(cameraService.getCamera(cid));

        long suid = statisticalUnit.getId();
        statisticalUnit = unitRepository.findById(suid).orElse(null);
        Assert.assertNull(statisticalUnit);
    }
}
