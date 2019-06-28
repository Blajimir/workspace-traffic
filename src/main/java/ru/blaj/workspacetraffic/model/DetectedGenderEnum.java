package ru.blaj.workspacetraffic.model;

/**
 * Энумератор - вспомогательный элемент для класса-модели {@link DetectedObject}
 * необходим для детализации пола объекта
 * @author Alesandr Kovalev aka blajimir
 * */
public enum DetectedGenderEnum {
    MALE("male"),
    FEMALE("female"),
    UNKNOWN("unknown");

    private String gender;

    DetectedGenderEnum(String gender){
        this.gender = gender;
    }

    public String getGender(){
        return this.gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

}
