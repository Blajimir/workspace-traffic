package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
public class StatisticalUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statistics_id_seq")
    @SequenceGenerator(name = "statistics_id_seq", sequenceName = "statistics_id_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;
    @ManyToOne
    @JoinColumn(name = "zone_id")
    private WorkspaceZone zone;
    private boolean busy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public StatisticalUnit withId(Long id){
        this.id = id;
        return this;
    }

    public StatisticalUnit withCamera(Camera camera){
        this.camera = camera;
        return this;
    }

    public StatisticalUnit withZone(WorkspaceZone zone){
        this.zone = zone;
        return this;
    }

    public StatisticalUnit withBusy(boolean busy){
        this.busy = busy;
        return this;
    }
}
