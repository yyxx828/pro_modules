package com.xajiusuo.busi.diction.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.diction.entity.CitysData;
import com.xajiusuo.busi.diction.entity.DepartPolice;
import com.xajiusuo.busi.diction.entity.ProvincesData;
import com.xajiusuo.busi.diction.service.CitysDataService;
import com.xajiusuo.busi.diction.service.DepartPoliceService;
import com.xajiusuo.busi.diction.service.ProvincesDataService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.JSONUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author GaoYong
 * @Date 19-6-6 下午5:27
 * @Description 分局字典维护
 */
@Slf4j
@Api(description = "分局(行政区域)码表维护方法")
@RestController
@RequestMapping(value = "/api/village/departPolice")
public class DepartPoliceController extends BaseController {

    @Autowired
    private DepartPoliceService departPoliceService;

    @Autowired
    private ProvincesDataService provincesDataService;

    @Autowired
    private CitysDataService citysDataService;

    @ApiOperation(value = "行政区域区县查询")
    @RequestMapping(value = "/getSelectAreaKeyValue", method = RequestMethod.GET)
    @ApiImplicitParams({
//             @ApiImplicitParam(name="villagename",required = false,value="小区名称", paramType = "query", dataType="String"),
    })
    @ResponseBody
    public ResponseBean getSelectAreaKeyValue(){
        java.util.List<ProvincesData> list;
        HashMap data = new HashMap();
        try{
            list =  this.provincesDataService.getAll();
            JSONArray result = (JSONArray) JSONUtils.autoRemoveField(JSONObject.toJSON(list),true,"id","contryname","contryid","provinceid","provincename","cityid","cityname","lon","lat","phonecode","cityname","delFlag");
            for(Object jsonObject:result){
                JSONObject a = (JSONObject) jsonObject;
                data.put(a.get("areaid"),a);
            }
            log.info("获取行政区域");
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.QUERY_FAIL);
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(data.values().toArray()));
    }

    @ApiOperation(value = "行政区域乡镇查询")
    @RequestMapping(value = "/getSelectContryAreaKeyValue", method = RequestMethod.GET)
    @ApiImplicitParams({
             @ApiImplicitParam(name="areaid",required = false,value="区县编码", paramType = "query", dataType="String"),
    })
    @ResponseBody
    public ResponseBean getSelectContryAreaKeyValue(String areaid){
        java.util.List<CitysData> list;
        try{
            list =  this.citysDataService.findBy("areaid",areaid);
            log.info("获取行政区域");
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.QUERY_FAIL);
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }


    @ApiOperation(value ="所属分局查询")
    @RequestMapping(value = "/getSelectDepartPoliceArea", method = RequestMethod.GET)
    @ApiImplicitParams({
//            @ApiImplicitParam(name="areaid", required = false, value ="所属区域", paramType = "query", dataType="String")
    })
    @ResponseBody
    public ResponseBean getSelectDepartPoliceArea(){
        java.util.List<DepartPolice> list;
        try {
            String sql = "select * from  P_DEPARTPOLICE_19 where 1=1  and delflag = ? ";
            list = this.departPoliceService.executeNativeQuerySql(sql, "0");
            log.info("获取分局信息");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.QUERY_FAIL);
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }


    @ApiOperation(value="分局码表新增/修改")
    @RequestMapping(value = "/addEditDepartPolice",method = RequestMethod.POST)
    @ApiImplicitParams({
//             @ApiImplicitParam(name="villagename",required = false,value="小区名称", paramType = "query", dataType="String"),
    })
    @ResponseBody
    public ResponseBean addEditDepartPolice(DepartPolice departPolice){

            try {
                this.departPoliceService.saveOrUpdate(departPolice);
                log.info("分局字典表更新成功");
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
            }

        return getResponseBean(Result.OPERATE_SUCCESS);
    }

    @ApiOperation(value="分局码表删除")
    @RequestMapping(value="delDepartPolice", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "唯一标识", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delDepartPolice(String id){
        try {
            DepartPolice departPolice = this.departPoliceService.getOne(id);
            departPolice.setDelFlag(true);
            this.departPoliceService.update(departPolice);
            log.info("分局字典表删除成功(软删除)");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            getResponseBean(Result.DELETE_FAIL);
        }
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    @ApiOperation(value="分局码表查看")
    @RequestMapping(value = "/viewDepartPolice", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "唯一标识", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewDepartPolice(String id){
        DepartPolice departPolice = null;
        try {
            departPolice =  this.departPoliceService.getOne(id);
            log.info("分局字典查看");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return getResponseBean(Result.OPERATE_SUCCESS);
    }


}
