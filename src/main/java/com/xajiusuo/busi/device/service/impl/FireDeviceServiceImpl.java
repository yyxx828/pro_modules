package com.xajiusuo.busi.device.service.impl;

import com.xajiusuo.busi.device.dao.FireDeviceDao;
import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.FireDevice;
import com.xajiusuo.busi.device.enums.ApeType;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.device.service.FireDeviceService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * @author lizhidong
 * @date 2019-6-24 11:08
 */
@Service
public class FireDeviceServiceImpl extends BaseServiceImpl<FireDevice, String> implements FireDeviceService {

    @Autowired
    private FireDeviceDao fireDeviceDao;
    @Autowired
    private DeviceService deviceService;

    @Override
    public BaseDao<FireDevice, String> getBaseDao() {
        return fireDeviceDao;
    }

    @Override
    public FireDevice getFireDeviceInfo(String deviceId) {
        return Optional.ofNullable(this.fireDeviceDao.findByDeviceIdAndDelFlagFalse(deviceId)).map(n -> (n.size() > 0) ? n.get(0) : null).orElse(null);
    }

    @Override
    public List<Device> getFireDevices(String villageId) {
        String sql = "select d.id, d.villageid, d.apeid, d.name, d.model, d.factory, d.place, f.type as apetype, d.installtime, d.longitude, d.latitude, d.memo, " +
                "d.isonline, d.ipaddr, d.ipv6addr, d.createtime, d.lastmodifytime, d.createuid, d.lastmodifyuid, d.createname, d.delflag, d.region " +
                "from t_device_20 d left join t_firedevice_20 f on d.apeid = f.deviceid where d.apetype = 10 and d.delflag = 0";
        return this.deviceService.executeNativeQuerySql(sql);
    }

    @Override
    public Page queryFireDevicePage(HttpServletRequest request, Pageable pageable, Device device) {
        StringBuilder sql = new StringBuilder("select d.id, d.villageid, d.apeid, d.name, d.model, d.factory, d.place, f.type as apetype, d.installtime, d.longitude, d.latitude, d.memo, " +
                "d.isonline, d.ipaddr, d.ipv6addr, d.createtime, d.lastmodifytime, d.createuid, d.lastmodifyuid, d.createname, d.delflag, d.region " +
                "from t_device_20 d left join t_firedevice_20 f on d.apeid = f.deviceid where d.apetype = 10 and d.delflag = 0");
        if (StringUtils.isNotEmpty(device.getVillageId())) {
            sql.append(" and d.villageId = '").append(device.getVillageId()).append("'");
        }
        if (StringUtils.isNotEmpty(device.getApeId())) {
            sql.append(" and d.apeId = '").append(device.getApeId()).append("'");
        }
        if (StringUtils.isNotEmpty(device.getName())) {
            sql.append(" and d.name like '%").append(device.getName()).append("%'");
        }
        if (StringUtils.isNotEmpty(device.getModel())) {
            sql.append(" and d.model = '").append(device.getModel()).append("'");
        }
        if (StringUtils.isNotEmpty(device.getFactory())) {
            sql.append(" and d.factory like '%").append(device.getFactory()).append("%'");
        }
        if (null != device.getApeType()) {
            sql.append(" and f.type = ").append(device.getApeType());
        }
        if (StringUtils.isNotEmpty(device.getInstallTimeRange()) && device.getInstallTimeRange().split(",").length == 2) {
            String[] times = device.getInstallTimeRange().split(",");
            sql.append(" and d.installTime >= to_date('").append(times[0]).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and d.installTime <= to_date('").append(times[1]).append(" 23:59:59','yyyy-MM-dd hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(device.getCreateTimeRange()) && device.getCreateTimeRange().split(",").length == 2) {
            String[] times = device.getCreateTimeRange().split(",");
            sql.append(" and d.createTime >= to_date('").append(times[0]).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and d.createTime <= to_date('").append(times[1]).append(" 23:59:59','yyyy-MM-dd hh24:mi:ss')");
        }
        return this.deviceService.executeQuerySqlByPage(pageable, sql.toString());
    }
}
