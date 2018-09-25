package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@NoArgsConstructor
public class Zone {
    @Column(precision = 10, scale = 2)
    protected Float left;
    @Column(precision = 10, scale = 2)
    protected Float top;
    @Column(precision = 10, scale = 2)
    protected Float width;
    @Column(precision = 10, scale = 2)
    protected Float height;

    public Zone withLeft(float left){
        this.left = left;
        return this;
    }

    public Zone withTop(float top){
        this.top = top;
        return this;
    }

    public Zone withWidth(float width){
        this.width = width;
        return this;
    }

    public Zone withHeight(float height){
        this.height = height;
        return this;
    }
}
