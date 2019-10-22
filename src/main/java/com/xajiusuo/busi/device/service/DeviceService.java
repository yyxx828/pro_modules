package com.xajiusuo.busi.device.service;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.vo.DeviceBaseStatVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lizhidong
 * @date 2019-6-6 16:21
 */
public interface DeviceService extends BaseService<Device, String> {

    String save(HttpServletRequest request, Device device);

    @Deprecated
    Device update(HttpServletRequest request, Device device);

    void delete(HttpServletRequest request, String id);

    Page querPage(HttpServletRequest request, Pageable pageable, Device device);

    List<Device> getDevicesByApeType(String villageId, Integer apeType, String name);

    /**
     * 获取多个类型的设备信息，按类型分组
     *
     * @param villageId 小区编码
     * @param apeTypes  设备类型，之间用,分割
     * @return
     */
    Map<Integer, List<Device>> getDevicesByApeTypes(String villageId, String apeTypes);

    /**
     * 验证设备是否存在，存在为true，否则为false
     *
     * @param apeId 设备编码
     * @param id    设备实体ID，区分新增或修改时验证
     * @return
     */
    boolean existsDevice(String apeId, String id);

    /**
     * 获取所有技防设备的IPv4
     *
     * @return
     */
    List<String> getDeviceIPv4s();

    /**
     * 获取指定设备编码的设备信息
     *
     * @param apeIds 设备编码
     * @return
     */
    List<Device> getDevicesByApeIds(Set<String> apeIds);

    Device getDeviceByApeId(String apeId);

    /**
     * 获取指定小区各类型技防设备数量
     *
     * @param villageId 小区编号
     * @return
     */
    List<DeviceBaseStatVo> getDeviceCount(String villageId);

    /**
     * 统计各类设备和在线量
     *
     * @return
     * @Author wangdou
     */
    List<Object[]> tjVariousTypeDevice();

    /**
     * 统计type为3或者10的子类数据
     *
     * @return
     * @Author wangdou
     */
    List<Object[]> tjChildType(int i);
}
