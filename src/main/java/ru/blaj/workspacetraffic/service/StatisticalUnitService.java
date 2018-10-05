package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.*;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log
public class StatisticalUnitService {

    @Autowired
    private StatisticalUnitRepository statisticalUnitRepository;
    @Autowired
    private CamImageService camImageService;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private VisionService visionService;
    @Autowired
    private ImageUtil imageUtil;
    @Value("${app.with-cam-image}")
    private boolean withCamImage;
    @Value("${app.azure.custom-vision.prediction-trashold-in-percent}")
    private int predictionTrashold;
    @Value("${app.azure.custom-vision.iou-trashold}")
    private float iouTrashold;
    @Value("${app.union-image.offset}")
    private int offset;
    @Value("${app.union-image.colums}")
    private int colNum;

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

    public StatisticalUnit getUnit(@NotNull Long id) {
        return Optional.of(id).filter(il -> il != 0)
                .map(il -> this.statisticalUnitRepository.findById(il).orElse(null))
                .orElse(null);
    }

    public StatisticalUnit saveUnit(@NotNull StatisticalUnit unit) {
        return Optional.of(unit)
                .filter(u -> Optional.ofNullable(unit.getId()).orElse(0L) != 0)
                .map(u -> this.statisticalUnitRepository.save(u))
                .orElse(null);
    }

    public StatisticalUnit addUnit(@NotNull StatisticalUnit unit) {
        if (unit.getId() != null) {
            unit.setId(null);
        }
        return this.statisticalUnitRepository.save(unit);
    }

    /**
     * Эта функция опрашивает камеру и при наличии изображения, отправляет его в когнитивный сервис "custom vision"
     * после получения результатов от когнетивного сервиса она сохраняет полученные результаты в БД как объекты
     * StatisticalUnit {@link StatisticalUnit}
     *
     * @param camera - объект класса {@link Camera}, объект не должен быть {@literal null}
     * @return возвращает статистику по полученных от когнетивного сервиса в сиде коллекции объектов {@link StatisticalUnit}
     */
    public Collection<StatisticalUnit> saveUnitFromCamera(@NotNull Camera camera) {
        Collection<StatisticalUnit> result = Collections.emptyList();
        BufferedImage bi = cameraService.getImageFromCamera(camera);
        if (bi != null) {
            //TODO: Добавить инмпорт значений offset и maxCol из properties
            MiddleStructure structure = imageUtil.generateUnionImage(bi, toZones(camera.getZones()), offset, colNum);
            List<PredictionZone> predictionZones = visionService.getPrediction(structure.getDest())
                    .stream().filter(predictionZone -> predictionZone.getProbability() > this.predictionTrashold)
                    .collect(Collectors.toList());

            List<PredictionZone> sourcePredictionZones = toSourcesPredictionZones(predictionZones, structure);

            if (withCamImage) {
                try {
                    CamImage camImage = new CamImage();
                    camImage.setCameraId(camera.getId());
                    camImage.setPredictions(predictionZones);
                    camImage.setContentImage(imageUtil.bImageToJpegBase64(structure.getSource()));
                    camImage.setUnionContentImage(imageUtil.bImageToJpegBase64(structure.getDest()));
                    camImageService.saveCamImage(camImage);
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }

            result = statisticalUnitRepository.saveAll(camera.getZones().stream()
                    .map(zone -> new StatisticalUnit()
                            .withCamera(camera)
                            .withBusy(sourcePredictionZones.stream().anyMatch(pzone ->
                                    imageUtil.getIoU(zone, pzone) > iouTrashold))
                            .withZone(zone)).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * вспомогательный метод для  TODO: дописать javadoc
     * */
    private List<PredictionZone> toSourcesPredictionZones(List<PredictionZone> predictionZones, MiddleStructure structure) {
        return structure.getUnionZones().stream().map(unit -> {
            Zone destZone = imageUtil.fromAbsoluteZone(unit.getDestAbsoluteZone(),
                    structure.getDest().getWidth(), structure.getDest().getHeight());
            return predictionZones.stream().filter(pZone -> imageUtil.getIoU(destZone, pZone) > 0.0)
                    .map(pZone -> pZone
                            .withLeft(destZone.getLeft() - pZone.getLeft() + unit.getSourceZone().getLeft())
                            .withTop(destZone.getTop() - pZone.getTop() + unit.getSourceZone().getTop()))
                    .collect(Collectors.toList());
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<Zone> toZones(List<WorkspaceZone> zones) {
        return zones.stream().map(zone -> (Zone) zone).collect(Collectors.toList());
    }
}
