package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cam_images")
@Data
@NoArgsConstructor
public class CamImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cam_images_seq")
    @SequenceGenerator(name = "cam_images_seq", sequenceName = "cam_images_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "camera_id")
    private Long cameraId;
    private String content;
    @ElementCollection
    private List<PredictionZone> predictions;
}
