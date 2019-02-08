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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OwnVisionServiceUnitTest {

    @Autowired
    private VisionService visionService;
    @Autowired
    private ImageUtil imageUtil;

    @Test
    public void testGetPrediction() throws IOException {
        String url = "https://videos3.earthcam.com/fecnetwork/14320.flv/chunklist_w58895686.m3u8";
        BufferedImage bi = imageUtil.getImageFromVideo(url, null);
        Assert.assertNotNull(bi);
        List<PredictionZone> pzList  = visionService.getPrediction(bi);
        Assert.assertNotNull(pzList);
        //Assert.assertTrue(pzList.size()>0);
        System.out.println(pzList);
    }
}
