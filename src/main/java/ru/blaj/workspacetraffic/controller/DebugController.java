package ru.blaj.workspacetraffic.controller;


import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.blaj.workspacetraffic.service.CamImageService;

import java.util.Collections;

@RestController
@RequestMapping(path="/api/debug", produces = "application/json;charset=utf-8")
@Log
@Profile("with-clean-cam-image")
public class DebugController {

    @Autowired
    private CamImageService camImageService;

    @GetMapping(path="/count")
    public ResponseEntity<?> getCountCamImage(){
        return ResponseEntity.ok(Collections.singletonMap("count",camImageService.getCamImagesCount()));
    }

    @DeleteMapping(path="/clean-half")
    public ResponseEntity<?> cleanCamImage(){
        long before = camImageService.getCamImagesCount();
        camImageService.deleteHalfOfCamImages();
        long after = camImageService.getCamImagesCount();
        log.info(String.format("Before: %d; After: %d; Delete count: %d", before, after, before - after));
        return ResponseEntity.ok(Collections.singletonMap("remove",before - after));
    }

    @DeleteMapping(path = "/clean")
    public void cleanAll(){
        camImageService.deleteAllCamImages();
    }
}
