package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.blaj.workspacetraffic.model.PredictionZone;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация класса-сервиса для собственного REST-сервиса, который реализует TensorFlow Object Detection API
 */
@Service
@Profile("own-vision")
@Log
public class OwnVisionService implements VisionService {
    @Value("${app.own-tf-od-service.url}")
    private String address;
    @Value("${app.filter-tag}")
    private String tagFilter;

    @Override
    public List<PredictionZone> getPrediction(BufferedImage bi) {
        List<PredictionZone> result = null;
        RestTemplate restTemplate = new RestTemplate();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", baos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> multipart = new LinkedMultiValueMap<>();
            HttpHeaders imgHeader = new HttpHeaders();
            imgHeader.setContentType(MediaType.IMAGE_PNG);
            imgHeader.setContentDispositionFormData("file", "1.png");
            HttpEntity<ByteArrayResource> imgPart = new HttpEntity<>( new ByteArrayResource(baos.toByteArray()), imgHeader);
            multipart.add("file", imgPart);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(multipart, headers);
            ResponseEntity<Map<String,Object>> response = restTemplate.exchange(
                    address.concat("/api/predict"),
                    HttpMethod.POST,entity, new ParameterizedTypeReference<Map<String,Object>>(){});
            result = this.getPredictionZoneFromMap(response.getBody()).stream()
                    .filter(p -> tagFilter.contains(p.getTag().toLowerCase())).collect(Collectors.toList());
        } catch (IOException e) {
            log.warning(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public List<PredictionZone> getPredictionZoneFromMap(Map<String, Object> map){
        ArrayList<PredictionZone> result = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) map.get("Predictions");
        predictions.forEach(oMap -> {
            @SuppressWarnings("unchecked")
            Map<String, Double> region = (Map<String, Double>) oMap.get("Region");
            PredictionZone item = new PredictionZone().withProbability((double) oMap.get("Probability")*100)
                    .withLeft(region.get("Left")).withTop(region.get("Top")).withWidth(region.get("Width"))
                    .withHeight(region.get("Height")).withTag((String)oMap.get("TagName"));
            result.add(item);
        });
        return result;
    }
}
