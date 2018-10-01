package ru.blaj.workspacetraffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.*;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticalUnitService {

    @Autowired
    private StatisticalUnitRepository statisticalUnitRepository;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private AzureVisionService visionService;
    @Autowired
    private ImageUtil imageUtil;
    @Value("${app.azure.custom-vision.trashold-in-percent}")
    private int trashold;

    @Value("${app.with-cam-image}")
    private boolean isWithCamImage;

    /*/@Autowired
    public StatisticalUnitService(StatisticalUnitRepository statisticalUnitRepository) {
        this.statisticalUnitRepository = statisticalUnitRepository;
    }//*/

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
            //TODO: Добавить инмпорт значений offset и maxCol из properties
            MiddleStructure structure = imageUtil.generateUnionImage(bi, toZones(camera.getZones()),5, 4);
            List<PredictionZone> predictionZones = visionService.getPrediction(structure.getDest())
                    .stream().filter(predictionZone -> predictionZone.getProbability()>this.trashold)
                    .collect(Collectors.toList());
           structure.getBetweenZones().forEach((aZone1, aZone2) -> {
               //TODO: допилить проверку на пересечение зон и предикшен зон
               aZone1
           });


        }
        return result;
    }

    private List<Zone> toZones(List<WorkspaceZone> zones){
        return zones.stream().map(zone -> (Zone)zone).collect(Collectors.toList());
    }
}
