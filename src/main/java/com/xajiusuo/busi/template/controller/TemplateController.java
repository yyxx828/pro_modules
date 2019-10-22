package com.xajiusuo.busi.template.controller;


import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.out.service.RemoteService;
import com.xajiusuo.busi.template.data.BusiProper;
import com.xajiusuo.busi.template.entity.BusField;
import com.xajiusuo.busi.template.entity.Business;
import com.xajiusuo.busi.template.entity.IntoRecord;
import com.xajiusuo.busi.template.entity.IntoTemp;
import com.xajiusuo.busi.template.service.BusFieldService;
import com.xajiusuo.busi.template.service.BusinessService;
import com.xajiusuo.busi.template.service.IntoRecordService;
import com.xajiusuo.busi.template.service.IntoTempService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.config.TableEntity;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.*;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Table;
import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(description = "模板管理")
@RestController
@RequestMapping(value = "/api/data/template")
public class TemplateController extends BaseController {

    final String itemTag = "template";
    final String itemName = "导入模板";

    @Autowired
    private IntoTempService intoTempService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusFieldService busFieldService;

    @Autowired
    private IntoRecordService intoRecordService;

    @Autowired
    private RemoteService remoteService;

    private LogSysService logSysService;


    private static SimpleDateFormat dataFormat_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "[BUSI - 00]待创建业务表下拉列表", httpMethod = "GET")
    @RequestMapping(value = "/queryEntity", method = RequestMethod.GET)
    public ResponseBean queryEntity() {
        List<Class> list = BeanUtils.getAllEntity();
        List<SelectList> voList = new ArrayList<SelectList>();

        List<Business> busList = businessService.getAll();
        List<String> tableList = new ArrayList<String>();
        for (Business b : busList) {
            tableList.add(b.getTname());
        }

        for (Class t1 : list) {
            Class<TableEntity> te = t1;
            ApiModel am = te.getAnnotation(ApiModel.class);
            Table table = te.getAnnotation(Table.class);

            if (table == null || tableList.contains(table.name().toLowerCase()) || am == null || StringUtils.isBlank(am.value()) || table.name().toLowerCase().startsWith("f_")) {
                continue;
            }
            voList.add(new SelectList(am.value(), am.description() + "[" + am.value() + "]", null));
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(voList));
    }

    @ApiOperation(value = "[BUSI - 01]业务表表格列表", httpMethod = "GET")
    @RequestMapping(value = "/queryBusiness", method = RequestMethod.GET)
    public ResponseBean queryBusiness() {
        List<Business> list = businessService.getAll();
        return getResponseBean(Result.QUERY_SUCCESS.setData(JSONUtils.autoRemoveField(JSONObject.toJSON(list), true)));
    }

    /**
     * @desc 通过类名快捷创建业务表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @PostMapping(value = "/loadBusinessByName")
    @ApiOperation(value = "[BUSI - 02]通过类名快捷创建业务表", httpMethod = "POST")
    @ResponseBody
    public ResponseBean loadBusinessByName(String name) {
        name = name.toLowerCase();
        Business entity = businessService.autoSaveByName(name, request);
        if (entity == null) {
            return new ResponseBean(200, "业务表已存在或数据破坏异常");
        }
        return getResponseBean(Result.SAVE_SUCCESS.setData(entity));
    }

    /**
     * @desc 业务表修改
     * @author 杨勇
     * @Date 2019-01-03
     */
    @PostMapping(value = "/saveBusiness")
    @ApiOperation(value = "[BUSI - 03]业务表新增修改修改", httpMethod = "POST")
    @ResponseBody
    public ResponseBean saveBusiness(@RequestBody Business entity) {
        if (entity.getId() != null) {//
            Business old = businessService.getOne(entity.getId());
            if (BusiProper.isPool(old.getTcode())) {//如果业务表存在则不允许修改标识
                entity.setTcode(old.getTcode());
            }
        }
        businessService.save(entity);

        return getResponseBean(Result.SAVE_SUCCESS);
    }

    /**
     * @desc 业务表字段新增/修改
     * @author 杨勇
     * @Date 2019-01-03
     */
    @PostMapping(value = "/saveBusField")
    @ApiOperation(value = "[BUSI - 04]业务表字段新增/修改", httpMethod = "POST", response = BusField.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderType", required = true, value = "排序方式(1:互换,0:插入),新增默认插入", allowableValues = "1,0", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean saveBusField(@RequestBody BusField entity, Integer orderType) {
        Result bean = busFieldService.save(entity, orderType, UserUtil.getCurrentInfo(request));
        return bean.toBean();
    }

    /**
     * @desc 业务表字段删除
     * @author 杨勇
     * @Date 2019-01-03
     */
    @RequestMapping(value = "/deleteBusField/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "[BUSI - 05]业务表字段删除", httpMethod = "DELETE")
    @ResponseBody
    public ResponseBean deleteBusField(@PathVariable(value = "id") Integer id) {

        busFieldService.delete(id);

        return getResponseBean(Result.DELETE_SUCCESS);
    }

    /**
     * @desc 业务表列表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @RequestMapping(value = "/queryBusinessList", method = RequestMethod.GET)
    @ApiOperation(value = "[BUSI - 06]业务表下拉列表", httpMethod = "GET")
    @ResponseBody
    public ResponseBean queryBusinessList() {
        List<Business> list = businessService.getAll();
        List<SelectList> selectLists = new ArrayList<>();
        for (Business b : list) {
            selectLists.add(new SelectList(b.getId() + "", b.getTdesc(), null));
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(selectLists));
    }

    /**
     * @desc 业务表列表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @RequestMapping(value = "/queryBusFieldList/{type}", method = RequestMethod.GET)
    @ApiOperation(value = "[BUSI - 07]业务表字段列表", httpMethod = "GET")
    @ResponseBody
    public ResponseBean queryBusFieldList(@PathVariable(value = "type") String type) {

        Integer id = null;
        Business bus = null;
        try {
            id = Integer.parseInt(type);
            bus = businessService.getOne(id);
        } catch (Exception e) {
            List<Business> buslist = businessService.findBy("tcode", type);
            if (buslist == null || buslist.size() == 0) {
                return getResponseBean(Result.QUERY_SUCCESS);
            }
            bus = buslist.get(0);
            id = bus.getId();
        }

        String newOrder = null;
        if (bus != null) {
            newOrder = bus.getNewOrder();
            bus.setNewOrder(null);
            businessService.save(bus);
        }

        List<BusField> list = busFieldService.executeNativeQuerySql("select * from " + SqlUtils.tableName(BusField.class) + " where bsid = ? order by orders", id);

        if (StringUtils.isNotBlank(newOrder)) {
            String[] os = newOrder.split(",");
            int orderType = Integer.parseInt(os[0]);
            int entityOrder = Integer.parseInt(os[1]);
            int oldOrder = Integer.parseInt(os[2]);
            if (entityOrder == oldOrder) {
                list.get(entityOrder - 1).setOrderDesc(" *");
            } else {
                list.get(entityOrder - 1).setOrderDesc(" (" + oldOrder + ")*");
                if (orderType == 1) {//互换
                    list.get(oldOrder - 1).setOrderDesc(" (" + entityOrder + ")");
                } else if (orderType == 0) {//插入
                    if (entityOrder > oldOrder) {//向下移动
                        for (int i = oldOrder - 1; i < entityOrder - 1; i++) {
                            list.get(i).setOrderDesc(" (" + (i + 2) + ")");
                        }
                    } else {//向上移动
                        for (int i = entityOrder; i < oldOrder; i++) {
                            list.get(i).setOrderDesc(" (" + i + ")");
                        }
                    }
                } else if (orderType == -1) {//删除
                    for (int i = entityOrder - 1; i < list.size(); i++) {
                        list.get(i).setOrderDesc(" (" + (i + 2) + ")");
                    }
                }
            }
        }

        return getResponseBean(Result.QUERY_SUCCESS.setData(JSONUtils.autoRemoveField(JSONObject.toJSON(list), true, "business")));
    }

    @ApiOperation(value = "[BUSI - 08]已创建全部业务表", httpMethod = "GET")
    @RequestMapping(value = "/queryAllEntity", method = RequestMethod.GET)
    public ResponseBean queryAllEntity() {
        List<Business> busList = businessService.getAll();

        List<SelectList> voList = new ArrayList<SelectList>();
        for (Business t1 : busList) {
            voList.add(new SelectList(t1.getId() + "", t1.getTdesc(), null));
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(voList));
    }

    @ApiOperation(value = "[BUSI - 09]通过业务表获取模板", httpMethod = "GET")
    @RequestMapping(value = "/queryAllTempByBsid/{bsid}", method = RequestMethod.GET)
    public ResponseBean queryAllTempByBsid(@PathVariable(value = "bsid") Integer bsid) {
        List<IntoTemp> list = intoTempService.findBy("business", bsid);

        List<SelectList> voList = new ArrayList<SelectList>();
        for (IntoTemp e : list) {
            voList.add(new SelectList(e.getId() + "", e.getTempName(), null));
        }

        return getResponseBean(Result.QUERY_SUCCESS.setData(voList));
    }

    @ApiOperation(value = "[BUSI - 10]寄递业务下拉列表", httpMethod = "GET")
    @RequestMapping(value = "/queryJdBusiness", method = RequestMethod.GET)
    public ResponseBean queryJdBusiness() {
        Map<String, String> map = BusiProper.getExpressMap();
        List<SelectList> voList = new ArrayList<SelectList>();
        for (String s : map.keySet()) {
            voList.add(new SelectList(s, map.get(s), null));
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(voList));
    }

    /**
     * @desc 模板管理列表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @ApiOperation(value = "[TEMP - 01]模板列表全部", httpMethod = "GET")
    @RequestMapping(value = "/queryManageTemplate", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    public ResponseBean queryManageTemplate(Pageable pageable, IntoTemp entity) {
        Page<IntoTemp> page = intoTempService.findPageByEntity(pageable, entity);

        return getResponseBean(Result.QUERY_SUCCESS.setData(page));
    }

    /**
     * @desc 模板新增/修改
     * @author 杨勇
     * @Date 2019-01-03
     */
    @PostMapping(value = "/saveTemplate")
    @ApiOperation(value = "[TEMP - 02]模板新增/修改", httpMethod = "POST")
    @ResponseBody
    public ResponseBean saveTemplate(@RequestBody IntoTemp entity) {

        if (entity.getBusiness() == null || entity.getBusiness().getId() == null) {
            return getResponseBean(Result.SAVE_FAIL);
        }
        Business bus = businessService.getOne(entity.getBusiness().getId());

        if (bus == null || bus.getId() == null) {
            return getResponseBean(Result.SAVE_FAIL);
        }

        entity.setBusiness(bus);

        intoTempService.save(entity);

        return getResponseBean(Result.SAVE_SUCCESS.setData(entity));
    }


    /**
     * @desc 模板详情
     * @author 杨勇
     * @Date 2019-01-03
     */
    @ApiOperation(value = "[TEMP - 03]模板详情", httpMethod = "GET")
    @RequestMapping(value = "/templateList/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "模板ID", paramType = "path", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean templateList(@PathVariable(value = "id") Integer id) {
        IntoTemp entity = intoTempService.getIntoTemp(id);

        return getResponseBean(Result.QUERY_SUCCESS.setData(JSONUtils.autoRemoveField(JSONObject.toJSON(entity), true)));
    }

    /**
     * @desc 已有模板列表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @ApiOperation(value = "[TEMP - 04]已有模板列表_业务表对应模板", httpMethod = "GET")
    @RequestMapping(value = "/manageTemplatesList/{type}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "模板类型", paramType = "path", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean manageTemplatesList(@PathVariable(value = "type") String type) {
        type = type.toLowerCase();
        List<Business> buslist = businessService.findBy("tcode", type);
        Business entity = null;
        if (buslist == null || buslist.size() == 0) {
            //查询业务表,如果不存在,进行业务表初始新增
            entity = businessService.autoSaveByName(type, request);
            if (entity == null) {
                return getResponseBean(Result.QUERY_SUCCESS);
            }
        } else {
            entity = buslist.get(0);
        }
        List<IntoTemp> list = intoTempService.findBy("business", entity);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bsId", entity.getId());
        map.put("list", list);

        return getResponseBean(Result.QUERY_SUCCESS.setData(map));
    }

    /**
     * @desc 已有模板列表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @ApiOperation(value = "[TEMP - 05]文件导入", httpMethod = "POST")
    @RequestMapping(value = "/importFile/{id}/{startLine}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean importFile(@PathVariable(value = "id") Integer id, @PathVariable(value = "startLine") Integer startLine, String encoding, @RequestParam("file") MultipartFile file) {
        IntoTemp entity = intoTempService.getIntoTemp(id);

        File temp = MD5FileUtil.tempSave(file);

        if (startLine == null || startLine < 1) {
            startLine = 1;
        }

        IntoRecord ir = new IntoRecord();
        ir.setFileName(file.getOriginalFilename());
        ir.setFileSize(file.getSize());
        ir.setStatus(0);
        ir.setStartNum(startLine);
        ir.setBusiness(entity.getBusiness());
        ir.setIntoTemp(entity);

        String md5 = MD5FileUtil.getFileMD5String(temp);
        String sql = "select * from F_INTORECORD where md5 ='" + md5 + "' and itid = " + entity.getId() + "";
        List<IntoRecord> list = intoRecordService.executeNativeQuerySql(sql, new Object[]{});
        //;List<IntoRecord> list = intoRecordService.findBy("md5",md5);
        if (list.size() > 0) {
            for (IntoRecord i : list) {
                if (i.getStatus() == 1 && i.getSuccessNum() > 0 && i.getBusiness().getId().equals(entity.getBusiness().getId())) {//导入成功&成功数量&同一业务ID
                    ir.setStatus(3);
                    break;
                }
            }
        }

        ir.setMd5(md5);
        intoRecordService.save(ir);
        if (ir.getStatus() == 3) {//重复
            return getResponseBean(Result.QUERY_SUCCESS.setData(ir));
        }

        UserInfoVo user = UserUtil.getCurrentUser(request);
        //文件临时存储 导入处理
        intoTempService.importFile(entity, temp, encoding, startLine, ir, user);
        //临时文件删除
        temp.delete();
        intoRecordService.save(ir);

        return getResponseBean(Result.QUERY_SUCCESS.setData(ir));
    }


    /**
     * @desc 已有模板列表
     * @author 杨勇
     * @Date 2019-01-03
     */
    @ApiOperation(value = "[TEMP - 06]导入记录", httpMethod = "POST")
    @RequestMapping(value = "/importRecode/{bsid}/{itid}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bsid", required = true, value = "业务表ID,1为全部", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "itid", required = true, value = "模板ID,1为全部", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "date", value = "查询日期,格式yyyy-MM-dd", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean importRecode(Pageable pageable, @PathVariable(value = "bsid") Integer bsid, String date, @PathVariable(value = "itid") Integer itid) {
        if (bsid != null && bsid <= 1) {
            bsid = null;
            itid = null;
        }
        if (itid != null && itid <= 1) {
            itid = null;
        }

        //日期查询
        Date begin = null;
        Date end = null;
        try {
            begin = dataFormat_yyyy_MM_dd.parse(date);
            end = new Date(begin.getTime() + 24 * 60 * 60 * 1000);
        } catch (Exception e) {
            begin = null;
            end = null;
        }

        List<Object> param = new ArrayList<Object>();

        StringBuilder sql = new StringBuilder("select * from " + intoRecordService.tableName() + " where 1=1");
        if (itid != null) {
            sql.append(" and itid = ?");
            param.add(itid);
        } else if (bsid != null) {
            sql.append(" and bsid = ?");
            param.add(bsid);
        }
        if (begin != null) {
            sql.append(" and createTime >= ? and createTime < ?");
            param.add(begin);
            param.add(end);
        }

        String delSql = MessageFormat.format("delete {0} t where t.itid not in (select id from {1})", intoRecordService.tableName(), intoTempService.tableName());
        intoRecordService.executeUpdateSql(delSql);

        Page<IntoRecord> page = intoRecordService.executeNativeQuerySqlForPage(pageable, sql.toString(), param.toArray());
        return getResponseBean(Result.QUERY_SUCCESS.setData(page));
    }

    /**
     * @desc 模版删除
     * @author 杨勇
     * @Date 2019-01-16
     */
    @RequestMapping(value = "/deleteTemplate/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "[TEMP - 06]模板删除,同时取消和导入记录的关联", httpMethod = "DELETE")
    @ResponseBody
    public ResponseBean deleteTemplate(@PathVariable(value = "id") Integer id) {
        intoTempService.deleteTemplate(id);
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    /**
     * @desc 导入统计
     * @author 杨勇
     * @Date 2019-01-10
     */
    @ApiOperation(value = "[STATIC - 01]导入量,最近两周", httpMethod = "POST")
    @RequestMapping(value = "/importStatistic", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean importStatistic() {
        int day = 14;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, 1 - day);
        java.sql.Date start = new java.sql.Date(c.getTimeInMillis());

        List<Object[]> list = new ArrayList<Object[]>();

        Map<String, Object[]> map = new HashMap<String, Object[]>();
        while (list.size() < day) {
            Object[] os = new Object[]{dataFormat_yyyy_MM_dd.format(c.getTime()), 0};
            list.add(os);
            map.put(os[0].toString(), os);
            c.add(Calendar.DATE, 1);
        }

//      String sql = "select count(*),sum(e.allnum),sum(e.successnum),sum(e.errornum),e.bsid,t.tdesc,e.da from (select allnum,successnum,errornum,to_char(createtime,'yyyy-MM-dd')  da,bsid from f_intorecord) e left join f_business t on e.bsid = t.id group by e.da,e.bsid,t.tdesc";
        String sql = "select count(*),sum(e.allnum),sum(e.successnum),sum(e.errornum),e.da from (select allnum,successnum,errornum,to_char(createtime,'yyyy-MM-dd') da,createtime,bsid from f_intorecord where createtime > ?) e left join f_business t on e.bsid = t.id group by e.da";

        List<Object[]> list1 = intoTempService.querySqlArr(sql,new Object[]{start.getTime()});
        for (Object[] os : list1) {
            Object[] oo = map.get(os[4]);
            if (oo != null) {
                oo[1] = os[2];
            }
        }

        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }


    /**
     * @desc 流程新增/修改
     * @author fanhua
     */
    @RequestMapping(value = "/codeFormatList", method = RequestMethod.GET)
    @ApiOperation(value = "编码格式", httpMethod = "GET")
    @ResponseBody
    public ResponseBean codeFormatList() {
        List<SelectList> selectLists = new ArrayList<>();
        selectLists.add(new SelectList("UTF-8"));
        selectLists.add(new SelectList("GBK"));
        selectLists.add(new SelectList("GB2312"));
        return getResponseBean(Result.QUERY_SUCCESS.setData(selectLists));
    }

}
