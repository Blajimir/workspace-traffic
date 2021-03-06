package ru.blaj.workspacetraffic.util;

import lombok.extern.java.Log;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.blaj.workspacetraffic.model.AbsoluteZone;
import ru.blaj.workspacetraffic.model.MiddleStructure;
import ru.blaj.workspacetraffic.model.Zone;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Component
public class ImageUtil {
    //TODO: установить таймаут через properties
    @Value("${app.try-number-camera-connection}")
    private int tryNum;
    @Value("${app.timeout-camera-connection}")
    private int timeoutConnection;

    /**
     * Эта функция позволяет получить последний кадр из видеопотока камеры по ссылке
     *
     * @param surl - ссылка на источних видеопотока (пока что принимает только стандартные форматы видео в
     *             качестве прямых статичных ссылок)
     * @return кадр в виде изображения в виде объектка {@link BufferedImage}
     */
    public BufferedImage getImageFromVideo(String surl) throws IOException {
        BufferedImage result = null;
        Optional<HttpURLConnection> ohuc = tryConnection(surl);
        Optional<InputStream> ois = Optional.empty();
        if (ohuc.isPresent()) {
            if(this.isLinkFromM3U8(ohuc.get())){
                ois = this.getLinkFromM3U8(surl, ohuc.get());
            }else{
                ois = Optional.of(ohuc.get().getInputStream());
            }
        }
        if(ois.isPresent()){
            Java2DFrameConverter converter = new Java2DFrameConverter();
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(ois.get());
            grabber.start();
            Frame frame = grabber.grabImage();
            if (frame != null) {
                result = converter.convert(frame);
            }
            grabber.stop();
            ohuc.get().disconnect();
        }
        return result;
    }
    /**
     * Эта вспомогательная функция позволяет определить ведет ли ссылка в виде объекта {@link HttpURLConnection}
     * к ресурсу M3U8
     *
     * @param huc - ссылка на источних видеопотока виде объекта {@link HttpURLConnection}
     * @return булевое значение
     */
    public boolean isLinkFromM3U8(HttpURLConnection huc){
        return Optional.of(huc).filter(vhuc -> vhuc.getContentType().toLowerCase().contains("vnd.apple.mpegurl")||
                vhuc.getContentType().toLowerCase().contains("mpegurl")).isPresent();
    }


    /**
     * Эта вспомогательная функция позволяет получить из ссылки {@link HttpURLConnection}
     * к ресурсу M3U8 ссылку на конечный файл видеопотока, из плейлиста M3U8 берется последний файл.
     * Данный алгоритм принимает первый паратетр как корневой из
     *
     * @param huc - ссылка на источних видеопотока виде объекта {@link HttpURLConnection}
     * @return булевое значение
     */
    public Optional<InputStream> getLinkFromM3U8(String surl , @NotNull HttpURLConnection huc) throws IOException {
        Optional<InputStream> result = Optional.empty();
        BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
        //huc.getURL().getPath()
        Optional<HttpURLConnection> ohuc = tryConnection(surl.replace(Arrays.stream(surl.split("/")).reduce((s, s2) -> s2).orElse("")
                , br.lines().reduce((s, s2) -> s2).orElse("")));
        huc.disconnect();
        if(ohuc.isPresent()){
            if(!this.isLinkFromM3U8(ohuc.get())){
                result = Optional.of(ohuc.get().getInputStream());
            }else{
                result = this.getLinkFromM3U8(surl, ohuc.get());
            }
        }
        return result;
    }
    public Optional<HttpURLConnection> tryConnection(String surl) {
        HttpURLConnection result = null;
        try {
            URL url = new URL(surl);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setConnectTimeout(this.timeoutConnection);
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
    /**
     * Intersection over Union
     * */
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

        result = new MiddleStructure(source, dest, zones, oldZones, newZones);
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
