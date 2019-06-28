package ru.blaj.workspacetraffic.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

/**
 * Класс - модель со списком {@link List<DetectedObject>},
 * необходим для хранения коллекции объектов {@link DetectedObject} в качестве HashMap коллекции, где ключем является
 * идентификатор зоны камеры. В виде Map<Long, List<DetectedObject>> описать объект в рамках jpa нотаций не удалось
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Entity
@Table(name = "detected_objects")
@Data
@NoArgsConstructor
public class DetectedObjects {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detected_objects_id_seq")
    @SequenceGenerator(name = "detected_objects_id_seq", sequenceName = "detected_objects_id_seq", allocationSize = 1)
    private Long id;
    @JsonValue
    @ElementCollection(fetch = FetchType.EAGER)
    private List<DetectedObject> detectedList;

    public DetectedObjects withDetectedList(List<DetectedObject> detectedList){
        this.detectedList = detectedList;
        return this;
    }

    public DetectedObjects withDetectedList(DetectedObject[] detectedObject) {
        return this.withDetectedList(Arrays.asList(detectedObject));
    }
}
