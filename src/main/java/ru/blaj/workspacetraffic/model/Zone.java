package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.blaj.workspacetraffic.util.ImageUtil;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@NoArgsConstructor
public class Zone {
    @Column(precision = 10, scale = 2)
    protected Double left;
    @Column(precision = 10, scale = 2)
    protected Double top;
    @Column(precision = 10, scale = 2)
    protected Double width;
    @Column(precision = 10, scale = 2)
    protected Double height;

    public Zone withLeft(double left){
        this.left = left;
        return this;
    }

    public Zone withTop(double top){
        this.top = top;
        return this;
    }

    public Zone withWidth(double width){
        this.width = width;
        return this;
    }

    public Zone withHeight(double height){
        this.height = height;
        return this;
    }

}
