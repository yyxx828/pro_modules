package com.xajiusuo.busi.device.entity;

import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author lizhidong
 * @date 2019-6-19 17:00
 */
@ApiModel(value = "building_entrance_device", description = "楼宇门禁设备信息表")
@Setter
@Getter
@Entity
@Table(name = "T_BUILDINGENTRANCEDEVICE_20", catalog = "village")
public class BuildingEntranceDevice extends BaseEntity {

    private String deviceId;//关联设备编码
    private String buildingNo;//关联楼栋编号
    private String unitNo;//关联楼栋单元编号
    private String unitFloor;//单元电梯楼层

    public BuildingEntranceDevice() {
    }

    public BuildingEntranceDevice(String deviceId, String buildingNo, String unitNo, String unitFloor) {
        this.deviceId = deviceId;
        this.buildingNo = buildingNo;
        this.unitNo = unitNo;
        this.unitFloor = unitFloor;
    }
}
