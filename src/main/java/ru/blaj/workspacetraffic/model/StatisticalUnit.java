package ru.blaj.workspacetraffic.model;

import com.fasterxml.jackson.annotation.*;
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
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Camera camera;
    /*@ManyToOne
    @JoinColumn(name = "zone_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkspaceZone zone;
    private boolean busy;*/
    private Long count;
    @Column(name = "use_zone")
    private boolean useZone;
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

    public StatisticalUnit withCount(Long count){
        this.count = count;
        return this;
    }

    public StatisticalUnit withDate(Date date){
        this.date = date;
        return this;
    }
}
