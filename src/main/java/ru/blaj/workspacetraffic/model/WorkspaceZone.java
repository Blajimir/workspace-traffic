package ru.blaj.workspacetraffic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс - модель Зон, зоны нужны для того чтобы отметить активные зоны на изображениях получаемых с камер
 * @author Alesandr Kovalev aka blajimir
 *
 * */

@Entity
@Table(name = "workspace_zones")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WorkspaceZone extends Zone{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workspace_zones_id_seq")
    @SequenceGenerator(name = "workspace_zones_id_seq", sequenceName = "workspace_zones_id_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String name;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "camera_id", foreignKey = @ForeignKey(name = "none"))
    private Camera camera;

    public WorkspaceZone withName(String name) {
        this.name = name;
        return this;
    }

    public WorkspaceZone withCamera(Camera camera) {
        this.camera = camera;
        return this;
    }

    @Override
    public WorkspaceZone withLeft(double left) {
        this.left = left;
        return this;
    }

    @Override
    public WorkspaceZone withTop(double top) {
        this.top = top;
        return this;
    }

    @Override
    public WorkspaceZone withWidth(double width) {
        this.width = width;
        return this;
    }

    @Override
    public WorkspaceZone withHeight(double height) {
        this.height = height;
        return this;
    }
}
