package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Класс - модель предсказанных зон, эти объекты хранят данные полученные от когнитивного сервиса, хранится в БД для
 * анализа качества работы сервиса
 * @author Alesandr Kovalev aka blajimir
 * */
@Embeddable
@Data
@NoArgsConstructor
public class PredictionZone extends Zone {
    private String tag;
    @Column(precision = 10, scale = 2)
    private float probability;
}
