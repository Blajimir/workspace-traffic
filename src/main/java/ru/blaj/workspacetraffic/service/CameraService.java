package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.model.Zone;
import ru.blaj.workspacetraffic.repository.CameraRepository;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Класс - сервис необходим для манипуляции объектами класса Camera( @see{@link Camera} )
 *
 * @author Alesandr Kovalev aka blajimir
 */

@Service
@Log
public class CameraService {
    private CameraRepository cameraRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    public Collection<Camera> getAllCameras() {
        return Collections.unmodifiableCollection(cameraRepository.findAll());
    }

    public Camera addCamera(@NotNull Camera camera) {
        Camera result = null;
        if (camera.getId() != null) {
            camera.setId(null);
        }
        if(this.isCameraAvailable(camera)){
            if(Optional.ofNullable(camera.getZones()).orElseGet(Collections::emptyList).isEmpty()){
                WorkspaceZone zone = new WorkspaceZone();
                zone.setName("zone0");
                zone.setTop(0.0);
                zone.setLeft(0.0);
                zone.setHeight(1.0);
                zone.setWidth(1.0);
                camera.setZones(Collections.singletonList(zone));
            }
            result = this.cameraRepository.save(camera);
        }
        return result;
    }

    public Camera getCamera(@NotNull Long id) {
        return Optional.ofNullable(id).filter(aLong -> aLong != 0)
                .map(aLong -> this.cameraRepository.findById(aLong).orElse(null)).orElse(null);
    }
    //TODO: Добавить логику для проверки изменения url камеры
    public Camera saveCamera(@NotNull Camera camera) {
        return Optional.of(camera)
                .filter(cam -> cam.getId() != null && cam.getId() != 0)
                .filter(cam -> !StringUtils.isEmpty(cam.getUrl()))
                .map(cam -> this.cameraRepository.save(cam))
                .orElse(null);
    }

    public void deleteCamera(@NotNull Camera camera) {
        this.cameraRepository.delete(camera);
    }

    public void deleteCamera(@NotNull Long id){
        if(this.cameraRepository.existsById(id)){
            this.cameraRepository.deleteById(id);
        }
    }

    public BufferedImage getImageFromCamera(Camera camera){
        BufferedImage result = null;
        try {
            result = ImageUtil.getImageFromVideo(camera.getUrl(), null);
        } catch (IOException e) {
           log.warning(e.getMessage());
        }
        return result;
    }

    public boolean isConnectionAvailable(@NotNull Camera camera) {
        return ImageUtil.tryConnection(camera.getUrl()).isPresent();
    }

    public boolean isCameraAvailable(@NotNull Camera camera) {
        boolean result = false;
        if (!StringUtils.isEmpty(camera.getUrl())) {
            try {
                if (ImageUtil.getImageFromVideo(camera.getUrl(), null) != null) {
                    result = true;
                }
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
        }
        return result;
    }
}
