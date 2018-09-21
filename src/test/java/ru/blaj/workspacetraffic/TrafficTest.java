package ru.blaj.workspacetraffic;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrafficTest {
    private static final String STREAM_URL = "https://r3---sn-xguxaxjvh-bvwe.googlevideo.com/videoplayback?initcwndbps=448750&cmbypass=yes&source=yt_live_broadcast&sparams=aitags%2Ccmbypass%2Cei%2Cgcr%2Cgir%2Chang%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clive%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cnoclen%2Cpl%2Crequiressl%2Csource%2Cexpire&ipbits=0&expire=1537446850&gcr=ru&mm=32&pl=20&mn=sn-xguxaxjvh-bvwe&id=UDsPPyXgaFM.0&requiressl=yes&ip=176.215.189.97&signature=7D48CEDDF80E157A8A14004BE2BD9D7C1D5A5C68.8B71E41D054715FB699B7686013D838AFC8896E4&live=1&mt=1537425138&mv=m&ms=lv&hang=1&c=WEB&ei=YT-jW4SjOp337ASs2YyQDg&gir=yes&keepalive=yes&mime=video%2Fmp4&key=yt6&itag=136&aitags=133%2C134%2C135%2C136%2C137%2C160&noclen=1&alr=yes&cpn=PUv3PwL8nXUPbvnA";
    @Test
    public void getJpegFromVideo() throws IOException {
        URL url = new URL(STREAM_URL);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        Java2DFrameConverter converter = new Java2DFrameConverter();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(huc.getInputStream());
        grabber.setFormat("mp4");
        System.out.println(huc.getContentType());
        System.out.println(huc.getResponseCode());
        grabber.start();
        Frame frame = grabber.grab();
        if(frame!=null){
            BufferedImage bImage=converter.convert(frame);
            ImageIO.write(bImage, "jpeg", new File("c:\\Programming\\temp\\TestMp4ToJpeg\\1.jpeg"));
        }
        grabber.stop();
    }
}
