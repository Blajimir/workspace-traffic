package ru.blaj.workspacetraffic.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.blaj.workspacetraffic.service.CamImageService;

@Component
@Log
@Profile("with-clean-cam-image")
public class DeleteHalfCamVisionTask {
    @Autowired
    CamImageService camImageService;
    @Scheduled(cron = "0 0 0 */2 * ?")
    public void deleteHalfCamImagesBySchedule(){
        long before = camImageService.getCamImagesCount();
        camImageService.deleteHalfOfCamImages();
        long after = camImageService.getCamImagesCount();
        log.info(String.format("Before: %d; After: %d; Delete count: %d", before, after, before - after));
    }
}
