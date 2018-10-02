package ru.blaj.workspacetraffic;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.blaj.workspacetraffic.model.*;
import ru.blaj.workspacetraffic.service.AzureVisionService;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrafficTest {
    private static final String STREAM_URL_YT = "https://r3---sn-xguxaxjvh-bvwe.googlevideo.com/videoplayback?initcwndbps=448750&cmbypass=yes&source=yt_live_broadcast&sparams=aitags%2Ccmbypass%2Cei%2Cgcr%2Cgir%2Chang%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clive%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cnoclen%2Cpl%2Crequiressl%2Csource%2Cexpire&ipbits=0&expire=1537446850&gcr=ru&mm=32&pl=20&mn=sn-xguxaxjvh-bvwe&id=UDsPPyXgaFM.0&requiressl=yes&ip=176.215.189.97&signature=7D48CEDDF80E157A8A14004BE2BD9D7C1D5A5C68.8B71E41D054715FB699B7686013D838AFC8896E4&live=1&mt=1537425138&mv=m&ms=lv&hang=1&c=WEB&ei=YT-jW4SjOp337ASs2YyQDg&gir=yes&keepalive=yes&mime=video%2Fmp4&key=yt6&itag=136&aitags=133%2C134%2C135%2C136%2C137%2C160&noclen=1&alr=yes&cpn=PUv3PwL8nXUPbvnA";
    private static final String STREAM_URL_CAM = "http://220.240.123.205/mjpg/video.mjpg?d=1537775591313";

    @Test
    public void getJpegFromMp4Video() throws IOException {
        URL url = new URL(STREAM_URL_YT);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        Java2DFrameConverter converter = new Java2DFrameConverter();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(huc.getInputStream());
        grabber.setFormat("mp4");
        System.out.println(huc.getContentType());
        System.out.println(huc.getResponseCode());
        grabber.start();
        Frame frame = grabber.grab();
        if (frame != null) {
            BufferedImage bImage = converter.convert(frame);
            ImageIO.write(bImage, "jpeg", new File("c:\\Programming\\temp\\TestMp4ToJpeg\\1.jpeg"));
        }
        grabber.stop();
    }

    @Test
    public void getJpegFromMjpgVideo() throws IOException {
        URL url = new URL(STREAM_URL_CAM);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        Java2DFrameConverter converter = new Java2DFrameConverter();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(huc.getInputStream());
        grabber.setFormat("mpjpeg");
        System.out.println(huc.getContentType());
        System.out.println(huc.getResponseCode());
        grabber.start();
        Frame frame = grabber.grab();
        if (frame != null) {
            BufferedImage bImage = converter.convert(frame);
            ImageIO.write(bImage, "jpeg", new File("c:\\Programming\\temp\\TestMp4ToJpeg\\2.jpeg"));
        }
        grabber.stop();
        huc.disconnect();
    }

    @Test
    public void testConnection() throws IOException {
        //String surl = STREAM_URL_CAM;
        String surl = "http://124.0.0.3";
        URL url = new URL(surl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        System.out.println(huc.getConnectTimeout());
        //System.out.println(huc.getResponseMessage());
    }

    @Test
    public void testAzureCustomVision() throws IOException {
        AzureVisionService visionService = new AzureVisionService();
        ReflectionTestUtils.setField(visionService, "predictionKey", "1040d279ac4540cda6bbc3006265c65f");
        ReflectionTestUtils.setField(visionService, "projectId", UUID.fromString("b62012e6-6cde-4185-b030-f516b986c297"));
        ReflectionTestUtils.setField(visionService, "tagFilter", "busy");
        BufferedImage bi = ImageIO.read(new File("c:\\Programming\\workspace-traffic-dataset\\test\\bbs1.jpg"));
        List<PredictionZone> predictionZones = visionService.getPrediction(bi);
        saveImageWithDrawRect(bi, predictionZones);
    }

    @Test
    public void testCameraAzureCustomVision() throws IOException {
        AzureVisionService visionService = new AzureVisionService();
        ImageUtil imageUtil = new ImageUtil();
        ReflectionTestUtils.setField(visionService, "predictionKey", "1040d279ac4540cda6bbc3006265c65f");
        ReflectionTestUtils.setField(visionService, "projectId", UUID.fromString("b62012e6-6cde-4185-b030-f516b986c297"));
        ReflectionTestUtils.setField(visionService, "tagFilter", "busy");
        BufferedImage bi = ImageIO.read(new File("c:\\Programming\\workspace-traffic-dataset\\test\\bbs1.jpg"));

        List<int[]> areaList = new ArrayList<>();
        areaList.add(new int[]{0, 83, 40, 147, 124});
        areaList.add(new int[]{1, 89, 76, 176, 193});

        List<WorkspaceZone> wZones = areaList.stream().map(ints -> new WorkspaceZone()
                .withName(Integer.toString(ints[0]))
                .withLeft(imageUtil.absoluteToRelative(ints[1], bi.getWidth()))
                .withTop(imageUtil.absoluteToRelative(ints[2], bi.getHeight()))
                .withWidth(imageUtil.absoluteToRelative(ints[3] - ints[1], bi.getWidth()))
                .withHeight(imageUtil.absoluteToRelative(ints[4] - ints[2], bi.getHeight())))
                .collect(Collectors.toList());
        MiddleStructure middleStructure = imageUtil.generateUnionImage(bi,
                wZones.stream().map(zone -> (Zone) zone).collect(Collectors.toList()),
                5, 3);

        List<PredictionZone> predictionZones = visionService.getPrediction(middleStructure.getDest());
        saveImageWithDrawRect(middleStructure.getDest(), predictionZones);
    }

    private void saveImageWithDrawRect(BufferedImage bi, List<PredictionZone> predictionZones) throws IOException {
        ImageUtil imageUtil = new ImageUtil();
        BufferedImage nbi = new BufferedImage(bi.getColorModel(), bi.copyData(null)
                , bi.isAlphaPremultiplied(), null);
        Graphics2D g = nbi.createGraphics();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        predictionZones.forEach(predictionZone -> {
            AbsoluteZone zone = imageUtil.fromZone(predictionZone, bi.getWidth(), bi.getHeight());
            g.drawRect(zone.getLeft(), zone.getTop(), zone.getWidth(), zone.getHeight());
        });
        String filename = String.format("c:\\Programming\\workspace-traffic-dataset\\test\\predict-%d.jpg",
                new Date().getTime());
        ImageIO.write(nbi, "jpeg", new File(filename));
    }

    @Test
    public void iouTest(){
        double[] a = new double[]{4,2,3,3};
        double[] b = new double[]{4,3,3,1};

        double xMax = Math.max(a[0], b[0]);
        double yMax = Math.max(a[1], b[1]);
        double xMin = Math.min(a[0]+a[2], b[0]+b[2]);
        double yMin = Math.min(a[1]+a[3], b[1]+b[3]);

        double interArea = (xMin - xMax) * (yMin - yMax);

        double aArea = a[2] * a[3];
        double bArea = b[2] * b[3];

        double iou = interArea / (aArea + bArea - interArea);

        System.out.println(String.format("%.2f/(%.2f + %.2f - %.2f)=%.2f", interArea, aArea, bArea, interArea, iou));
    }

    @Test
    public void testStreamFlatMap(){
        ImageUtil imageUtil = new ImageUtil();
        List<Zone> zones = new ArrayList<>();
        zones.add(new Zone().withLeft(2).withTop(1).withWidth(4).withHeight(3));
        zones.add(new Zone().withLeft(8).withTop(2).withWidth(3).withHeight(3));

        List<Zone> pZones = new ArrayList<>();
        pZones.add(new Zone().withLeft(4).withTop(1).withWidth(1).withHeight(1));
        pZones.add(new Zone().withLeft(4).withTop(3).withWidth(2).withHeight(2));
        pZones.add(new Zone().withLeft(10).withTop(3).withWidth(2).withHeight(2));
        pZones.add(new Zone().withLeft(3).withTop(3).withWidth(10).withHeight(3));

        System.out.println(zones.stream().map(z -> pZones.stream().filter(pz -> imageUtil.getIoU(z, pz) > 0)
                .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList()));
    }

    @Test
    public void hashTest(){
        WorkspaceZone wzone = new WorkspaceZone()
                .withName("zone0").withLeft(0.0).withTop(0.0).withHeight(0.5).withWidth(0.5);
        Zone zone = (Zone) wzone;
        Zone anotherZone = new Zone();
        System.out.println(String.format("WorkspaceZone id: %d %nZone id: %d %nAnother zone id: %d",
                System.identityHashCode(wzone),
                System.identityHashCode(zone),
                System.identityHashCode(anotherZone)));

        System.out.println(String.format("zones equals: %b%nanother zones equals: %b", wzone == zone, wzone==anotherZone));
    }

}
