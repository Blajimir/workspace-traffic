package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.*;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
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
    @Value("${app.filter-tag}")
    private String tagPattern;

    /*/@Autowired
    public StatisticalUnitService(StatisticalUnitRepository statisticalUnitRepository) {
        this.statisticalUnitRepository = statisticalUnitRepository;
    }//

    public Collection<StatisticalUnit> getAllUnits() {
        return Collections.unmodifiableCollection(this.statisticalUnitRepository.findAll());
    }*/

    public Collection<StatisticalUnit> getUnitsByCamera(Long id) {
        return Optional.ofNullable(id).filter(il -> il != 0)
                .map(il -> this.statisticalUnitRepository.findAllByCameraId(il))
                .orElse(Collections.emptyList());
    }

    public Page<StatisticalUnit> getUnitsByCamera(@NotNull Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));
        return this.statisticalUnitRepository.findAllByCameraId(id, pageable);
    }

    public Collection<StatisticalUnit> getUnitsByCamera(@NotNull Camera camera) {
        return Optional.of(camera).map(cam -> this.getUnitsByCamera(cam.getId())).orElse(Collections.emptyList());
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
     * Эта функция опрашивает камеру и при наличии изображения, отправляет его в когнитивный сервис (напр. "custom vision")
     * после получения результатов от когнетивного сервиса она сохраняет полученный результат в БД как объект
     * StatisticalUnit {@link StatisticalUnit}
     *
     * @param camera - объект класса {@link Camera}, объект не должен быть {@literal null}
     * @return возвращает статистичискую единицу с полученными от когнетивного сервиса  данными в виде объекта {@link StatisticalUnit}
     */
    @Transactional
    public StatisticalUnit saveUnitFromCamera(@NotNull Camera camera) {
        StatisticalUnit result = null;
        BufferedImage bi = cameraService.getImageFromCamera(camera);
        if (bi != null) {
            /*Optional<Camera> opCam = Optional.of(camera);
            MiddleStructure structure = opCam
                    .filter(Camera::isUseZone)
                    .map(cam -> imageUtil.generateUnionImage(bi, toZones(cam.getZones()), offset, colNum))
                    .orElseGet(() -> new MiddleStructure().withSource(bi).withDest(bi));*/

            List<PredictionZone> predictionZones = visionService.getPrediction(bi)
                    .stream().filter(predictionZone -> predictionZone.getProbability() > this.predictionTrashold)
                    .collect(Collectors.toList());

            /*long count = opCam.filter(Camera::isUseZone)
                    .map(cam ->
                            cam.getZones().stream().filter(zone ->
                                    toSourcesPredictionZones(predictionZones, structure).stream()
                                            .anyMatch(pzone -> imageUtil.getIoU(zone, pzone) > iouTrashold)).count())
                    .orElseGet(() -> (long) predictionZones.size());*/
            Map<Long, DetectedObjects> detectedObjects;
            if (camera.isUseZone()) {
                detectedObjects = camera.getZones().stream().map(zone ->
                        new AbstractMap.SimpleEntry<>(zone.getId(),
                                new DetectedObjects().withDetectedList(predictionZones.stream()
                                .filter(pZone -> imageUtil.getIoU(zone, pZone) > iouTrashold)
                                .map(pZone -> DetectedObject.tagToDetectedObject(pZone.getTag(),tagPattern))
                                        .collect(Collectors.toList()))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            } else {
                detectedObjects = Collections.singletonMap(0L,
                        new DetectedObjects().withDetectedList(predictionZones.stream()
                        .map(pZone -> DetectedObject.tagToDetectedObject(pZone.getTag(),tagPattern))
                                .collect(Collectors.toList())));
            }

            if (withCamImage) {
                try {
                    CamImage camImage = new CamImage();
                    camImage.setCameraId(camera.getId());
                    camImage.setPredictions(predictionZones);
                    camImage.setUseZone(camera.isUseZone());
                    if (camera.isUseZone()) {
                        camImage.setZones(camera.getZones().stream()
                                .map(StatisticalWorkspaceZone::new).collect(Collectors.toList()));
                    }
                    camImage.setContentImage(imageUtil.bImageToJpegBase64(bi));
                    /*camImage.setContentImage(imageUtil.bImageToJpegBase64(structure.getSource()));
                    if(camera.isUseZone()){
                        camImage.setUnionContentImage(imageUtil.bImageToJpegBase64(structure.getDest()));
                    }*/
                    camImageService.addCamImage(camImage);
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }

            result = statisticalUnitRepository.save(new StatisticalUnit()
                    .withCamera(camera)
                    .withDetectedObjects(detectedObjects).withDate(new Date()));
        }
        return result;
    }

    /**
     * вспомогательный метод для определения пересечение (методом IoU) между объектами найденными на изображении
     * и Зонами омеченными для этой камеры
     *
     * @param predictionZones -
     *                        TODO: дописать javadoc
     */
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
