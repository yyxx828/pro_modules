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
 * 监控视频设备信息表
 *
 * @author lizhidong
 * @date 2019-6-10 10:56
 */
@ApiModel(value = "video_device", description = "监控视频设备信息表")
@Setter
@Getter
@Entity
@Table(name = "T_VIDEO_DEVICE_20", catalog = "village")
public class VideoDevice extends BaseEntity {

    private String deviceId;//关联设备编码
    private String capDirection;//车辆抓拍方向
    private String monitorDirection;//监视方向
    private String monitorDes;//监视区域说明
    private String userId;//用户账号
    private String password;//口令
    private String rtspRealAddr;//视频实时通道地址
    private String rtspBackAddr;//视频回放通道地址
    private String rtspRealToken;//视频实时通道token
    private String rtspBackToken;//视频回放通道token
    private Integer type;//视频设备类型

    public VideoDevice() {
    }

    public VideoDevice(String deviceId, String capDirection, String monitorDirection, String monitorDes, String userId, String password, String rtspRealAddr, String rtspBackAddr, Integer type) {
        this.deviceId = deviceId;
        this.capDirection = capDirection;
        this.monitorDirection = monitorDirection;
        this.monitorDes = monitorDes;
        this.userId = userId;
        this.password = password;
        this.rtspRealAddr = rtspRealAddr;
        this.rtspBackAddr = rtspBackAddr;
        this.type = type;
    }

    /**
     * 视频监控设备类型
     */
    public enum VideoDeviceType {
        SD(1, "普通"),
        HD(2, "高清");

        private Integer value;
        private String desc;

        VideoDeviceType(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public Integer getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

        public static VideoDeviceType valueBy(Integer value) {
            return Optional.ofNullable(value).map(n -> Optional.ofNullable(Arrays.asList(VideoDeviceType.values()).stream().filter(m -> m.getValue().equals(n)).collect(Collectors.toList())).map(v -> v.get(0)).orElse(null)).orElse(null);
        }
    }
}
