package ru.blaj.workspacetraffic;

import ru.blaj.workspacetraffic.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UtilTest {


    private static final Random RANDOM = new Random();

    private static int nextIntRange(int min, int max){
        if(min>=max)
            throw new IllegalArgumentException("max must be greater than min");
        return RANDOM.nextInt(max - min)+min;
    }

    public static List<StatisticalUnit> buildUnits(Camera camera){
        return buildUnits(camera, 2,5);
    }

    public static List<StatisticalUnit> buildUnits(Camera camera, int min, int max){
        return IntStream.range(0, nextIntRange(min, max))
                .mapToObj(value -> buildUnit(camera))
                .collect(Collectors.toList());
    }

    public static StatisticalUnit buildUnit(Camera camera){
        return new StatisticalUnit()
                .withCamera(camera)
                .withDetectedObjects(getRandomDetectedObjectList(camera))
                .withDate(new Date());
    }

    public static Camera buildCamera(){
       return buildCamera("http://220.240.123.205/mjpg/video.mjpg", true);
    }

    public static Camera buildCamera(String url, boolean withZone){
        Camera camera = new Camera();
        camera.setUrl(url);
        camera.setUseZone(withZone);
        if(withZone){
            List<WorkspaceZone> zones = new ArrayList<>();
            WorkspaceZone zone = new WorkspaceZone()
                    .withName("test1")
                    .withLeft(0.1).withTop(0.1)
                    .withWidth(0.3).withHeight(0.2)
                    .withCamera(camera);
            zones.add(zone);
            zone = new WorkspaceZone()
                    .withName("test2")
                    .withLeft(0.4).withTop(0.2)
                    .withWidth(0.3).withHeight(0.2)
                    .withCamera(camera);
            zones.add(zone);

            camera.setZones(zones);
        }
        camera.setActive(true);
        return camera;
    }

    public static  String getURL(String path){
        return String.format("http://localhost:%d%s",8085,path);
    }

    public static Map<Long, DetectedObjects> getRandomDetectedObjectList(Camera camera){
        return getRandomDetectedObjectList(camera, 5);
    }

    public static Map<Long, DetectedObjects> getRandomDetectedObjectList(Camera camera, int bound){
       return camera.getZones().stream().map(zone ->
               new AbstractMap.SimpleEntry<>(zone.getId(),
                       new DetectedObjects().withDetectedList(IntStream.range(0, RANDOM.nextInt(bound)+1)
                       .mapToObj(op -> getRandomDetectedObject())
                       .collect(Collectors.toList()))))
               .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }

    public static DetectedObject getRandomDetectedObject(){
        return new DetectedObject()
                .withGender(DetectedGenderEnum.values()[RANDOM.nextInt(DetectedGenderEnum.values().length)])
                .withAge(DetectedAgeEnum.values()[RANDOM.nextInt(DetectedAgeEnum.values().length)]);
    }
}
