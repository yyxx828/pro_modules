package com.xajiusuo.busi.flow.service.impl;

import com.xajiusuo.busi.flow.conf.FlowBusiConf;
import com.xajiusuo.busi.flow.dao.FlowAllotDao;
import com.xajiusuo.busi.flow.entity.FlowAllot;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.service.FlowAllotService;
import com.xajiusuo.busi.flow.service.FlowNodeService;
import com.xajiusuo.busi.out.controller.OutFlowController;
import com.xajiusuo.busi.out.service.RemoteService;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.CfI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by 杨勇  on 2018/5/25.
 */
@Service
public class FlowAllotServiceImpl extends BaseServiceImpl<FlowAllot, Integer> implements FlowAllotService {

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private FlowNodeService flowNodeRepository;

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    FlowAllotDao entityRepository;

    @Autowired
    private OutFlowController outFlowController;

    @Override
    public BaseDao<FlowAllot, Integer> getBaseDao() {
        return entityRepository;
    }


    @Override
    public Page<FlowAllot> findPageByEntity(Pageable pageable, FlowAllot entity) {
        String sql = BeanUtils.beanToSql(entity);
        sql += " order by " + pageable.getSort().toString().replaceAll(":", "");
        return entityRepository.executeQuerySqlByPage(pageable,sql);
    }

    @Override
    public Page<FlowAllot> queryFlowAllotPage(Pageable page, FlowAllot entity) {

        String sql = MessageFormat.format( "select * from {0} e where 1=1~ and e.busi = ?~~ and e.flowType = ?~~ and e.did = ?~~ and e.pid = ?~~ and e.userId = ?~",SqlUtils.tableName(FlowAllot.class));
        List<Object> param = new ArrayList<Object>();
        param.add(entity.getBusi());//业务
        param.add(entity.getFlowType());//类型
        param.add(entity.getDid());//部门
        param.add(entity.getPid());//职务
        param.add(entity.getUserId());//人员
        if(entity.getFlowType() == null){
            if(entity.getUserId() != null){//查询人员时候部门+职务无效
                param.set(2,null);
                param.set(3,null);
            }
        }else{
            //人员
            switch (entity.getFlowType()){
                case 0://指定人 部门+职务转至查询人员
                    if(entity.getUserId() != null){//当查询人员id为空查询对应部门+角色
                        param.set(2,null);//去掉部门
                        param.set(3,null);//去掉人员
                    }
                    break;
                case 1://指定部门+职务
                    param.set(4,null);//去掉人员
                    break;
                case 2://指定职务
                    param.set(2,null);//去掉部门
                    param.set(4,null);//去掉人员
                    break;
                case 9://默认
                    param.set(2,null);//去掉部门
                    param.set(3,null);//去掉职务
                    param.set(4,null);//去掉人员
                    break;
            }
        }

        StringBuilder sb = new StringBuilder();
        Object[] params = SqlUtils.convertHSql(sql, sb, param.toArray());

        Page<FlowAllot> pager = entityRepository.executeQuerySqlByPage(page, sb.toString(), params);
        return pager;
    }

    @Override
    public Result findSameAllot(FlowAllot entity) {
        StringBuilder sql = new StringBuilder(MessageFormat.format( "select * from {0} e where 1=1~ and e.busi = ?~~ and e.flowType = ?~~ and e.did = ?~~ and e.pid = ?~~ and e.userId = ?~~ and e.condition = ?~~ and e.id <> ?~", SqlUtils.tableName(FlowAllot.class)));
        if (StringUtils.isBlank(entity.getCondition())) {
            sql.append(" and e.condition is null");
        }

        //业务判断
        if(StringUtils.isBlank(entity.getBusi())){
            if(entity.getFlowType() == 9){
                sql.append(" and e.busi is null");
            }else{
                return Result.find(CfI.R_FLOW_BUSINOTNULL_FAIL);
            }
        }else{
            if(!FlowBusiConf.getBusis().contains(entity.getBusi())){
                return Result.find(CfI.R_FLOW_BUSINOEXIST_FAIL);
            }
        }

        List<Object> param = new ArrayList<Object>();
        param.add(entity.getBusi());
        param.add(entity.getFlowType());
        param.add(entity.getDid());//2 部门
        param.add(entity.getPid());//3 职务
        param.add(entity.getUserId());//4 人员
        param.add(entity.getCondition());//5 额外条件
        param.add(entity.getId());//6 ID

        if (entity.getFlowType() == 0) {//个人
            if (entity.getUserId() == null) {
                return Result.find(CfI.R_FLOW_USERNOTNULL_FAIL);
            }
            param.set(2,null);entity.setDid(null);entity.setDepName(null);
            param.set(3,null);entity.setPid(null);entity.setDutyName(null);
        } else if (entity.getFlowType() == 1) {//部门+角色
            if (entity.getDid() == null) {
                return Result.find(CfI.R_FLOW_DUTYNOTNULL_FAIL);
            }
            if (entity.getPid() == null) {
                return Result.find(CfI.R_FLOW_DUTYNOTNULL_FAIL);
            }
            param.set(4,null);entity.setUserId(null);entity.setUserName(null);
        } else if (entity.getFlowType() == 2) {//角色
            if (entity.getPid() == null) {
                return Result.find(CfI.R_FLOW_DUTYNOTNULL_FAIL);
            }
            param.set(2,null);entity.setDid(null);entity.setDepName(null);
            param.set(4,null);entity.setUserId(null);entity.setUserName(null);
        } else if (entity.getFlowType() == 9) {//全局不加任意条件
            param.set(2,null);entity.setDid(null);entity.setDepName(null);
            param.set(3,null);entity.setPid(null);entity.setDutyName(null);
            param.set(4,null);entity.setUserId(null);entity.setUserName(null);
            param.set(5,null);entity.setCondition(null);
        }
        if (entity.getFlowDefId() == null) {
            return Result.find(CfI.R_FLOW_FLOWNOTEXIST_FAIL);
        }

        StringBuilder sb = new StringBuilder();
        Object[] params = SqlUtils.convertHSql(sql.toString(), sb, param.toArray());

        List<FlowAllot> list = executeNativeQuerySql(sb.toString(), params);
        if (list.size() > 0) {
            return Result.find(CfI.R_FLOW_FLOWALLOTEXIST_FAIL);
        }

        //流程判断
        List<FlowNode> list1 = flowNodeRepository.executeNativeQuerySql(MessageFormat.format( "select * from {0} e where fdId = ? order by orders",SqlUtils.tableName(FlowNode.class)), entity.getFlowDefId());
        if(list1.size() > 0){
            return null;
        }
        return Result.find(CfI.R_FLOW_FLOWNOTEXIST_FAIL);
    }

    @Override
    public Result allotName(FlowAllot entity) {

        for(Map<String,String> map: FlowBusiConf.getBusiMap()){
            if(map.get("value").equals(entity.getBusi())){
                entity.setBusiName(map.get("name"));
                break;
            }
        }

        Object o;
        if(entity.getFlowType() == 0){
            Userinfo user = userinfoService.getUserInfoById(entity.getUserId());
            if(user == null || user.getId() == null){
                return Result.find(CfI.R_FLOW_USERNOTNULL_FAIL);
            }
            entity.setUserName(user.getFullname());
            entity.setDid(user.getDepartId());
            entity.setDepName(user.getDepartName());
            entity.setPid(user.getDutyId());
            entity.setDutyName(user.getDutyName());
        }
        return null;
    }


    @Override
    public Map<String, Object> flowInfoMatch(FlowBusiConf conf, Integer uid, String condition) {
        String sqlCon = " and e.condition is null";
        if(StringUtils.isNotBlank(condition)){
            sqlCon = " and e.condition = " + condition;
        }

        String sql = null;
        //找个人
        sql = MessageFormat.format( "select * from {0} e where runs = 1 and e.flowType = ? and e.busi = ? and e.userId = ?{1}",tableName(),sqlCon);
        List<FlowAllot> list = executeNativeQuerySql(sql, 0,conf.getBusi(),uid);
        if(list.size() == 0){
            Userinfo user = userinfoService.getUserInfoById(uid);
            if(user != null && user.getId() != null){
                //部门+职务
                Integer did = user.getDepartId();;
                Integer pid = user.getDutyId();
                sql = MessageFormat.format("select * from {0} e where runs = 1 and e.flowType = ? and e.busi = ? and e.did = ? and e.pid = ?{1}" ,tableName(), sqlCon);
                list = executeNativeQuerySql(sql, 1,conf.getBusi(),did,pid);
                if(list == null || list.size() == 0){
                    sql = MessageFormat.format( "select * from {0} e where runs = 1 and e.flowType = ? and e.busi = ? and e.pid = ?{1}", tableName(),sqlCon);
                    list = executeNativeQuerySql(sql, 2,conf.getBusi(),pid);
                }
                if(list == null || list.size() == 0){
                    sql = MessageFormat.format( "select * from {0} e where runs = 1 and e.flowType = ? and e.busi = ?", tableName());
                    list = executeNativeQuerySql(sql, 9,conf.getBusi());
                }
                if(list == null || list.size() == 0){
                    sql = MessageFormat.format("select * from {0} e where runs = 1 and e.flowType = ? and e.busi is null", tableName());
                    list = executeNativeQuerySql(sql, 9);
                }
            }
        }

        Map<String,Object> map = Collections.emptyMap();

        if(list != null && list.size() > 0){
            map = new HashMap<String,Object>();
            int fdid = list.get(0).getFlowDefId();
            map.put("flowDefId",fdid);
            map.put("flowIns",outFlowController.flowBusiStatusPrepared(fdid,null,uid).getData());
        }
        return map;
    }

    @Override
    public void batchSaveOrUpdate(List<FlowAllot> entitys) {
        entityRepository.batchSaveOrUpdate(entitys);
    }


}
