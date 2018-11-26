package ru.blaj.workspacetraffic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DatasetUnitDto {
    private String id;
    private String created;
    private int width;
    private int height;
    @JsonProperty("originalImageUri")
    private String imageUri;
    private List<Zone> regions;
}
