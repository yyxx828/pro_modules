package com.xajiusuo.busi.entranceandexit.controller;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.diction.controller.DictionController;
import com.xajiusuo.busi.entranceandexit.entity.FollowOwnerVo;
import com.xajiusuo.busi.entranceandexit.entity.FollowPerson;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.service.FollowPersonService;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceInOutService;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonDetialsVo;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonRelationVo;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonVo;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInfoService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/17.
 */
@Slf4j
@Api(description = "关注人员")
@RestController
@RequestMapping(value = "/api/village/FollowPerson")
public class FollowPersonController extends BaseController {

    @Autowired
    private FollowPersonService followPersonService;
    @Autowired
    private GateEntranceGuardLogService gateEntranceGuardLogService; //出入口门禁采集
    @Autowired
    private BuildingEntranceGuardLogService buildingEntranceGuardLogService; //楼宇门禁采集
    @Autowired
    private GateEntranceInOutService gateEntranceInOutService;
    @Autowired
    private DictionController dictionController; //字典信息


    /**
     * @Author shirenjing
     * @Description 
     * @Date 9:17 2019/6/18
     * @Param {
    "followType": "老人",
    "owner": {
    "id": "123456"
    }
    }
     * @return
     **/
    @PostMapping(value = "/markingFollowPerson")
    @ApiOperation(value = "关注人员打标", httpMethod = "POST")
    @ResponseBody
    public ResponseBean markingFollowPerson(@RequestBody FollowPerson followPerson) {
        UserInfoVo userinfo = UserUtil.getCurrentUser(request);
        if(userinfo==null) userinfo = new UserInfoVo();
        Result successResult = null;
        Result failResult = null;
        if(CommonUtils.isNotEmpty(followPerson.getId())){
            successResult = Result.UPDATE_SUCCESS;
            failResult = Result.UPDATE_FAIL;
        }else{
            successResult = Result.SAVE_SUCCESS;
            failResult = Result.SAVE_FAIL;
        }
        try{
            followPersonService.saveUpdate(followPerson);
            return getResponseBean(successResult);
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(failResult);
        }
    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 10:33 2019/6/21
     * @Param [pageable, followPersonVo]
     * @return
     **/
    @GetMapping(value = "/queryPageFollowperson")
    @ApiOperation(value = "关注人员列表信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryPageFollowperson(Pageable pageable, FollowPersonVo followPersonVo){
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "lastModifyTime"),true);
        try{
            Page<Map> page = followPersonService.queryPageFollowPerson(pageable,followPersonVo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 10:33 2019/6/21
     * @Param [id]
     * @return
     **/
    @ApiOperation(value = "查看关注人员详情", httpMethod = "GET")
    @RequestMapping(value = "/getFollowPersonDetials/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getFollowPersonDetials(@PathVariable(value = "id") String id) {
        try{
            FollowPersonDetialsVo result = followPersonService.queryDetials(id);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }

    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 10:33 2019/6/21
     * @Param [idcard]
     * @return
     **/
    @ApiOperation(value = "获取关注人员亲属信息", httpMethod = "GET")
    @RequestMapping(value = "/getFollowPersonQs/{idcard}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getFollowPersonQs(@PathVariable(value = "idcard") String idcard) {
        try{
            List<FollowPersonRelationVo> result = followPersonService.queryRelations(idcard);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }

    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 10:33 2019/6/21
     * @Param [page, gateEntranceGuardLog]
     * @return
     **/
    @ApiOperation(value = "获取关注人出入口门禁采集信息", httpMethod = "GET")
    @RequestMapping(value = "/queryGateEntranceGuardLog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passtime,asc),", example = "passtime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryGateEntranceGuardLog(Pageable pageable, @RequestParam(value = "idCard") String idCard,
                              String passtimes) {
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "passtime"),true);
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        }
        try{
            Page<GateEntranceGuardLog> result = gateEntranceGuardLogService.queryPageLog(pageable,idCard,passtimeArr);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 11:27 2019/6/21
     * @Param [pageable, idCard, passtimes]
     * @return
     **/
    @ApiOperation(value = "获取关注人楼宇门禁采集信息", httpMethod = "GET")
    @RequestMapping(value = "/queryBuildingEntranceGuardLog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryBuildingEntranceGuardLog(Pageable pageable, @RequestParam(value = "idCard") String idCard,
                              String passtimes) {
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "passtime"),true);
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        }
        try{
            Page<BuildingEntranceGuardLog> result = buildingEntranceGuardLogService.queryPageLog(pageable,idCard,passtimeArr);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }



    @ApiOperation(value = "出入口门禁采集信息/楼宇门禁采集信--频繁出入", httpMethod = "GET")
    @RequestMapping(value = "/queryOftenInOut", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为nums,desc),", example = "nums,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "count", required = false,  value = "次数", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, defaultValue = "0", value = "0:出入口门禁 1：楼宇门禁", paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean queryOftenInOut(Pageable pageable,String count,String passtimes,int flag) {
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "nums"),true);
//      int count = Configs.find(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_NUMS).getInt();
        int cs = 1;  //用于与前台联调测试
        if(CommonUtils.isNotEmpty(count)) cs = Integer.parseInt(count);
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        }
        try{
            Page<Map> result = gateEntranceInOutService.queryOftenInOut(pageable,passtimeArr,cs,flag);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @ApiOperation(value = "出入口门禁采集信息/楼宇门禁采集信--昼伏夜出", httpMethod = "GET")
    @RequestMapping(value = "/queryNightOut", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为nums,desc),", example = "nums,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "count", required = false, defaultValue = "2", value = "次数", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "housrs", required = false, value = "时间段，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, defaultValue = "0", value = "0:出入口门禁 1：楼宇门禁", paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean queryNightOut(Pageable pageable,String count,String passtimes,String housrs,int flag) {
        Integer[] hs = new Integer[2];
        if(!CommonUtils.isNotEmpty(housrs)){
            housrs = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_TIMERANGE).getValue();
        }
        if(housrs.contains(",")){
            hs[0] = Integer.parseInt(housrs.split(",")[0]);
            hs[1] = Integer.parseInt(housrs.split(",")[1]);
        }
//        if(count==0) count = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_NUMS).getInt();
        int cs = 1;  //用于与前台联调测试
        if(CommonUtils.isNotEmpty(count)) cs = Integer.parseInt(count);
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        }
        try{
            Page<Map> result = gateEntranceInOutService.queryNightOut(pageable,passtimeArr,cs,flag,hs);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "出入口门禁采集信息/楼宇门禁采集信息--频繁出入详单", httpMethod = "GET")
    @RequestMapping(value = "/queryLogOftenInOut", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
//            @ApiImplicitParam(name = "count", required = true, defaultValue = "2", value = "次数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "passtimes", required = true, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "entrancecardno", required = true, value = "门禁卡号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, defaultValue = "0", value = "0:出入口门禁 1：楼宇门禁", paramType = "query", dataType = "int")

    })
    @ResponseBody
    public ResponseBean queryLogOftenInOut(Pageable pageable,String passtimes,String entrancecardno,int flag) {
        String[] passtimeArr = passtimes.split(",");
        try{
            if(flag==0){
                Page<Map> result = gateEntranceGuardLogService.queryPageLogByOftenInOut(pageable,passtimeArr,entrancecardno);
                return getResponseBean(Result.QUERY_SUCCESS.setData(result));
            }else{
                Page<Map> result = buildingEntranceGuardLogService.queryPageLogByOftenInOut(pageable,passtimeArr,entrancecardno);
                return getResponseBean(Result.QUERY_SUCCESS.setData(result));
            }
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @ApiOperation(value = "出入口门禁采集信息/楼宇门禁采集信息--昼伏夜出详单", httpMethod = "GET")
    @RequestMapping(value = "/queryLogNightOut", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
//            @ApiImplicitParam(name = "count", required = true, defaultValue = "2", value = "次数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "passtimes", required = true, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "entrancecardno", required = true, value = "门禁卡号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "housrs", required = true, value = "时间段，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, defaultValue = "0", value = "0:出入口门禁 1：楼宇门禁", paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean queryLogNightOut(Pageable pageable,String passtimes,String entrancecardno,String housrs,int flag) {
        Integer[] hs = new Integer[2];
        if(housrs.contains(",")){
            hs[0] = Integer.parseInt(housrs.split(",")[0]);
            hs[1] = Integer.parseInt(housrs.split(",")[1]);
        }else{
            hs[0] = Integer.parseInt(housrs);
            hs[1] = Integer.parseInt(housrs);
        }
        String[] passtimeArr = passtimes.split(",");
        try{
            if(flag==0){
                Page<Map> result = gateEntranceGuardLogService.queryPageLogByNightOut(pageable,passtimeArr,entrancecardno,hs);
                return getResponseBean(Result.QUERY_SUCCESS.setData(result));
            }else {
                Page<Map> result = buildingEntranceGuardLogService.queryPageLogByNightOut(pageable,passtimeArr,entrancecardno,hs);
                return getResponseBean(Result.QUERY_SUCCESS.setData(result));
            }
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getOwners")
    @ApiOperation(value = "获取业主信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fieldname", required = true, value = "字段名 name：业主姓名 idnumber：业主证件编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fieldvalue", required = true, value = "字段值",  paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getOwners(String fieldname, String fieldvalue) {
        try {
            List<FollowOwnerVo> list = followPersonService.queryUnFollowOwner(fieldname,fieldvalue);
            return Result.QUERY_SUCCESS.toBean(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }

    }

    @GetMapping(value = "/getOwnersNew")
    @ApiOperation(value = "获取业主信息new", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", required = true, value = "字段值",  paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getOwnersNew(String value) {
        try {
            List<FollowOwnerVo> list = followPersonService.queryUnFollowOwner("idnumber",value);
            if(list==null || list.size()==0){
                list = followPersonService.queryUnFollowOwner("name",value);
            }
            return Result.QUERY_SUCCESS.toBean(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }

    }

    @RequestMapping(value = "/deleteFollow/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关注", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "关注信息id", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean deleteInfo(@PathVariable(value = "id") String id) {
        try {
            followPersonService.delete(id);
            return Result.DELETE_SUCCESS.toBean();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.DELETE_FAIL.toBean();
        }
    }

    @ApiOperation(value = "关注人员字典下拉")
    @RequestMapping(value = "getFollowDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getFollowDiction(){
        //租赁状态字典下拉码值
        try{
            String rental = Configs.find(CfI.C_FOLLOWPERSON_TYPE_DICTION).getValue();
            return dictionController.listDictions(rental);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }




}
