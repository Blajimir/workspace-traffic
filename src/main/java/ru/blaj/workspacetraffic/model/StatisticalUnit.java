package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
public class StatisticalUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statistics_seq")
    @SequenceGenerator(name = "statistics_seq", sequenceName = "statistics_id_seq", allocationSize = 1)
    private Long id;
    @OneToMany
    @JoinColumn(name = "camera_id")
    private Camera camera;
    @OneToOne
    @JoinColumn(name = "zone_id")
    private WorkspaceZone zone;
    private boolean busy;
}
