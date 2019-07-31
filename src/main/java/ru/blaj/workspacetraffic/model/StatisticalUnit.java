package ru.blaj.workspacetraffic.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
public class StatisticalUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statistics_id_seq")
    @SequenceGenerator(name = "statistics_id_seq", sequenceName = "statistics_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "camera_id", nullable = false)
    @NotNull
    private Long cameraId;
    /*@ManyToOne
    @JoinColumn(name = "zone_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkspaceZone zone;
    private boolean busy;
    private Long count;*/
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="statistics_detected_objects",
            joinColumns = @JoinColumn(name="unit_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="detected_obj_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "zone_id")
    @JoinColumn(name = "detected_obj_id")
    private Map<Long, DetectedObjects> detectedObjects;
    @Column(name = "use_zone")
    private boolean useZone;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @JsonGetter("count")
    public long getCount(){
        return this.detectedObjects.values().stream().mapToLong(dObj -> dObj.getDetectedList().size()).sum();
    }

    public StatisticalUnit withId(Long id){
        this.id = id;
        return this;
    }

    public StatisticalUnit withCamera(Camera camera){
        this.cameraId = camera.getId();
        this.useZone = camera.isUseZone();
        return this;
    }

    public StatisticalUnit withDetectedObjects(Map<Long, DetectedObjects> detectedObjects){
        this.detectedObjects = detectedObjects;
        return this;
    }

    /*public StatisticalUnit withCount(Long count){
        this.count = count;
        return this;
    }*/

    public StatisticalUnit withDate(Date date){
        this.date = date;
        return this;
    }

    public void addDetectedObject(Long zoneId, @NotNull DetectedObject ... detectedObject){
        Long zid = Optional.ofNullable(zoneId).orElse(0L);
        if(this.detectedObjects == null)
            this.detectedObjects = new HashMap<>();
        if(this.detectedObjects.get(zid)!=null){
            this.detectedObjects.get(zid).setDetectedList(Arrays.asList(detectedObject));
        }else{
            this.detectedObjects.put(zid, new DetectedObjects().withDetectedList(detectedObject));
        }
    }
}
