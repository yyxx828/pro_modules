package com.xajiusuo.busi.backupandops.controller;

import com.xajiusuo.busi.backupandops.entity.ComserverVo;
import com.xajiusuo.busi.backupandops.entity.OpsDevVo;
import com.xajiusuo.busi.backupandops.entity.ResultVo;
import com.xajiusuo.busi.backupandops.service.OpsService;
import com.xajiusuo.busi.device.entity.FireDevice;
import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.enums.ApeType;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.busi.backupandops.backopsutils.BackopsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xajiusuo.busi.backupandops.entity.OpsDevVo.*;

/**
 * Created by wangdou on 2019/6/17
 */
@Slf4j
@Api(description = "可视化运维管理")
@RestController
@RequestMapping(value = "/api/village/visops")
public class VisopsController extends BaseController {

    @Value("${device.path}")
    private String devicepath;
    @Value("${com.serverpath}")
    private String comserverpath;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private OpsService opsService;

    /**
     * 机房服务器查询
     *
     * @return
     */
    @ApiOperation(value = "机房服务器查询", httpMethod = "GET")
    @RequestMapping(value = "/queryComServer", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean queryComServer() {
        try {
            List<ResultVo> dbObject = new ArrayList<>();
            List<ResultVo> webObject = new ArrayList<>();
            List<String> list;
            if (!BackopsUtil.operatingSys()) {
                list = BackopsUtil.readerTxt("D:\\backops\\comserver\\machineInformation.txt");
            } else {
                list = BackopsUtil.readerTxt(comserverpath + "machineInformation.txt");
            }
            for (String s : list) {
                String[] value = s.split(",");
                for (int i = 1; i < value.length; i++) {
                    if ("db".equals(value[0].split(":")[1])) {
                        dbObject.add(new ResultVo(value[i].split(":")[0], value[i].split(":")[1]));
                    } else {
                        webObject.add(new ResultVo(value[i].split(":")[0], value[i].split(":")[1]));
                    }
                }
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(new ComserverVo(dbObject, webObject)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getResponseBean(Result.QUERY_FAIL);
    }

    /**
     * 统计各类设备和在线量
     *
     * @return
     */
    @ApiOperation(value = "统计各类设备和在线量", httpMethod = "GET")
    @RequestMapping(value = "/tjVariousTypeDevice", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean tjVariousTypeDevice() {
        List<OpsDevVo> resultList = new ArrayList<>();
        List<Object[]> list = deviceService.tjVariousTypeDevice();
        for (Object o : list) {
            Object[] oo = (Object[]) o;
            List childTypeList = deviceService.tjChildType(Integer.parseInt(oo[0].toString()));
            if (CollectionUtils.isEmpty(childTypeList)) {
                resultList.add(new OpsDevVo(Integer.parseInt(oo[0].toString()), ApeType.valueBy(Integer.parseInt(oo[0].toString())).getDesc(), Integer.parseInt(oo[1].toString()), this.getOnLineCount(oo[2]), null));
            } else {
                List<NameAndInt> child = new ArrayList<>();
                for (Object o1 : childTypeList) {
                    Object[] oo1 = (Object[]) o1;
                    if (ApeType.VIDEO.getValue() == Integer.parseInt(oo[0].toString())) {
                        child.add(new NameAndInt(VideoDevice.VideoDeviceType.valueBy(Integer.parseInt(oo1[0].toString())).getDesc(), Integer.parseInt(oo1[1].toString())));
                    } else if (ApeType.FIRE.getValue() == Integer.parseInt(oo[0].toString())) {
                        child.add(new NameAndInt(FireDevice.FireDeviceType.valueBy(Integer.parseInt(oo1[0].toString())).getDesc(), Integer.parseInt(oo1[1].toString())));
                    }
                }
                resultList.add(new OpsDevVo(Integer.parseInt(oo[0].toString()), ApeType.valueBy(Integer.parseInt(oo[0].toString())).getDesc(), Integer.parseInt(oo[1].toString()), this.getOnLineCount(oo[2]), child));
            }
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(resultList));
    }

    /**
     * 根据ip获得在线数
     *
     * @param ips
     * @return
     */
    private String getOnLineCount(Object ips) {
        if (null != ips && !"".equals(ips)) {
            int i = 0;
            if (!StringUtils.isEmpty(ips)) {
                Map<String, Object> map = opsService.getDevStatus(ips.toString());
                for (String s : map.keySet()) {
                    if ("网络联通".equals(map.get(s))) {
                        i++;
                    }
                }
            }
            return i + "";
        } else {
            return null;
        }
    }

//    @Scheduled(cron = "0 */5 * * * ?")
    public void writeDvIp() {
        //获取所有设备ip写入ping.host
        List<String> ipList = deviceService.getDeviceIPv4s();
        if (!BackopsUtil.operatingSys()) {
            BackopsUtil.writTxt(ipList, "D:\\backops\\device\\ping.host");
        } else {
            BackopsUtil.writTxt(ipList, devicepath + "ping.host");
        }
    }

    /**
     * 测试根据ip获取状态
     *
     * @param ips
     * @return
     */
    @ApiOperation(value = "根据ip获取状态", httpMethod = "GET")
    @RequestMapping(value = "/getDevStatus", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ips", required = false, value = "可为空,多个ip用,号分割", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getDevStatus(String ips) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(opsService.getDevStatus(ips)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getResponseBean(Result.QUERY_FAIL);
    }

}
