package ru.blaj.workspacetraffic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.service.CameraService;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(path="/api/camera", produces = "application/json;charset=utf-8")
public class CameraController {
    @Autowired
    private CameraService cameraService;

    @GetMapping
    public Collection<Camera> getAllCameras(){
        return cameraService.getAllCameras();
    }

    @GetMapping(path = "/{id}")
    public Camera getCamera(@NotNull @PathVariable("id") Long id){
        return cameraService.getCamera(id);
    }

    @GetMapping(path = "/image/{id}")
    public ResponseEntity<?> getImage(@NotNull @PathVariable("id") Long id){
        return Optional.ofNullable(cameraService.getImageFromCameraAsJpegBase64(id))
                .map(s -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("image/jpeg;base64"))
                        .header(HttpHeaders.CONTENT_ENCODING,"base64")
                        .header(HttpHeaders.TRANSFER_ENCODING, "base64")
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"cam_screenshot.jpg\"")
                        .body(s))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping
    public Camera addCamera(@NotNull @RequestBody Camera camera){
        return cameraService.addCamera(camera);
    }

    @PutMapping
    public Camera saveCamera(@NotNull @RequestBody Camera camera){
        return cameraService.saveCamera(camera);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteCamera(@NotNull @PathVariable("id") Long id){
        cameraService.deleteCamera(id);
    }
}
