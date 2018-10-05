package ru.blaj.workspacetraffic.service;

import ru.blaj.workspacetraffic.model.PredictionZone;

import java.awt.image.BufferedImage;
import java.util.List;

public interface VisionService {
    List<PredictionZone> getPrediction(BufferedImage bi);
}
