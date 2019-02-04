package ru.blaj.workspacetraffic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.PredictionEndpoint;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.ImagePrediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.Prediction;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.blaj.workspacetraffic.model.PredictionZone;
import ru.blaj.workspacetraffic.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс - сервис для взаимодействие с когнетивным сервисом CustomVision, который будет анализировать кадры с камер
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Service
@Profile("custom-vision")
@Log
public class AzureVisionService implements VisionService{
    @Autowired
    private ImageUtil imageUtil;
    @Value("${app.azure.custom-vision.prediction-key}")
    private String predictionKey;
    @Value("${app.azure.custom-vision.project-id}")
    private UUID projectId;
    @Value("${app.azure.custom-vision.filter-tag}")
    private String tagFilter;
    //TODO: дописать функцию!!!
    @Override
    public List<PredictionZone> getPrediction(BufferedImage bi){
        List<PredictionZone> result = null;
        PredictionEndpoint predictClient = CustomVisionPredictionManager.authenticate(this.predictionKey);
        try {
            ImagePrediction predictionResult = predictClient.predictions().predictImage()
                    .withProjectId(this.projectId)
                    .withImageData(imageUtil.bImageToJpeg(bi))
                    .execute();
            result = toPredictionZone(predictionResult.predictions());
            log.info(new ObjectMapper().writer().withDefaultPrettyPrinter()
                    .writeValueAsString(result));
        } catch (IOException e) {
            log.warning(e.getMessage());
            e.printStackTrace();
        }
        RestTemplate template = new RestTemplate();
        return result;
    }

    private List<PredictionZone> toPredictionZone(List<Prediction> predictions){
        return predictions.stream().filter(p -> this.tagFilter.contains(p.tagName())).map(p -> new PredictionZone()
                .withTag(p.tagName())
                .withProbability(p.probability()*100.0)
                .withLeft(p.boundingBox().left())
                .withTop(p.boundingBox().top())
                .withWidth(p.boundingBox().width())
                .withHeight(p.boundingBox().height()))
                .collect(Collectors.toList());
    }
}
