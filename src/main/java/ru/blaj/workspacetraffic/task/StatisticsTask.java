package ru.blaj.workspacetraffic.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.blaj.workspacetraffic.service.CameraService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import java.util.Collection;

@Component
@Log
public class StatisticsTask {
    @Autowired
    private StatisticalUnitService unitService;
    @Autowired
    private CameraService cameraService;

    @Scheduled(cron = "0 */2 * * * ?")
    public void statisticsCollection(){

        cameraService.getAllCameras().stream().map(unitService::getUnitsByCamera)
                .flatMap(Collection::stream)
                .forEach(unit ->
                        log.info(String.format("Save unit for camera: %d unit: {id: %d, zone-name: %s, busy: %b}",
                                unit.getCamera().getId(), unit.getId(), unit.getZone().getName(), unit.isBusy())));
    }
}
