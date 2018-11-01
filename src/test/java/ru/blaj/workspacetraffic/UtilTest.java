package ru.blaj.workspacetraffic;

import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.model.WorkspaceZone;

import java.util.*;
import java.util.stream.Collectors;

public class UtilTest {
    public static Collection<StatisticalUnit> buildUnits(Camera camera){
        return camera.getZones().stream().map(zone -> new StatisticalUnit()
                .withCamera(camera)
                .withCount(new Random().nextLong())
                .withDate(new Date())).collect(Collectors.toList());
    }

    public static Camera buildCamera(){
        Camera camera = new Camera();
        camera.setUrl("http://220.240.123.205/mjpg/video.mjpg");

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
        return camera;
    }

    public static  String getURL(String path){
        return String.format("http://localhost:%d%s",8085,path);
    }
}
