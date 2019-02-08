package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageUtilUnitTest {
    @Autowired
    private ImageUtil imageUtil;

    @Test
    public void testGetImage() throws IOException {
        String surl = "https://cdn-002.whatsupcams.com/hls/hr_samoevent02.m3u8";//"https://videos3.earthcam.com/fecnetwork/14320.flv/chunklist_w58895686.m3u8";
        BufferedImage bi = imageUtil.getImageFromVideo(surl,null);
        Assert.assertNotNull(bi);
        System.out.println();
        System.out.println(imageUtil.bImageToJpegBase64(bi));
        System.out.println();
    }
}
