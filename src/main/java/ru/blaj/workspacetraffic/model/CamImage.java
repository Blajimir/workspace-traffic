package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cam_images")
@Data
@NoArgsConstructor
public class CamImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cam_images_id_seq")
    @SequenceGenerator(name = "cam_images_id_seq", sequenceName = "cam_images_id_seq", allocationSize = 1)
    private Long id;
    @CreationTimestamp
    private Date timestamp;
    @Column(name = "camera_id")
    private Long cameraId;
    @Column(columnDefinition = "text")
    private String contentImage;
    @Column(columnDefinition = "text")
    private String unionContentImage;
    @Column(name="use_zone")
    private boolean useZone;
    @ElementCollection
    private List<PredictionZone> predictions;
}
