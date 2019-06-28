package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Класс - модель предсказанных зон, эти объекты хранят данные полученные от когнитивного сервиса, хранится в БД для
 * анализа качества работы сервиса
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Embeddable
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PredictionZone extends Zone {
    private String tag;
    @Column(precision = 10, scale = 2)
    private double probability;

    public PredictionZone withTag(String tag){
        this.tag = tag;
        return this;
    }

    public PredictionZone withProbability(double probability){
        this.probability = probability;
        return this;
    }

    @Override
    public PredictionZone withLeft(double left) {
        this.left = left;
        return this;
    }

    @Override
    public PredictionZone withTop(double top) {
        this.top = top;
        return this;
    }

    @Override
    public PredictionZone withWidth(double width) {
        this.width = width;
        return this;
    }

    @Override
    public PredictionZone withHeight(double height) {
        this.height = height;
        return this;
    }
}
