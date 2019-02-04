package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.PredictionZone;

import java.awt.image.BufferedImage;
import java.util.List;

@Service
@Profile("own-vision")
@Log
public class OwnVisionService implements VisionService {

    @Value("${app.own-tf-od-service.url}")
    private String address;

    @Override
    public List<PredictionZone> getPrediction(BufferedImage bi) {
        //HttpEntity
        return null;
    }
}
