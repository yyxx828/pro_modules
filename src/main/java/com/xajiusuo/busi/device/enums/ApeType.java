package com.xajiusuo.busi.device.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 设备类型
 *
 * @author lizhidong
 * @date 2019-6-6 15:51
 */
public enum ApeType {

    ENTRANCE(1, "出入口设备"),
    PARK(2, "停车场"),
    VIDEO(3, "视频监控"),
    VERIFACE(4, "人脸识别"),
    GEOFENCE(5, "电子栅栏"),
    ELEC_PATROL(6, "电子巡更"),
    DOOR_CONTROL(7, "楼宇门禁"),
    WIFI_PROBE(8, "WIFI探针"),
    OTHER(9, "其他"),
    FIRE(10, "消防设施");

    private Integer value;
    private String desc;

    ApeType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static ApeType valueBy(Integer value) {
        return Optional.ofNullable(value).map(n -> Optional.ofNullable(Arrays.asList(ApeType.values()).stream().filter(m -> m.getValue().equals(n)).collect(Collectors.toList())).map(v -> v.get(0)).orElse(null)).orElse(null);
    }

    public static String getDesc(Integer value) {
        return Optional.ofNullable(valueBy(value)).map(ApeType::getDesc).orElse(null);
    }
}
