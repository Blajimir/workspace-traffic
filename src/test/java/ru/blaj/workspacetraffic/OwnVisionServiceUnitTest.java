package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.PredictionZone;
import ru.blaj.workspacetraffic.service.VisionService;
import ru.blaj.workspacetraffic.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/workspace-traffic"
        ,"spring.jpa.hibernate.ddl-auto=create-drop"
        ,"app.own-tf-od-service.url=http://localhost:8087"})
public class OwnVisionServiceUnitTest {

    @Autowired
    private VisionService visionService;
    @Autowired
    private ImageUtil imageUtil;

    @Test
    public void testGetPrediction() throws IOException {
        //String url = "https://videos3.earthcam.com/fecnetwork/14320.flv/chunklist_w58895686.m3u8";
        String url = "https://image.shutterstock.com/z/stock-photo-group-of-people-602783837.jpg";
        BufferedImage bi = imageUtil.getImageFromVideo(url);
        Assert.assertNotNull(bi);
        List<PredictionZone> pzList  = visionService.getPrediction(bi);
        Assert.assertNotNull(pzList);
        //Assert.assertTrue(pzList.size()>0);
        System.out.println(pzList);
    }

}
