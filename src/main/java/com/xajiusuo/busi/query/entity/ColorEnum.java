package com.xajiusuo.busi.query.entity;

/**
 * Created by Administrator on 2019/6/28.
 */
public enum ColorEnum {


    MAN("rgb(233,87,128)", "男"),
    WOMAN("rgb(92,155,229)", "女"),
    HOUSE("rgb(107,178,25)", "房"),
    MOTOR_VEHICLE("rgb(213,137,70)", "机动车");

    ColorEnum(String color, String typeName) {
        Color = color;
        this.typeName = typeName;
    }

    private String Color;
    private String typeName;

    public String getColor() {
        return Color;
    }

    public String getTypeName() {
        return typeName;
    }
}
