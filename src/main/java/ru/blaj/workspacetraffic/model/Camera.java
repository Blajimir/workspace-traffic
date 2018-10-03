package ru.blaj.workspacetraffic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Класс - модель отражает архитектуру хранимых данных Камер содержит поля необходимые для связи с видеопотоком камер
 *
 * @author Alesandr Kovalev aka blajimir
 * */

@Entity
@Table(name = "cameras")
@Data
@NoArgsConstructor
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "camera_id_seq")
    @SequenceGenerator(name="camera_id_seq", sequenceName = "camera_id_seq", allocationSize = 1)
    private Long id;
    private String url;
    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceZone> zones;
}
