package ru.blaj.workspacetraffic.model;

/**
 * Энумератор - вспомогательный элемент для класса-модели {@link DetectedObject}
 * необходим для детализации возраста объекта
 * @author Alesandr Kovalev aka blajimir
 * */
public enum DetectedAgeEnum {
    CHILD("child"),
    YOUNG("young"),
    ADULT("adult"),
    ELDERLY("elderly"),
    UNKNOWN("unknown");

    private String age;
    DetectedAgeEnum(String age){
        this.age = age;
    }

    public String getAge(){
        return this.age;
    }
    public void setAge(String age){
        this.age = age;
    }

}
