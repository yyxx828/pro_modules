package com.xajiusuo.busi.device.entity;

import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 技防设备关注日志
 *
 * @author lizhidong
 * @date 2019-6-26 14:43
 */
@ApiModel(value = "device_focus", description = "消防设施信息")
@Setter
@Getter
@Entity
@Table(name = "T_DEVICEFOCUS_20", catalog = "village")
public class DeviceFocus extends BaseEntity {

    private String villageId;//小区编号
    private String deviceId;//设备编码
    private Integer apeType;//设备类型

    public DeviceFocus() {
    }

    public DeviceFocus(String deviceId, Integer apeType) {
        this.deviceId = deviceId;
        this.apeType = apeType;
    }

    public DeviceFocus(String villageId, String deviceId, Integer apeType) {
        this.villageId = villageId;
        this.deviceId = deviceId;
        this.apeType = apeType;
    }
}
