package com.xajiusuo.busi.entranceandexit.controller;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceCard;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceCardService;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceCard;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceCardService;
import com.xajiusuo.busi.entranceandexit.vo.AccessCardVo;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.service.OwnerService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.down.FileContentType;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by wangdou on 2019/7/1
 */
@Slf4j
@Api(description = "门禁卡管理")
@RestController
@RequestMapping(value = "/api/village/accessCard")
public class AccessCardController extends BaseController {

    @Autowired
    private GateEntranceCardService gateEntranceCardService;//出入口
    @Autowired
    private BuildingEntranceCardService buildingEntranceCardService;//楼宇
    @Autowired
    private OwnerService ownerService;

    /**
     * 门禁基本信息列表查询
     */
    @ApiOperation(value = "门禁基本信息列表查询", httpMethod = "GET")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "personidnumber", required = false, value = "证件号码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "entrancecardno", required = false, value = "门禁卡编号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean query(Pageable page, String personidnumber, String entrancecardno) {
        List<AccessCardVo> ulist = new ArrayList<>();
        //出入口门禁卡所有数据
        List<GateEntranceCard> gateEntranceCardList = gateEntranceCardService.getAll();
        //楼宇门禁卡所有数据
        List<BuildingEntranceCard> buildingEntranceCardList = buildingEntranceCardService.getAll();
        if (!CollectionUtils.isEmpty(gateEntranceCardList)) {
            gateEntranceCardList.forEach(t -> {
                AccessCardVo accessCardVo = new AccessCardVo();
                accessCardVo.setId(t.getId());
                accessCardVo.setVillageid(t.getVillageid());
                accessCardVo.setEntrancecardtype(t.getEntrancecardtype());
                accessCardVo.setEntrancecardno(t.getEntrancecardno());
                accessCardVo.setIsvalid(t.getIsvalid());
                accessCardVo.setPasstime(t.getPasstime());
                accessCardVo.setPersonidnumber(t.getPersonidnumber());
                if (!StringUtils.isEmpty(t.getPersonidnumber())) {
                    List<Owner> ownerList = ownerService.findBy("IDNUMBER", t.getPersonidnumber());
                    accessCardVo.setPersonname(CollectionUtils.isEmpty(ownerList) ? "" : ownerList.get(0).getName());
                } else {
                    accessCardVo.setPersonname(null);
                }
                accessCardVo.setAuthscope(1);
                buildingEntranceCardList.forEach(n -> {
                    if (n.getEntrancecardno().equals(t.getEntrancecardno())) {
                        accessCardVo.setAuthscope(0);
                    }
                });
                ulist.add(accessCardVo);
                buildingEntranceCardList.removeIf(next -> next.getEntrancecardno().equals(t.getEntrancecardno()));
            });
        }
        if (!CollectionUtils.isEmpty(buildingEntranceCardList)) {
            buildingEntranceCardList.forEach(t -> {
                AccessCardVo accessCardVo = new AccessCardVo();
                accessCardVo.setId(t.getId());
                accessCardVo.setVillageid(t.getVillageid());
                accessCardVo.setEntrancecardtype(t.getEntrancecardtype());
                accessCardVo.setEntrancecardno(t.getEntrancecardno());
                accessCardVo.setIsvalid(t.getIsvalid());
                accessCardVo.setPasstime(t.getPasstime());
                accessCardVo.setAuthscope(2);
                accessCardVo.setPersonidnumber(t.getPersonidnumber());
                if (!StringUtils.isEmpty(t.getPersonidnumber())) {
                    List<Owner> ownerList = ownerService.findBy("IDNUMBER", t.getPersonidnumber());
                    accessCardVo.setPersonname(CollectionUtils.isEmpty(ownerList) ? "" : ownerList.get(0).getName());
                } else {
                    accessCardVo.setPersonname(null);
                }
                ulist.add(accessCardVo);
            });
        }
        Page newPage = null;
        if (!StringUtils.isEmpty(personidnumber)) {
            newPage = listToPage(ulist.stream().filter(t -> t.getPersonidnumber().equals(personidnumber)).sorted(Comparator.comparing(AccessCardVo::getPasstime, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList()), page.getPageNumber(), page.getPageSize());
        }
        if (!StringUtils.isEmpty(entrancecardno)) {
            newPage = listToPage(ulist.stream().filter(t -> t.getEntrancecardno().equals(entrancecardno)).sorted(Comparator.comparing(AccessCardVo::getPasstime, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList()), page.getPageNumber(), page.getPageSize());
        }
        if (!StringUtils.isEmpty(personidnumber) && !StringUtils.isEmpty(entrancecardno)) {
            newPage = listToPage(ulist.stream().filter(t -> t.getPersonidnumber().equals(personidnumber) && t.getEntrancecardno().equals(entrancecardno)).sorted(Comparator.comparing(AccessCardVo::getPasstime, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList()), page.getPageNumber(), page.getPageSize());
        }
        if (StringUtils.isEmpty(personidnumber) && StringUtils.isEmpty(entrancecardno)) {
            newPage = listToPage(ulist.stream().sorted(Comparator.comparing(AccessCardVo::getPasstime, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList()), page.getPageNumber(), page.getPageSize());
        }
        return Result.QUERY_SUCCESS.toBean(newPage);
    }

    /**
     * 新增或修改门禁卡基本信息
     *
     * @param accessCardVo
     * @return
     */
    @ApiOperation(value = "新增或修改门禁卡基本信息", notes = "门禁卡基本信息，新增时不要传id", httpMethod = "POST")
    @PostMapping(value = "/saveUpdateAccessCard")
    @ResponseBody
    public ResponseBean saveUpdateAccessCard(@RequestBody AccessCardVo accessCardVo) {
        try {
            if (!StringUtils.isEmpty(accessCardVo.getId())) {
                gateEntranceCardService.executeUpdateSql("delete from " + gateEntranceCardService.tableName() + " where ENTRANCECARDNO = '" + accessCardVo.getEntrancecardno() + "'");
                buildingEntranceCardService.executeUpdateSql("delete from " + buildingEntranceCardService.tableName() + " where ENTRANCECARDNO = '" + accessCardVo.getEntrancecardno() + "'");
            }
            accessCardVo.setPasstime(accessCardVo.getPasstime() == null ? new Date() : accessCardVo.getPasstime());
            if (0 == accessCardVo.getAuthscope()) {
                GateEntranceCard gateEntranceCard = new GateEntranceCard();
                BuildingEntranceCard buildingEntranceCard = new BuildingEntranceCard();
                BeanUtils.copyProperties(accessCardVo, gateEntranceCard);
                gateEntranceCard.setId(null);
                gateEntranceCardService.saveUpdate(gateEntranceCard);
                BeanUtils.copyProperties(accessCardVo, buildingEntranceCard);
                buildingEntranceCard.setId(null);
                buildingEntranceCardService.saveUpdate(buildingEntranceCard);
            } else if (1 == accessCardVo.getAuthscope()) {
                GateEntranceCard gateEntranceCard = new GateEntranceCard();
                BeanUtils.copyProperties(accessCardVo, gateEntranceCard);
                gateEntranceCard.setId(null);
                gateEntranceCardService.saveUpdate(gateEntranceCard);
            } else {
                BuildingEntranceCard buildingEntranceCard = new BuildingEntranceCard();
                BeanUtils.copyProperties(accessCardVo, buildingEntranceCard);
                buildingEntranceCard.setId(null);
                buildingEntranceCardService.saveUpdate(buildingEntranceCard);
            }
            return Result.SAVE_SUCCESS.toBean();
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return Result.SAVE_FAIL.toBean();
    }

    /**
     * 根据ID查询门禁卡基本信息
     *
     * @param entrancecardno
     * @return
     */
    @ApiOperation(value = "根据ID查询门禁卡基本信息", httpMethod = "GET")
    @RequestMapping(value = "/getAccessCardByNo", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entrancecardno", required = true, value = "门禁卡编号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getAccessCardByNo(String entrancecardno) {
        try {
            GateEntranceCard gateEntranceCarde = gateEntranceCardService.getOne("entrancecardno", entrancecardno);
            AccessCardVo accessCardVo = new AccessCardVo();
            BeanUtils.copyProperties(gateEntranceCarde, accessCardVo);
            return Result.QUERY_SUCCESS.toBean(accessCardVo);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return Result.QUERY_FAIL.toBean();
    }

    /**
     * 删除门禁卡基本信息
     *
     * @param entrancecardno
     * @return
     */
    @ApiOperation(value = "删除门禁卡基本信息", httpMethod = "DELETE")
    @RequestMapping(value = "/deleteAccessCard", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entrancecardno", required = true, value = "门禁卡编号,多个编号用,号分割", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean deleteAccessCard(String entrancecardno) {
        String[] ids = entrancecardno.split(",");
        for (String id : ids) {
            gateEntranceCardService.getBaseDao().deleteBy("entrancecardno", id);
            buildingEntranceCardService.getBaseDao().deleteBy("entrancecardno", id);
        }
        return Result.DELETE_SUCCESS.toBean();
    }

    @RequestMapping(value = "/downTemplete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "门禁卡信息模板下载", httpMethod = "GET")
    public ResponseBean downTemplete() {
        try {
            ModelExcel modelExcel = new ModelExcel();
            String[] handers = new String[]{"小区编号*", "门禁卡类型", "门禁卡编号*", "是否有效(1有效0无效)", "采集时间", "授权范围(0全部1出入口2楼宇)*", "证件号码*", "业主姓名"};
            List<String[]> downData = new ArrayList<>();
            String[] downRows = {};
            String fileName = "门禁卡信息导入模板.xls";
            modelExcel.createExcelTemplate(fileName, handers, downData, downRows);
            fileDownOrShow(fileName, FileContentType.xls, new FileInputStream(new File(fileName)));
            if (new File(fileName).exists()) new File(fileName).delete();
            return Result.OPERATE_SUCCESS.toBean();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.OPERATE_FAIL.toBean();
        }
    }

    @ApiOperation(value = "门禁卡信息导入")
    @RequestMapping(value = "uploadAccessCarExcel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean uploadAccessCarExcel(@RequestParam(name = "file", required = true) MultipartFile file) {
        try {
            InputStream in = file.getInputStream();
            List result = gateEntranceCardService.uploadAccessCarExcel(in);
            return Result.OPERATE_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.find(CfI.R_EXCELLOG_ALL_CHECK_FAIL).toBean();
        }
    }
}
