package com.xajiusuo.busi.query.entity;


/**
 * Created by Administrator on 2019/6/28.
 */

public enum InfoEnum {
    BIRTHDAY("生日"), NAME("姓名"), SEX("性别"),
    ID_TYPE("证件类型"), ID_NUMBER("证件号码"), NATION("民族"), BUILDING_NAME("楼栋名称"),
    UNIT_NAME("单元名称"), HOUSE_NO("房屋编码"),
    ROOM_NAME("房屋名称"), FLOOR("层数"), VEHICLE_BRAND("车辆品牌"), VEHICLE_CLASS("车辆类型"),
    VEHICLE_COLOR("车身颜色"), VEHICLE_MODEL("车辆型号"), PLATE_NO("车牌号码"),
    NO_DATA("暂无数据");

    private String value;

    InfoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
