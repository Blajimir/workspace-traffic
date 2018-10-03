package ru.blaj.workspacetraffic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.service.CameraService;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping(path="/api/camera", produces = "application/json;charset=utf-8")
public class CameraController {
    @Autowired
    private CameraService cameraService;

    @GetMapping(path = "/")
    public Collection<Camera> getAllCameras(){
        return cameraService.getAllCameras();
    }

    @GetMapping(path = "/{id}")
    public Camera getCamera(@NotNull @PathVariable("id") Long id){
        return cameraService.getCamera(id);
    }

    @PostMapping(path = "/")
    public Camera addCamera(@NotNull @RequestBody Camera camera){
        return cameraService.addCamera(camera);
    }

    @PutMapping(path="/")
    public Camera saveCamera(@NotNull @RequestBody Camera camera){
        return cameraService.saveCamera(camera);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteCamera(@NotNull @PathVariable("id") Long id){
        cameraService.deleteCamera(id);
    }
}
