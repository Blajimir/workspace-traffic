package ru.blaj.workspacetraffic.model;

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
    private boolean active;
    @Column(name = "use_zone")
    private boolean useZone;
    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkspaceZone> zones;
}
