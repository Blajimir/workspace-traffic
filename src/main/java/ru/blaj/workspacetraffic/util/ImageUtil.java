package ru.blaj.workspacetraffic.util;

import lombok.extern.java.Log;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.util.StringUtils;
import ru.blaj.workspacetraffic.model.AbsoluteZone;
import ru.blaj.workspacetraffic.model.MiddleStructure;
import ru.blaj.workspacetraffic.model.Zone;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public static MiddleStructure generateUnionImage(BufferedImage source, List<Zone> zones, int offset, int maxCol) {
        MiddleStructure result = null;
        BufferedImage dest = null;
        int fullHeight = offset;
        int fullWidth = 0;
        int height = 0;
        int width = offset;
        boolean full = false;
        int col = 0;
        List<AbsoluteZone> oldZones = fromZones(zones, source.getWidth(), source.getHeight());
        List<AbsoluteZone> newZones = new ArrayList<>();
        for (AbsoluteZone zone : oldZones) {
            col++;
            full = false;
            newZones.add(new AbsoluteZone().withLeft(width).withTop(fullHeight)
                    .withWidth(zone.getWidth()).withHeight(zone.getHeight()));
            height = Math.max(height, zone.getHeight() + offset);
            width += zone.getWidth() + offset;
            if (col == maxCol) {
                col = 0;
                width = 0;
                fullWidth = Math.max(fullWidth, width);
                fullHeight += height;
                full = true;
            }
        }
        if (!full) {
            fullWidth = Math.max(fullWidth, width);
            fullHeight += height;
        }

        dest = new BufferedImage(fullWidth+offset, fullHeight+offset, source.getType());
        Graphics2D g = (Graphics2D) dest.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, dest.getWidth(), dest.getHeight());

        for (AbsoluteZone zone : newZones) {
            AbsoluteZone oldZone = oldZones.get(newZones.indexOf(zone));
            g.drawImage(source,
                    zone.getLeft(), zone.getTop(),
                    zone.getLeft() + zone.getWidth(),zone.getTop() + zone.getHeight(),
                    oldZone.getLeft(), oldZone.getTop(),
                    oldZone.getLeft() + oldZone.getWidth(), oldZone.getTop() + oldZone.getHeight(),
                    null);
        }

        result = new MiddleStructure(source, dest, oldZones, newZones);
        return result;
    }

    public static float absoluteToRelative(int aValue, int length) {
        return 1.0f / (float) length * (float) aValue;
    }

    public static int relativeToAbsolute(double rValue, int length) {
        return (int) (rValue * length);
    }

    public static Zone fromAbsoluteZone(AbsoluteZone zone, int width, int height) {
        return new Zone()
                .withLeft(ImageUtil.absoluteToRelative(zone.getLeft(), width))
                .withTop(ImageUtil.absoluteToRelative(zone.getTop(), height))
                .withWidth(ImageUtil.absoluteToRelative(zone.getWidth(), width))
                .withHeight(ImageUtil.absoluteToRelative(zone.getHeight(), height));
    }

    public static AbsoluteZone fromZone(Zone zone, int width, int height) {
        return new AbsoluteZone()
                .withLeft(ImageUtil.relativeToAbsolute(zone.getLeft(), width))
                .withTop(ImageUtil.relativeToAbsolute(zone.getTop(), height))
                .withWidth(ImageUtil.relativeToAbsolute(zone.getWidth(), width))
                .withHeight(ImageUtil.relativeToAbsolute(zone.getHeight(), height));
    }

    public static List<AbsoluteZone> fromZones(List<Zone> zones, int width, int height) {
        return zones.stream()
                .map(zone -> fromZone(zone, width, height))
                .collect(Collectors.toList());
    }

    public static List<Zone> fromAbsoluteZones(List<AbsoluteZone> zones, int width, int height) {
        return zones.stream()
                .map(zone -> fromAbsoluteZone(zone, width, height))
                .collect(Collectors.toList());
    }

}
