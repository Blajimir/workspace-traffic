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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cameras_seq")
    @SequenceGenerator(name="camera_seq", sequenceName = "camera_id_seq", allocationSize = 1)
    private Long id;
    private String url;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany
    @JoinColumn(name = "camera_id")
    private List<WorkspaceZone> zones;
}
