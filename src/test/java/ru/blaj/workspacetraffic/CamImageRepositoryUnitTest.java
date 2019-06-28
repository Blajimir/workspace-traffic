package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.repository.CamImageRepository;
import ru.blaj.workspacetraffic.service.CamImageService;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/workspace-traffic"
        ,"spring.jpa.hibernate.ddl-auto=create-drop"
        ,"spring.jpa.show-sql=false"
        ,"app.own-tf-od-service.url=http://localhost:8087"})
public class CamImageRepositoryUnitTest {

    @Autowired
    private CameraService cameraService;
    @Autowired
    private StatisticalUnitService statisticalUnitService;
    @Autowired
    private CamImageRepository camImageRepository;
    @Autowired
    private CamImageService camImageService;

    @Test
    public void countMaxMinTest(){
        String url = "https://image.shutterstock.com/z/stock-photo-group-of-people-602783837.jpg";
        Camera camera = cameraService.addCamera(UtilTest.buildCamera(url, true));
        Assert.assertNotNull(camera);
        int count = 5;
        IntStream.range(0,count).forEach(value -> statisticalUnitService.saveUnitFromCamera(camera));
        long camCount = camImageRepository.count();
        System.out.println("count: "+camCount);
        Assert.assertTrue(camCount>0);
        long max = camImageRepository.getMaxId();
        System.out.println("max: "+max);
        Assert.assertEquals(count, max);
        long min =  camImageRepository.getMinId();
        System.out.println("min: "+min);
        Assert.assertEquals(1, min);
    }

    @Test
    public void deleteHalfOfCamImagesTest(){
        String url = "https://image.shutterstock.com/z/stock-photo-group-of-people-602783837.jpg";
        Camera camera = cameraService.addCamera(UtilTest.buildCamera(url, true));
        Assert.assertNotNull(camera);
        int count = 6;
        IntStream.range(0,count).forEach(value -> statisticalUnitService.saveUnitFromCamera(camera));
        long camCount = camImageRepository.count();
        System.out.println("count: "+camCount);
        Assert.assertTrue(camCount>0);
        long halfCount = camCount/2;
        System.out.println("halfCount: "+halfCount);
        camImageService.deleteHalfOfCamImages();
        Assert.assertEquals(halfCount, camImageRepository.count());
        System.out.println("count: "+camImageRepository.count());
    }

}
