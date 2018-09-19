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
    private static final String STREAM_URL = "https://r3---sn-xguxaxjvh-bvwe.googlevideo.com/videoplayback?key=yt6&itag=136&mime=video%2Fmp4&live=1&hang=1&ipbits=0&pl=20&aitags=133%2C134%2C135%2C136%2C137%2C160&expire=1537358482&signature=9B31FBC2F3F83A97576FCA240DFBB622AF56BF04.420F5F91338184C0DC5B786B49E1416530B46E7B&cmbypass=yes&gcr=ru&noclen=1&id=UDsPPyXgaFM.0&sparams=aitags%2Ccmbypass%2Cei%2Cgcr%2Cgir%2Chang%2Cid%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clive%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cnoclen%2Cpl%2Crequiressl%2Csource%2Cexpire&mm=32&gir=yes&c=WEB&mn=sn-xguxaxjvh-bvwe&ip=176.215.189.97&ms=lv&mt=1537336548&mv=u&keepalive=yes&ei=MuahW9OHHJP57gTVrpW4Bw&requiressl=yes&source=yt_live_broadcast&alr=yes&cpn=fTky5NtUXRcnDQHO";
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
