package ru.blaj.workspacetraffic.service;

import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.PredictionEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.PredictionZone;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Класс - сервис для взаимодействие с когнетивным сервисом CustomVision, который будет анализировать кадры с камер
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Service
public class AzureVisionService {
    @Value("${app.azure.custom-vision.prediction-key}")
    private String predictionKey;
    //TODO: дописать функцию!!!
    public List<PredictionZone> getPrediction(BufferedImage bi){
        PredictionEndpoint predictClient = CustomVisionPredictionManager.authenticate(this.predictionKey);
        //TODO: убрать заглушку
        return null;
    }
}
