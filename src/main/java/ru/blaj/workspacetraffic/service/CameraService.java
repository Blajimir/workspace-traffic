package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.repository.CameraRepository;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Класс - сервис необходим для манипуляции объектами класса Camera( @{@link Camera} )
 * @author Alesandr Kovalev aka blajimir
 * */

@Service
@Log
public class CameraService {
    private CameraRepository cameraRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository){
        this.cameraRepository = cameraRepository;
    }

    public List<Camera> getAllCameras(){
        return  cameraRepository.findAll();
    }

    public Camera addCamera(@NotNull Camera camera){
        if(camera.getId()!=null){
            camera.setId(null);
        }
        return this.cameraRepository.save(camera);
    }

    public Camera getCamera(@NotNull Long id){
        return Optional.ofNullable(id).filter(aLong -> aLong!=0)
                .map(aLong -> this.cameraRepository.findById(aLong).orElse(null)).orElse(null);
    }

    public Camera saveCamera(@NotNull Camera camera){
        return Optional.of(camera)
                .filter(cam -> cam.getId()!=null && cam.getId()!=0)
                .filter(cam -> !StringUtils.isEmpty(cam.getUrl()))
                .map(cam -> this.cameraRepository.save(cam))
                .orElse(null);
    }

    public void deleteCamera(@NotNull Camera camera){
        this.cameraRepository.delete(camera);
    }

    public boolean isCameraAvailable(@NotNull Camera camera){
        boolean result = false;
        if(!StringUtils.isEmpty(camera.getUrl())){
            try {
                if(ImageUtil.getImageFromVideo(camera.getUrl(), null)!=null){
                    result = true;
                }
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
        }
        return result;
    }
}
