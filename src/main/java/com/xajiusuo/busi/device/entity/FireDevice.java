package com.xajiusuo.busi.device.entity;

import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lizhidong
 * @date 2019-6-24 11:03
 */
@ApiModel(value = "fire_device", description = "消防设施信息")
@Setter
@Getter
@Entity
@Table(name = "T_FIREDEVICE_20", catalog = "village")
public class FireDevice extends BaseEntity {

    private String deviceId;//关联设备编码
    private Integer type;//消防设施类型

    public FireDevice(String deviceId, Integer type) {
        this.deviceId = deviceId;
        this.type = type;
    }

    public FireDevice() {
    }

    /**
     * 消防设备类型
     */
    public enum FireDeviceType {
        ZD(1, "自动设施"),
        JZ(2, "建筑设施"),
        PS(3, "喷洒系统"),
        OTHER(4, "其它");

        private Integer value;
        private String desc;

        FireDeviceType(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public Integer getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

        public static FireDeviceType valueBy(Integer value) {
            return Optional.ofNullable(value).map(n -> Optional.ofNullable(Arrays.asList(FireDeviceType.values()).stream().filter(m -> m.getValue().equals(n)).collect(Collectors.toList())).map(v -> v.get(0)).orElse(null)).orElse(null);
        }
    }
}
