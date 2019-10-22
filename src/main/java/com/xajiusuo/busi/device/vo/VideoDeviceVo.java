package com.xajiusuo.busi.device.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.VideoDevice;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频监控信息
 *
 * @author lizhidong
 * @date 2019-6-24 15:48
 */
@Data
public class VideoDeviceVo {

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

    private Integer lineState = 1;//在线状态,默认在线
    private Integer isFocus = 0;//是否重点关注，默认未关注

    public VideoDeviceVo(String villageId, String apeId, String name, String model, String factory, String place, Date installTime, Integer isOnline, String longitude, String latitude, Integer apeType, String memo, String ipAddr, String ipv6Addr, String region, Integer lineState) {
        this.villageId = villageId;
        this.apeId = apeId;
        this.name = name;
        this.model = model;
        this.factory = factory;
        this.place = place;
        this.installTime = installTime;
        this.isOnline = isOnline;
        this.longitude = longitude;
        this.latitude = latitude;
        this.apeType = apeType;
        this.memo = memo;
        this.ipAddr = ipAddr;
        this.ipv6Addr = ipv6Addr;
        this.region = region;
        this.lineState = lineState;
    }

    public VideoDeviceVo(String villageId, String apeId, String name, String model, String factory, String place, Date installTime, Integer isOnline, String longitude, String latitude, Integer apeType, String memo, String ipAddr, String ipv6Addr, String region, String capDirection, String monitorDirection, String monitorDes, String userId, String password, String rtspRealAddr, String rtspBackAddr, String rtspRealToken, String rtspBackToken, Integer type, Integer lineState) {
        this.villageId = villageId;
        this.apeId = apeId;
        this.name = name;
        this.model = model;
        this.factory = factory;
        this.place = place;
        this.installTime = installTime;
        this.isOnline = isOnline;
        this.longitude = longitude;
        this.latitude = latitude;
        this.apeType = apeType;
        this.memo = memo;
        this.ipAddr = ipAddr;
        this.ipv6Addr = ipv6Addr;
        this.region = region;
        this.capDirection = capDirection;
        this.monitorDirection = monitorDirection;
        this.monitorDes = monitorDes;
        this.userId = userId;
        this.password = password;
        this.rtspRealAddr = rtspRealAddr;
        this.rtspBackAddr = rtspBackAddr;
        this.rtspRealToken = rtspRealToken;
        this.rtspBackToken = rtspBackToken;
        this.type = type;
        this.lineState = lineState;
    }

    public VideoDeviceVo(String villageId, String apeId, String name, String model, String factory, String place, Date installTime, Integer isOnline, String longitude, String latitude, Integer apeType, String memo, String ipAddr, String ipv6Addr, String region, String capDirection, String monitorDirection, String monitorDes, String userId, String password, String rtspRealAddr, String rtspBackAddr, String rtspRealToken, String rtspBackToken, Integer type, Integer lineState, Integer isFocus) {
        this.villageId = villageId;
        this.apeId = apeId;
        this.name = name;
        this.model = model;
        this.factory = factory;
        this.place = place;
        this.installTime = installTime;
        this.isOnline = isOnline;
        this.longitude = longitude;
        this.latitude = latitude;
        this.apeType = apeType;
        this.memo = memo;
        this.ipAddr = ipAddr;
        this.ipv6Addr = ipv6Addr;
        this.region = region;
        this.capDirection = capDirection;
        this.monitorDirection = monitorDirection;
        this.monitorDes = monitorDes;
        this.userId = userId;
        this.password = password;
        this.rtspRealAddr = rtspRealAddr;
        this.rtspBackAddr = rtspBackAddr;
        this.rtspRealToken = rtspRealToken;
        this.rtspBackToken = rtspBackToken;
        this.type = type;
        this.lineState = lineState;
        this.isFocus = isFocus;
    }

    public VideoDeviceVo(Device device, VideoDevice videoDevice, Integer lineState, Integer isFocus) {
        this.villageId = device.getVillageId();
        this.apeId = device.getApeId();
        this.name = device.getName();
        this.model = device.getModel();
        this.factory = device.getFactory();
        this.place = device.getPlace();
        this.installTime = device.getInstallTime();
        this.isOnline = device.getIsOnline();
        this.longitude = device.getLongitude();
        this.latitude = device.getLatitude();
        this.apeType = device.getApeType();
        this.memo = device.getMemo();
        this.ipAddr = device.getIpAddr();
        this.ipv6Addr = device.getIpv6Addr();
        this.region = device.getRegion();
        this.capDirection = videoDevice.getCapDirection();
        this.monitorDirection = videoDevice.getMonitorDirection();
        this.monitorDes = videoDevice.getMonitorDes();
        this.userId = videoDevice.getUserId();
        this.password = videoDevice.getPassword();
        this.rtspRealAddr = videoDevice.getRtspRealAddr();
        this.rtspBackAddr = videoDevice.getRtspBackAddr();
        this.rtspRealToken = videoDevice.getRtspRealToken();
        this.rtspBackToken = videoDevice.getRtspBackToken();
        this.type = videoDevice.getType();
        if (null != lineState) this.lineState = lineState;
        if (null != isFocus) this.isFocus = isFocus;
    }

    public Device convertDevice() {
        return new Device(villageId, apeId, name, model, factory, place, installTime, longitude, latitude, apeType, memo, ipAddr, ipv6Addr, region);
    }

    public VideoDevice convertVideoDevice() {
        return new VideoDevice(apeId, capDirection, monitorDirection, monitorDes, userId, password, rtspRealAddr, rtspBackAddr, type);
    }

    /**
     * 整合信息
     *
     * @param deviceList   视频监控设备信息
     * @param videoDevices 视频监控详情信息
     * @param lineStatus   视频监控状态
     * @param focusApeIds  重点关注设备编码
     * @return
     */
    public static List<VideoDeviceVo> convert(List<Device> deviceList, List<VideoDevice> videoDevices, Map<String, Object> lineStatus, Set<String> focusApeIds) {
        Map<String, List<VideoDevice>> videoMap = Optional.ofNullable(videoDevices).map(n -> n.stream().collect(Collectors.groupingBy(VideoDevice::getDeviceId))).orElse(null);
        return Optional.ofNullable(deviceList)
                .map(n -> n.stream().map(m -> {
                    VideoDevice videoDevice = Optional.ofNullable(videoMap.get(m.getApeId())).map(v -> v.get(0)).orElse(null);
                    Integer lineState = (null == lineStatus ? 1 : null == lineStatus.get(m.getIpAddr()) ? 1 : Integer.parseInt(lineStatus.get(m.getIpAddr()) + ""));
                    Integer isFoucus = Optional.ofNullable(focusApeIds).map(v -> v.contains(m.getApeId()) ? 1 : 0).orElse(0);
                    VideoDeviceVo videoDeviceVo;
                    if (null == videoDevice) {
                        videoDeviceVo = new VideoDeviceVo(m.getVillageId(), m.getApeId(), m.getName(), m.getModel(), m.getFactory(), m.getPlace(), m.getInstallTime(), m.getIsOnline(),
                                m.getLongitude(), m.getLatitude(), m.getApeType(), m.getMemo(), m.getIpAddr(), m.getIpv6Addr(), m.getRegion(), lineState);
                    } else {
                        videoDeviceVo = new VideoDeviceVo(m.getVillageId(), m.getApeId(), m.getName(), m.getModel(), m.getFactory(), m.getPlace(), m.getInstallTime(), m.getIsOnline(),
                                m.getLongitude(), m.getLatitude(), m.getApeType(), m.getMemo(), m.getIpAddr(), m.getIpv6Addr(), m.getRegion(), videoDevice.getCapDirection(), videoDevice.getMonitorDirection(),
                                videoDevice.getMonitorDes(), videoDevice.getUserId(), videoDevice.getPassword(), videoDevice.getRtspRealAddr(), videoDevice.getRtspBackAddr(), videoDevice.getRtspRealToken(),
                                videoDevice.getRtspBackToken(), videoDevice.getType(), lineState, isFoucus);
                    }
                    return videoDeviceVo;
                }).collect(Collectors.toList()))
                .orElse(null);
    }
}
