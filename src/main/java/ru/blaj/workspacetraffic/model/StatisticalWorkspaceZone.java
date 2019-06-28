package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Встраеваемый класс - модель для хранения информации о зонах которые были выставленны для камеры на момент сохранения лога,
 * хранится в БД для анализа качества работы сервиса.
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Embeddable
@EqualsAndHashCode(callSuper = true)
@Data
public class StatisticalWorkspaceZone extends Zone {
    @Column(name = "zone_name")
    private String zoneName;

    public StatisticalWorkspaceZone(){}

    public StatisticalWorkspaceZone(WorkspaceZone zone){
        this.zoneName = zone.getName();
        this.setLeft(zone.getLeft());
        this.setTop(zone.getTop());
        this.setWidth(zone.getWidth());
        this.setHeight(zone.getHeight());
    }

    public StatisticalWorkspaceZone withZoneName(String zoneName){
        this.zoneName = zoneName;
        return this;
    }

    @Override
    public StatisticalWorkspaceZone withLeft(double left) {
        this.left = left;
        return this;
    }

    @Override
    public StatisticalWorkspaceZone withTop(double top) {
        this.top = top;
        return this;
    }

    @Override
    public StatisticalWorkspaceZone withWidth(double width) {
        this.width = width;
        return this;
    }

    @Override
    public StatisticalWorkspaceZone withHeight(double height) {
        this.height = height;
        return this;
    }
}
