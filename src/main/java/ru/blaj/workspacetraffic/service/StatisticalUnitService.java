package ru.blaj.workspacetraffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.Camera;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class StatisticalUnitService {

    @Autowired
    private StatisticalUnitRepository statisticalUnitRepository;
    @Autowired
    private CameraService cameraService;

    @Value("${app.with-cam-image}")
    private boolean isWithCamImage;

    @Autowired
    public StatisticalUnitService(StatisticalUnitRepository statisticalUnitRepository) {
        this.statisticalUnitRepository = statisticalUnitRepository;
    }

    public Collection<StatisticalUnit> getAllUnits() {
        return Collections.unmodifiableCollection(this.statisticalUnitRepository.findAll());
    }

    public Collection<StatisticalUnit> getUnitsByCamera(Long id) {
        return Optional.ofNullable(id).filter(il -> il != 0)
                .map(il -> this.statisticalUnitRepository.findAllByCamera_Id(il))
                .orElse(Collections.emptyList());
    }

    public Collection<StatisticalUnit> getUnitsByCamera(@NotNull Camera camera) {
        return Optional.of(camera).map(cam -> this.getUnitsByCamera(cam.getId())).orElse(Collections.emptyList());
    }

    public Collection<StatisticalUnit> getUnitsByZone(Long id) {
        return Optional.ofNullable(id).filter(il -> il != 0)
                .map(il -> this.statisticalUnitRepository.findAllByZone_Id(id))
                .orElse(Collections.emptyList());
    }

    public Collection<StatisticalUnit> getUnitsByZone(@NotNull WorkspaceZone zone) {
        return Optional.of(zone).map(z -> this.getUnitsByZone(z.getId()))
                .orElse(Collections.emptyList());
    }

    public StatisticalUnit getUnit(@NotNull Long id){
        return Optional.of(id).filter(il -> il!=0)
                .map(il -> this.statisticalUnitRepository.findById(il).orElse(null))
                .orElse(null);
    }

    public StatisticalUnit saveUnit(@NotNull StatisticalUnit unit){
        return Optional.of(unit)
                .filter(u -> Optional.ofNullable(unit.getId()).orElse(0L)!=0)
                .map(u -> this.statisticalUnitRepository.save(u))
                .orElse(null);
    }

    public StatisticalUnit addUnit(@NotNull StatisticalUnit unit){
        if(unit.getId()!=null){
            unit.setId(null);
        }
        return this.statisticalUnitRepository.save(unit);
    }
    /**
     * Эта функция опрашивает камеру и при наличии изображения, отправляет его в когнитивный сервис custom vision
     * после получения результатов от когнетивного сервиса он сохраняет полученные результаты в БД как объекты
     * StatisticalUnit {@link StatisticalUnit}
     *
     * @param camera - объект класса {@link Camera}, объект не должен быть {@literal null}
     * @return возвращает статистику по полученных от когнетивного сервиса в сиде коллекции объектов {@link StatisticalUnit}
     */
    public Collection<StatisticalUnit> saveUnitFromCamera(@NotNull Camera camera){
        Collection<StatisticalUnit> result = Collections.emptyList();
        BufferedImage bi = cameraService.getImageFromCamera(camera);
        if(bi!=null){
           // bi.
        }
        return result;
    }
}
