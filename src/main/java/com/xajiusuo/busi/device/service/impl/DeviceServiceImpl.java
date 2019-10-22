package com.xajiusuo.busi.device.service.impl;

import com.xajiusuo.busi.device.dao.DeviceDao;
import com.xajiusuo.busi.device.entity.BuildingEntranceDevice;
import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.FireDevice;
import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.enums.ApeType;
import com.xajiusuo.busi.device.service.BuildingEntranceDeviceService;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.device.service.FireDeviceService;
import com.xajiusuo.busi.device.service.VideoDeviceService;
import com.xajiusuo.busi.device.vo.DeviceBaseStatVo;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhidong
 * @date 2019-6-6 16:22
 */
@Slf4j
@Service
public class DeviceServiceImpl extends BaseServiceImpl<Device, String> implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private VideoDeviceService videoDeviceService;
    @Autowired
    private FireDeviceService fireDeviceService;
    @Autowired
    private BuildingEntranceDeviceService buildingEntranceDeviceService;

    @Override
    public BaseDao<Device, String> getBaseDao() {
        return deviceDao;
    }

    @Override
    public String save(HttpServletRequest request, Device device) {
        if (null == device)
            return null;
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if (null != userInfoVo) {
            device.setCreateUID(userInfoVo.getUserId());
            device.setCreateName(userInfoVo.getFullName());
            device.setLastModifyUID(userInfoVo.getUserId());
        }
        device.setCreateTime(new Date());
        device.setLastModifyTime(new Date());
        device.setDelFlag(false);
        return this.deviceDao.save(device).getId();
    }

    @Override
    public Device update(HttpServletRequest request, Device device) {
        if (null == device)
            return null;
        if (StringUtils.isNotEmpty(device.getId())) {
            Device oldDevice = this.deviceDao.getOne(device.getId());
            UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
            if (null != userInfoVo) {
                device.setLastModifyUID(userInfoVo.getUserId());
            }
            device.setLastModifyTime(new Date());
            device.setCreateUID(oldDevice.getCreateUID());
            device.setCreateName(oldDevice.getCreateName());
            device.setCreateTime(oldDevice.getCreateTime());
            device.setDelFlag(oldDevice.getDelFlag());
            return this.deviceDao.update(device);
        } else {
            throw new IllegalArgumentException("只能修改已存在信息！");
        }
    }

    @Override
    public void delete(HttpServletRequest request, String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("请传递正确的ID！");
        }
        this.deviceDao.findById(id).map(n -> {
            this.deviceDao.delete(n);
            //同时删除设备关联信息
            switch (ApeType.valueBy(n.getApeType())) {
                case VIDEO:
                    VideoDevice videoDevice = this.videoDeviceService.getVideoDeviceInfo(n.getApeId());
                    this.videoDeviceService.delete(videoDevice);
                    break;
                case DOOR_CONTROL:
                    BuildingEntranceDevice buildingEntranceDevice = this.buildingEntranceDeviceService.getBEDevice(n.getApeId());
                    this.buildingEntranceDeviceService.delete(buildingEntranceDevice);
                    break;
                case FIRE:
                    FireDevice fireDevice = this.fireDeviceService.getFireDeviceInfo(n.getApeId());
                    this.fireDeviceService.delete(fireDevice);
                    break;
                default:
                    break;
            }
            return null;
        });
    }

    @Override
    public Page querPage(HttpServletRequest request, Pageable pageable, Device device) {
        StringBuilder sql = new StringBuilder("select * from T_DEVICE_20 where 1=1");
        if (StringUtils.isNotEmpty(device.getVillageId())) {
            sql.append(" and villageId = '").append(device.getVillageId()).append("'");
        }
        if (StringUtils.isNotEmpty(device.getApeId())) {
            sql.append(" and apeId = '").append(device.getApeId()).append("'");
        }
        if (StringUtils.isNotEmpty(device.getName())) {
            sql.append(" and name like '%").append(device.getName()).append("%'");
        }
        if (StringUtils.isNotEmpty(device.getModel())) {
            sql.append(" and model = '").append(device.getModel()).append("'");
        }
        if (StringUtils.isNotEmpty(device.getFactory())) {
            sql.append(" and factory like '%").append(device.getFactory()).append("%'");
        }
        if (null != device.getIsOnline()) {
            sql.append(" and isOnline = ").append(device.getIsOnline());
        }
        if (null != device.getApeType()) {
            sql.append(" and apeType = ").append(device.getApeType());
        }
        if (StringUtils.isNoneEmpty(device.getIpAddr())) {
            sql.append(" and (ipAddr = '").append(device.getIpAddr()).append("' or ipv6Addr = '").append(device.getIpAddr()).append("')");
        }
        if (StringUtils.isNotEmpty(device.getInstallTimeRange()) && device.getInstallTimeRange().split(",").length == 2) {
            String[] times = device.getInstallTimeRange().split(",");
            sql.append(" and installTime >= to_date('").append(times[0]).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and installTime <= to_date('").append(times[1]).append(" 23:59:59','yyyy-MM-dd hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(device.getCreateTimeRange()) && device.getCreateTimeRange().split(",").length == 2) {
            String[] times = device.getCreateTimeRange().split(",");
            sql.append(" and createTime >= to_date('").append(times[0]).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and createTime <= to_date('").append(times[1]).append(" 23:59:59','yyyy-MM-dd hh24:mi:ss')");
        }
        return this.deviceDao.executeQuerySqlByPage(pageable, sql.toString());
    }

    /**
     * 获取小区下指定类型的技防设备信息
     * <p>
     * 如果不指定技防设备返回所有技防设备信息
     * </p>
     *
     * @param villageId 小区ID
     * @param apeType   技防设备类型
     * @param name      设备名称用于查询
     * @return
     */
    @Override
    public List<Device> getDevicesByApeType(String villageId, Integer apeType, String name) {
        if (StringUtils.isEmpty(villageId))
            return null;
        Device device = new Device();
        device.setName(name);
        device.setApeType(apeType);
        device.setDelFlag(false);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("apeType", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("delFlag", ExampleMatcher.GenericPropertyMatchers.exact());
        return this.deviceDao.findAll(Example.of(device, exampleMatcher));
    }

    /**
     * 获取多个类型的设备信息，按类型分组
     *
     * @param villageId 小区编码
     * @param apeTypes  设备类型，之间用,分割
     * @return
     */
    @Override
    public Map<Integer, List<Device>> getDevicesByApeTypes(String villageId, String apeTypes) {
        List<Integer> list = new ArrayList<>();
        for (String s : apeTypes.split(",")) {
            list.add(Integer.parseInt(s));
        }
        return Optional.ofNullable(this.deviceDao.findByVillageIdAndApeTypeIn(villageId, list)).map(n -> n.stream().collect(Collectors.groupingBy(Device::getApeType))).orElse(null);
    }

    /**
     * 验证设备是否存在，存在为true，否则为false
     *
     * @param apeId 设备编码
     * @return
     */
    @Override
    public boolean existsDevice(String apeId, String id) {
        if (StringUtils.isEmpty(id)) {
            return !StringUtils.isEmpty(apeId) && this.deviceDao.countByApeIdAndDelFlagFalse(apeId) > 0L;
        } else {
            return !StringUtils.isEmpty(apeId) && this.deviceDao.countByApeIdAndIdNotAndDelFlagFalse(apeId, id) > 0L;
        }
    }

    /**
     * 获取所有技防设备的IPv4
     *
     * @return
     */
    @Override
    public List<String> getDeviceIPv4s() {
        List<Device> list = this.deviceDao.findAll();
        return list.stream().filter(n -> n.getDelFlag().equals(false)).map(Device::getIpAddr).collect(Collectors.toList());
    }

    /**
     * 获取指定设备编码的设备信息
     *
     * @param apeIds 设备编码
     * @return
     */
    @Override
    public List<Device> getDevicesByApeIds(Set<String> apeIds) {
        return this.deviceDao.findByApeIdInAndDelFlagFalse(apeIds);
    }

    @Override
    public Device getDeviceByApeId(String apeId) {
        return Optional.ofNullable(this.deviceDao.findByApeIdInAndDelFlagFalse(Arrays.asList(apeId))).map(n -> n.size() > 0 ? n.get(0) : null).orElse(null);
    }

    @Override
    public List<DeviceBaseStatVo> getDeviceCount(String villageId) {
        return DeviceBaseStatVo.deviceCount(this.deviceDao.findByVillageIdAndDelFlagFalse(villageId));
    }

    @Override
    public List<Object[]> tjVariousTypeDevice() {
        return deviceDao.listBySQL("select APETYPE,count(1) zs,wm_concat(IPADDR) ips from T_DEVICE_20 where APETYPE != '9' and DELFLAG = '0' group by apetype order by zs");
    }

    @Override
    public List<Object[]> tjChildType(int i) {
        if (ApeType.VIDEO.getValue() == i) {
            return deviceDao.listBySQL("select TYPE,count(1) zs  from T_VIDEO_DEVICE_20 where DELFLAG = '0' group by TYPE order by zs");
        } else if (ApeType.FIRE.getValue() == i) {
            return deviceDao.listBySQL("select TYPE,count(1) zs  from T_FIREDEVICE_20 where DELFLAG = '0' group by TYPE order by zs");
        }
        return null;
    }
}
