package ru.blaj.workspacetraffic.util;

import lombok.extern.java.Log;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.util.StringUtils;
import ru.blaj.workspacetraffic.model.CamImage;
import ru.blaj.workspacetraffic.model.Zone;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

@Log
public class ImageUtil {
    //TODO: изменить класс на компонент для указания кол-ва попыток и таймаута через properties
    private static int tryNum = 3;

    public static BufferedImage getImageFromVideo(String surl, String format) throws IOException {
        BufferedImage result = null;
        Optional<HttpURLConnection> ohuc = tryConnection(surl);
        if (ohuc.isPresent()) {
            Java2DFrameConverter converter = new Java2DFrameConverter();
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(ohuc.get().getInputStream());
            if (!StringUtils.isEmpty(format)) {
                grabber.setFormat("mpjpeg");
            }
            grabber.start();
            Frame frame = grabber.grab();
            if (frame != null) {
                result = converter.convert(frame);
                //ImageIO.write(bImage, "jpeg", new File("c:\\Programming\\temp\\TestMp4ToJpeg\\2.jpeg"));
            }
            grabber.stop();
            ohuc.get().disconnect();
        }
        return result;
    }

    public static Optional<HttpURLConnection> tryConnection(String surl) {
        HttpURLConnection result = null;
        try {
            URL url = new URL(surl);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            int tryCount = 0;
            while (huc.getResponseCode() != HttpURLConnection.HTTP_OK && tryCount < tryNum) {
                huc.disconnect();
                huc.connect();
                tryCount++;
            }
            if (huc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = huc;
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return Optional.ofNullable(result);
    }

    public static byte[] bImageToJpeg(BufferedImage bImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpeg", baos);
        return baos.toByteArray();
    }

    public static String bImageToJpegBase64(BufferedImage bImage) throws IOException {
        return Base64.getEncoder().encodeToString(bImageToJpeg(bImage));
    }

    public static BufferedImage JpegToBImage(String base64str) throws IOException {
        byte[] buffer = Base64.getDecoder().decode(base64str);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        return ImageIO.read(bais);
    }

    //TODO: допилить функцию склейки зон изображений в одно изображение
    public static BufferedImage generateUnionImage(BufferedImage source, List<Zone> zones, int offset, int maxCol) {
        BufferedImage result = null;
        int fullHeight = offset;
        int fullWidth = 0;
        int height = 0;
        int width = 0;
        boolean full = false;
        int col = 0;
        List<Zone> newZones = new ArrayList<>();
        for (Zone zone : zones) {
            col++;
            full = false;
            //TODO: заменить зоны с относительными координатами на зоны с абсолютными координатами!!!
            newZones.add(absoluteValuesToZone(width, fullHeight,
                    relativeToAbsolute(zone.getWidth(), source.getWidth()),
                    relativeToAbsolute(zone.getHeight(), source.getHeight()),
                    source.getWidth(), source.getHeight()));
            height = Math.max(height, relativeToAbsolute(zone.getHeight(), source.getHeight()) + offset);
            width += relativeToAbsolute(zone.getWidth(), source.getWidth()) + offset;
            if (col == maxCol) {
                col = 0;
                width = offset;
                fullWidth = Math.max(fullWidth, width);
                fullHeight += height;
                full = true;
            }
        }
        if (!full) {
            fullWidth = Math.max(fullWidth, width);
            fullHeight += height;
        }

        result = new BufferedImage(fullWidth, fullHeight, source.getType());
        Graphics2D g = (Graphics2D) result.getGraphics();

        /*g.drawImage(source,
                );*/
        return result;
    }

    public static Zone absoluteValuesToZone(int zl, int zt, int zw, int zh, int w, int h) {
        return new Zone()
                .withLeft(absoluteToRelative(zl, w))
                .withTop(absoluteToRelative(zt, h))
                .withWidth(absoluteToRelative(zw, w))
                .withHeight(absoluteToRelative(zh, h));
    }

    public static List<Integer> zoneToAbsoluteValues(Zone zone, int w, int h) {
        List<Integer> coords = new ArrayList<>();
        coords.add(relativeToAbsolute(zone.getLeft(),w));
        coords.add(relativeToAbsolute(zone.getTop(),h));
        coords.add(relativeToAbsolute(zone.getWidth(),w));
        coords.add(relativeToAbsolute(zone.getHeight(),h));
        return  coords;
    }

    public static float absoluteToRelative(int aValue, int length) {
        return 1.0f / (float) length * (float) aValue;
    }

    public static int relativeToAbsolute(float rValue, int length) {
        return (int) (rValue * length);
    }
}
