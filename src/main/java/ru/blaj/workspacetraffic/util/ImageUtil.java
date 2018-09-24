package ru.blaj.workspacetraffic.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ImageUtil {
    public static BufferedImage getImageFromVideo(String surl, String format) throws IOException {
        BufferedImage result = null;
        URL url = new URL(surl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        Java2DFrameConverter converter = new Java2DFrameConverter();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(huc.getInputStream());
        if(!StringUtils.isEmpty(format)) {
            grabber.setFormat("mpjpeg");
        }
        grabber.start();
        Frame frame = grabber.grab();
        if(frame!=null){
            result=converter.convert(frame);
            //ImageIO.write(bImage, "jpeg", new File("c:\\Programming\\temp\\TestMp4ToJpeg\\2.jpeg"));
        }
        grabber.stop();
        huc.disconnect();
        return result;
    }

    public static byte[] bImageToJpeg (BufferedImage bImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpeg", baos);
        return baos.toByteArray();
    }

    public static String bImageToJpegBase64 (BufferedImage bImage) throws IOException {
        return Base64.getEncoder().encodeToString(bImageToJpeg(bImage));
    }

    public static BufferedImage JpegToBImage(String base64str) throws IOException {
        byte[] buffer = Base64.getDecoder().decode(base64str);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        return ImageIO.read(bais);
    }
}
