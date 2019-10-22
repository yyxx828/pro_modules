package com.xajiusuo.busi.device.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 8类技防设备基本信息
 *
 * @author lizhidong
 * @date 2019-6-6 13:44
 */
@ApiModel(value = "device", description = "8类技防设备基本信息")
@Setter
@Getter
@Entity
@Table(name = "T_DEVICE_20", catalog = "village")
public class Device extends BaseEntity {

    private String villageId;//小区编号
    private String apeId;//设备编码
    private String name;//设备名称
    private String model;//设备型号
    private String factory;//生产厂家
    private String place;//安装位置
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date installTime;//安装时间
    private Integer isOnline;//是否在线
    private String longitude;//安装位置-经度
    private String latitude;//安装位置-纬度
    private Integer apeType;//8大设备类型
    private String memo;//备注
    private String ipAddr;//ip地址
    private String ipv6Addr;//ip6地址
    private String region;//所属区域

    @ApiModelProperty(value = "用于安装时间区间查询", required = false)
    @Transient
    private String installTimeRange;
    @ApiModelProperty(value = "用于创建时间区间查询", required = false)
    @Transient
    private String createTimeRange;

    public Device() {
    }

    public Device(String villageId, String apeId, String name, String model, String factory, String place, Date installTime, String longitude, String latitude, Integer apeType, String memo, String ipAddr, String ipv6Addr, String region) {
        this.villageId = villageId;
        this.apeId = apeId;
        this.name = name;
        this.model = model;
        this.factory = factory;
        this.place = place;
        this.installTime = installTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.apeType = apeType;
        this.memo = memo;
        this.ipAddr = ipAddr;
        this.ipv6Addr = ipv6Addr;
        this.region = region;
    }
}
