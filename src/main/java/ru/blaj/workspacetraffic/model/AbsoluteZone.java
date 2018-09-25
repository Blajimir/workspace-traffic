package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AbsoluteZone {
    private int left;
    private int top;
    private int width;
    private int height;

    public AbsoluteZone withLeft(int left){
        this.left = left;
        return this;
    }

    public AbsoluteZone withTop(int top){
        this.top = top;
        return this;
    }

    public AbsoluteZone withWidth(int width){
        this.width = width;
        return this;
    }

    public AbsoluteZone withHeight(int height){
        this.height = height;
        return this;
    }

}
