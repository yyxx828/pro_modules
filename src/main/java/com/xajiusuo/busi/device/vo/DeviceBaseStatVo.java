package com.xajiusuo.busi.device.vo;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.enums.ApeType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 技防设备基本情况统计
 *
 * @author lizhidong
 * @date 2019-6-24 11:16
 */
@Data
public class DeviceBaseStatVo {

    private Integer value;//类型值
    private String desc;//类型描述
    private long total;//总量
    private long onlineCount;//在线
    private long offlineCount;//离线
    private List<DeviceBaseStatVo> child;//类型下的子类型的统计情况

    public DeviceBaseStatVo() {
    }

    public DeviceBaseStatVo(Integer value, String desc, long total) {
        this.value = value;
        this.desc = desc;
        this.total = total;
        this.onlineCount = 0L;
        this.offlineCount = 0L;
        this.child = null;
    }

    /**
     * 统计全部技防设备数量
     *
     * @param devices
     * @return
     */
    public static List<DeviceBaseStatVo> deviceCount(List<Device> devices) {
        return
                Optional.ofNullable(devices).map(n -> {
                    List<DeviceBaseStatVo> statVos = new ArrayList<DeviceBaseStatVo>();
                    n.stream().collect(Collectors.groupingBy(Device::getApeType, Collectors.counting())).forEach((k, v) -> statVos.add(new DeviceBaseStatVo(k, ApeType.valueBy(k).getDesc(), v)));
                    return statVos;
                }).orElse(null);
    }

}
