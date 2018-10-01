package ru.blaj.workspacetraffic.util;

import lombok.extern.java.Log;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
@Component
public class ImageUtil {
    //TODO: установить таймаут через properties
    @Value("${app.try-number-camera-conection}")
    private int tryNum;

    public BufferedImage getImageFromVideo(String surl, String format) throws IOException {
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

    public Optional<HttpURLConnection> tryConnection(String surl) {
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

    public byte[] bImageToJpeg(BufferedImage bImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpeg", baos);
        return baos.toByteArray();
    }

    public String bImageToJpegBase64(BufferedImage bImage) throws IOException {
        return Base64.getEncoder().encodeToString(bImageToJpeg(bImage));
    }

    public BufferedImage JpegToBImage(String base64str) throws IOException {
        byte[] buffer = Base64.getDecoder().decode(base64str);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        return ImageIO.read(bais);
    }

    public double getIoU(Zone zone, Zone predictionZone) {

        Zone interZone = new Zone()
                .withLeft(Math.max(zone.getLeft(), predictionZone.getLeft()))
                .withTop(Math.max(zone.getTop(), predictionZone.getTop()));
        interZone.setWidth(Math.min(zone.getLeft() + zone.getWidth(), predictionZone.getLeft() + predictionZone.getWidth()) - interZone.getLeft());
        interZone.setHeight(Math.min(zone.getTop() + zone.getHeight(), predictionZone.getTop() + predictionZone.getHeight()) - interZone.getTop());

        double interArea = Optional.of(interZone)
                .filter(z -> z.getWidth() > 0 && z.getHeight() > 0)
                .map(z -> z.getWidth() * z.getHeight()).orElse(0.0);

        double zoneArea = zone.getWidth() * zone.getHeight();
        double predictionZoneArea = predictionZone.getWidth() * predictionZone.getHeight();

        return interArea / (zoneArea + predictionZoneArea - interArea);
    }

    /**
     * Функция которая склеивает зоны с камеры в одно изображение
     * TODO: дописать javadoc для этой функции
     */
    public MiddleStructure generateUnionImage(BufferedImage source, List<Zone> zones, int offset, int maxCol) {
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

        dest = new BufferedImage(fullWidth + offset, fullHeight + offset, source.getType());
        Graphics2D g = (Graphics2D) dest.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, dest.getWidth(), dest.getHeight());

        for (AbsoluteZone zone : newZones) {
            AbsoluteZone oldZone = oldZones.get(newZones.indexOf(zone));
            g.drawImage(source,
                    zone.getLeft(), zone.getTop(),
                    zone.getLeft() + zone.getWidth(), zone.getTop() + zone.getHeight(),
                    oldZone.getLeft(), oldZone.getTop(),
                    oldZone.getLeft() + oldZone.getWidth(), oldZone.getTop() + oldZone.getHeight(),
                    null);
        }

        result = new MiddleStructure(source, dest, oldZones, newZones);
        return result;
    }

    public float absoluteToRelative(int aValue, int length) {
        return 1.0f / (float) length * (float) aValue;
    }

    public int relativeToAbsolute(double rValue, int length) {
        return (int) (rValue * length);
    }

    public Zone fromAbsoluteZone(AbsoluteZone zone, int width, int height) {
        return new Zone()
                .withLeft(this.absoluteToRelative(zone.getLeft(), width))
                .withTop(this.absoluteToRelative(zone.getTop(), height))
                .withWidth(this.absoluteToRelative(zone.getWidth(), width))
                .withHeight(this.absoluteToRelative(zone.getHeight(), height));
    }

    public AbsoluteZone fromZone(Zone zone, int width, int height) {
        return new AbsoluteZone()
                .withLeft(this.relativeToAbsolute(zone.getLeft(), width))
                .withTop(this.relativeToAbsolute(zone.getTop(), height))
                .withWidth(this.relativeToAbsolute(zone.getWidth(), width))
                .withHeight(this.relativeToAbsolute(zone.getHeight(), height));
    }

    public List<AbsoluteZone> fromZones(List<Zone> zones, int width, int height) {
        return zones.stream()
                .map(zone -> fromZone(zone, width, height))
                .collect(Collectors.toList());
    }

    public List<Zone> fromAbsoluteZones(List<AbsoluteZone> zones, int width, int height) {
        return zones.stream()
                .map(zone -> fromAbsoluteZone(zone, width, height))
                .collect(Collectors.toList());
    }

}
