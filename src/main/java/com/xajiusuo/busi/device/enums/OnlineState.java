package com.xajiusuo.busi.device.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 设备在线状态
 *
 * @author lizhidong
 * @date 2019-6-6 15:39
 */
public enum OnlineState {

    ON(0, "在线"),
    OFF(1, "离线");

    private Integer value;
    private String desc;

    OnlineState(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static OnlineState valueBy(Integer value) {
        return Optional.ofNullable(value).map(n -> Optional.ofNullable(Arrays.asList(OnlineState.values()).stream().filter(m -> m.getValue().equals(n)).collect(Collectors.toList())).map(v -> v.get(0)).orElse(null)).orElse(null);
    }


}
