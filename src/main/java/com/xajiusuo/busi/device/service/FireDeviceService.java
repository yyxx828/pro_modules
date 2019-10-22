package com.xajiusuo.busi.device.service;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.FireDevice;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-24 11:09
 */
public interface FireDeviceService extends BaseService<FireDevice, String> {

    FireDevice getFireDeviceInfo(String deviceId);

    List<Device> getFireDevices(String villageId);

    Page queryFireDevicePage(HttpServletRequest request, Pageable pageable, Device device);
}
