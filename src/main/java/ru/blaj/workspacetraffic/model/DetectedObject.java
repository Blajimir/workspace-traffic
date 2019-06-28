package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Класс - модель для хранения дополнительной информации (пол и возраст) о найденный объектах на
 * изображениях получаемых с камер.
 * @author Alesandr Kovalev aka blajimir
 * */
@Data
@Embeddable
@NoArgsConstructor
public class DetectedObject {
    @Enumerated(EnumType.STRING)
    private DetectedGenderEnum gender;
    @Enumerated(EnumType.STRING)
    private DetectedAgeEnum age;

    public DetectedObject withGender(DetectedGenderEnum gender){
        this.gender = gender;
        return this;
    }

    public DetectedObject withAge(DetectedAgeEnum age){
        this.age = age;
        return this;
    }
}
