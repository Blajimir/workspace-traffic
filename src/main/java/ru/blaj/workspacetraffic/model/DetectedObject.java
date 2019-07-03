package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Arrays;
import java.util.Optional;

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

    /**
     * вспомогательный метод для серрилизации тэга в объект {@link DetectedObject}
     *
     * @param tag - тэг обнаруженного объекта полученного из конетивного сервиса
     * @return возвращает объект {@link DetectedObject} или null, если тэг не содержит паттерна фильрруютщего тэги
     */
    public static DetectedObject tagToDetectedObject(String tag, String tagPattern) {
        return Optional.ofNullable(tag).map(String::toLowerCase).filter(t -> t.contains(tagPattern))
                .map(t -> t.split("_")).map(t -> {
                    DetectedObject result = new DetectedObject()
                            .withGender(DetectedGenderEnum.UNKNOWN).withAge(DetectedAgeEnum.UNKNOWN);
                    if (t.length > 1) {
                        result.withGender(Arrays.stream(DetectedGenderEnum.values())
                                .filter(g -> g.getGender().equals(t[1]))
                                .findFirst().orElse(DetectedGenderEnum.UNKNOWN));
                    }
                    if (t.length > 2) {
                        result.withAge(Arrays.stream(DetectedAgeEnum.values())
                                .filter(g -> g.getAge().equals(t[2]))
                                .findFirst().orElse(DetectedAgeEnum.UNKNOWN));
                    }
                    return result;
                }).orElse(null);
    }
}
