package ru.blaj.workspacetraffic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.blaj.workspacetraffic.model.DatasetUnitDto;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Log
public class LoadDatasetService {
    @Value("${app.azure.custom-vision.training-key}")
    private String trainingKey;
    @Value("${app.azure.custom-vision.project-id}")
    private String projectId;
    @Value("${app.azure.custom-vision.filter-tag}")
    private String tagFilter;
    private String tagFilterId;
    private RestTemplate restTemplate;
    @Value("${app.azure.custom-vision.training-url}")
    private String url;
    private final static String ZIP_FOLDER = "imgs";
    @PostConstruct
    public void init(){
        this.restTemplate = new RestTemplate();
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Training-Key", trainingKey);
        headers.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String,Object>>> responseEntity =
                this.restTemplate.exchange(url.concat("/projects/{projectId}/tags"),
                        HttpMethod.GET,
                        entity, new ParameterizedTypeReference<List<Map<String, Object>>>() {},
                        projectId);
        if(responseEntity.getStatusCode() != HttpStatus.OK || !setTagId(responseEntity.getBody())){
            throw new RestClientException(
                    String.format("Status: %d   Request not consist tag id for tag name %s",
                            responseEntity.getStatusCodeValue(),this.tagFilter));
        }
    }

    public String getTagId(){
        return this.tagFilterId;
    }

    public List<DatasetUnitDto> getDataSet(){
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Training-key", trainingKey);
        HttpEntity entity = new HttpEntity<>(headers);
        HashMap<String, String> uriVars = new HashMap<>();
        uriVars.put("projectId", this.projectId);
        uriVars.put("tagId", this.tagFilterId);
        ResponseEntity<List<DatasetUnitDto>> responseEntity = this.restTemplate.exchange(
                url.concat("/projects/{projectId}/images/tagged?tagIds=[{tagId}]&take=256"),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<DatasetUnitDto>>() {},
                uriVars);
        if(responseEntity.getStatusCode() != HttpStatus.OK){
            throw new RestClientException(
                    String.format("Bad status from custom vision service: %d ",
                            responseEntity.getStatusCodeValue()));
        }
        return responseEntity.getBody();
    }

    public void saveDataSetLikeZip() throws IOException {
        FileOutputStream f = new FileOutputStream("cv-dataset.zip");
        ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(f));
        List<DatasetUnitDto> dataset = this.getDataSet();
        for (DatasetUnitDto unit: dataset) {
            URL url = new URL(unit.getImageUri());
            BufferedImage img = ImageIO.read(url);
            String path = ZIP_FOLDER.concat("/").concat(unit.getId()).concat(".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpeg", baos);
            byte[] barray = baos.toByteArray();
            zip.putNextEntry(new ZipEntry(path));
            zip.write(barray,0,barray.length);
            zip.closeEntry();
            unit.setImageUri(path);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        zip.putNextEntry(new ZipEntry("dataset.json"));
        byte[] barray = objectMapper.writer().writeValueAsBytes(dataset);
        zip.write(barray,0, barray.length);
        zip.closeEntry();
        zip.close();
        f.close();
    }

    private boolean setTagId(List<Map<String, Object>> tags){
        return Optional.ofNullable(tags).orElse(Collections.emptyList()).stream().filter(objMap -> objMap.get("name").equals(this.tagFilter))
                .findFirst().map(objMap -> {
                    tagFilterId = (String)objMap.get("id");
                    return true;
                }).orElse(false);
    }
}
