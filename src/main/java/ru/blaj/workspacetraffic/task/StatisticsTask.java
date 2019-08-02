package ru.blaj.workspacetraffic.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Component
@Log
public class StatisticsTask {
    @Autowired
    private StatisticalUnitService unitService;
    @Autowired
    private CameraService cameraService;

    @Scheduled(cron = "0 */2 * * * ?")
    public void statisticsCollection(){
        cameraService.getAllCameras().stream().filter(Camera::isActive).map(unitService::saveUnitFromCamera)
                .forEach(unit -> {
                    if(unit != null){
                        log.info(String.format("Save unit for camera: %d  unit id: %d use zone: %b count: %d",
                                unit.getCameraId(), unit.getId(), unit.isUseZone(), unit.getCount()));
                    }else{
                        log.warning("Something wrong! Return StatisticalUnit is null");
                    }
                });
    }
}
