package com.xajiusuo.busi.echartsUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 统计公共时间段枚举
 *
 * @author lizhidong
 * @date 2019-6-17 10:08
 */
public enum TimeFrame {

    D("d", "日"),
    W("w", "周"),
    M("m", "月"),
    Y("y", "年");

    private String value;
    private String desc;

    TimeFrame(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static TimeFrame valueBy(String value) {
        return Optional.ofNullable(value).map(n -> Optional.ofNullable(Arrays.asList(TimeFrame.values()).stream().filter(m -> value.equals(m.getValue())).collect(Collectors.toList())).map(v -> v.get(0)).orElse(null)).orElse(null);
    }
}
