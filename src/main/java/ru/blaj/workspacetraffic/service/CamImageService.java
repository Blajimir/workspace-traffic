package ru.blaj.workspacetraffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.CamImage;
import ru.blaj.workspacetraffic.repository.CamImageRepository;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CamImageService {
    private CamImageRepository camImageRepository;

    @Autowired
    public CamImageService(CamImageRepository camImageRepository) {
        this.camImageRepository = camImageRepository;
    }

    public CamImage addCamImage(@NotNull CamImage camImage) {
        if (camImage.getId() != null) {
            camImage.setId(null);
        }
        return this.camImageRepository.save(camImage);
    }

    public CamImage saveCamImage(@NotNull CamImage camImage) {
        return Optional.of(camImage)
                .filter(ci -> ci.getId() != null && ci.getId() != 0)
                .map(ci -> this.camImageRepository.save(ci)).orElse(null);
    }

    public CamImage getCamImage(@NotNull Long id) {
        return Optional.of(id).filter(aLong -> aLong != 0)
                .map(aLong -> this.camImageRepository.findById(aLong).orElse(null))
                .orElse(null);
    }

    public Collection<CamImage> getAllCamImage(){
        return Collections.unmodifiableCollection(this.camImageRepository.findAll());
    }
    public Page<CamImage> getAllByCameraId(@NotNull Long id, int page, int size){
        Page<CamImage> result = null;
        Pageable pageable = PageRequest.of(page,size, Sort.by("timestamp"));
        result = camImageRepository.findAllByCameraId(id, pageable);
        return result;
    }
    public void deleteCamImage(@NotNull CamImage camImage) {
        this.camImageRepository.delete(camImage);
    }

    public void deleteCamImage(@NotNull Long id) {
        if (this.camImageRepository.existsById(id)) {
            this.camImageRepository.deleteById(id);
        }
    }
}
