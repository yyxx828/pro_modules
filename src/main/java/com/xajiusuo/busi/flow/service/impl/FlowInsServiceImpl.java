package com.xajiusuo.busi.flow.service.impl;

import com.xajiusuo.busi.flow.dao.FlowInsDao;
import com.xajiusuo.busi.flow.dao.FlowNodeDao;
import com.xajiusuo.busi.flow.dao.SpInfoDao;
import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.entity.SpInfo;
import com.xajiusuo.busi.flow.service.FlowInsService;
import com.xajiusuo.busi.user.dao.UserinfoDao;
import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.BaseEntityUtil;
import com.xajiusuo.utils.CfI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hadoop on 18-1-22.
 */
@Slf4j
@Service
public class FlowInsServiceImpl extends BaseServiceImpl<FlowIns, Integer> implements FlowInsService {
    @Autowired
    private FlowInsDao flowInsDao;

    @Autowired
    private FlowNodeDao flowNodeDao;

    @Autowired
    private UserinfoDao userinfoDao;

    @Autowired
    private SpInfoDao spInfoDao;

    @Override
    public BaseDao<FlowIns, Integer> getBaseDao() {
        return flowInsDao;
    }

    @Override
    public FlowIns saveFlow(FlowDef def, String bsid, Integer uid, String sysId, String sysName) {
        String sql = MessageFormat.format( "select * from {0} where busiId = ? and sysId = ?", SqlUtils.tableName(FlowIns.class));

        List<FlowIns> tempList = flowInsDao.executeNativeQuerySql(sql, bsid,sysId);
        if(tempList.size() >= 1){
            return tempList.get(0);
        }

        FlowIns ins = new FlowIns(def,bsid);
        FlowNode fn = ins.getFlowNode();
        FlowNode node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),1);
        if(node != null && node.getSpType() == null && node.getOrders() != 1){
            node = null;
        }
        BaseEntityUtil.addEntity(ins,uid);
        ins.setStatus(0);
        ins.statusing();
        ins.setFlowNode(node);
        ins.setSysId(sysId);
        ins.setSysName(sysName);
        setInsUsers(ins);//流程保存
        flowInsDao.save(ins);
        saveSpInfo(ins,"流程保存",uid, SpInfo.save,fn);
        return ins;
    }

    @Override
    public void setInsUsers(FlowIns ins){
        //获取下一级所有审批人员信息
        FlowNode node = ins.getFlowNode();
        if(node != null && node.getOrders() == 1){
            node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),2);
            if(node != null && node.getSpType() == null && node.getOrders() != 1){
                node = null;
            }
        }
        ins.setNextSpUser(getUserByNode(node,ins.getCreateUID()));

        //获取当前审批人员信息
        if(StringUtils.isNotBlank(ins.getCurSpUids())){
            List<Userinfo> list = new ArrayList<Userinfo>();
            for(String id:ins.getCurSpUids().split(",")){
                list.add(userinfoDao.getOne(Integer.parseInt(id)));
            }
            ins.setCurSpUser(list);
        }else{
            List<Userinfo> list = getUserByNode(ins.getFlowNode(),ins.getCreateUID());
            //指定审核人过滤
            if(!StringUtils.isEmpty(ins.getUids())){
                String uids = "," + ins.getUids() + ",";
                for(int i = list.size() - 1 ; i>= 0 ;i--){
                    if(!uids.contains("," + list.get(i).getId() + ",")){
                        list.remove(i);
                    }
                }
            }
            //审核人占位过滤
            if(ins.getHostId() != null){
                for(int i = list.size() - 1 ; i>= 0 ;i--){
                    if(!list.get(i).getId().equals(ins.getHostId())){
                        list.remove(i);
                    }
                }
            }
            ins.setCurSpUser(list);
        }
        //设置flowNode为下一级节点
        if(ins.getFlowNode() != null){
            node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),ins.getFlowNode().getOrders() + 1);
            ins.setNextNode(node);
        }else{
            ins.setNextNode(null);
        }
    }

    private List<Userinfo> getUserByNode(FlowNode node,Integer uid){
        //0指定人 -1指定机构+职位 1本机构+职位 2上级机构+职位 3/4/5...依次往上机构+职位 99最高机构+职位
        List<Userinfo> list = new ArrayList<Userinfo>(0);
        if(node == null || node.getSpType() == null){
        }else if(node.getSpType() == 0){
            for(String id:node.getUserIds().split(",")){
                Userinfo u = userinfoDao.getOne(Integer.parseInt(id));
                if(u != null){
                    list.add(u);
                }
            }
        }else if(node.getSpType() == -1){
            for(String id:node.getRoleIds().split(",")){
                list.addAll(userinfoDao.findByDepartAndDutyAndDelFlagFalse(new Depart(node.getDepId()),new Duty(Integer.parseInt(id))));
            }
        }else{
            Userinfo user = userinfoDao.getOne(uid);
            int level = node.getSpType();
            Depart dep = user.getDepart();
            while(level-- > 1 && dep.getParent() != null && dep.getParent().getDlevel() != 0){
                dep = dep.getParent();
            }
            for(String id:node.getRoleIds().split(",")){
                list.addAll(userinfoDao.findByDepartAndDutyAndDelFlagFalse(dep,new Duty(Integer.parseInt(id))));
            }
        }
        return list;
    }

    private void saveSpInfo(FlowIns ins, String spInfo, Integer uid, Integer spType, FlowNode fn){
        SpInfo info = new SpInfo();
        info.setFlowIns(ins);
        info.setSpInfo(spInfo);
        info.setCreateUID(uid);
        info.setSpType(spType);
        info.setCurNode(fn);
        spInfoDao.save(info);
    }

    @Override
    public Page<FlowIns> findByFlowDef(FlowDef flowDef, Pageable pageable) {
        return flowInsDao.findByFlowDef(flowDef, pageable);
    }

    @Override
    public Page<FlowIns> findAll(Pageable pageable) {
        return flowInsDao.findAll(pageable);
    }

    @Override
    public int submitFlow(FlowIns ins, Integer uid,String uids) {
        int b = -1;
        if(ins.getCreateUID().equals(uid) && ins.getFlowNode().getOrders() == 1){//如果是本人提交,并且在第一步则可提交
            FlowNode node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),2);
            if(node != null && node.getSpType() == null && node.getOrders() != 1){
                node = null;
            }
            ins.setFlowNode(node);
            setInsUsers(ins);//流程提交
            ins.setCurSpUids();
            if(!StringUtils.isEmpty(uids)){
                String $uids = "," + ins.getCurSpUids() + ",";
                boolean isNotInUser = false;
                for(String useId:uids.split(",")){
                    if(!$uids.contains("," + useId + ",")){
                        isNotInUser = true;
                        break;
                    }
                }
                if(isNotInUser){
                    return -1;
                }
                ins.setCurSpUids(uids);
            }
            if(node != null){//提交如果有下一个节点,则流程进入第二节点
                ins.setStatus(2);
            }else{//如果提交后无节点,则流程通过
                ins.setStatus(9);
            }
            ins.statusing();
            flowInsDao.saveAndFlush(ins);
            saveSpInfo(ins,"发起人提交",uid,SpInfo.submit,null);
            b = 1;
        }
        return b;
    }

    @Override
    public Result checkAccess(FlowIns ins, Userinfo user) {
        if(StringUtils.isNotBlank(ins.getCurSpUids())){
            if(("," + ins.getCurSpUids() + ",").contains("," + user.getId() + ",")){
                return null;
            }else{
                return Result.find(CfI.R_FLOW_NOACCESS_FAIL);
            }
        }
        if(user == null){//此用户已经被禁用
            return Result.find(CfI.R_USER_DISABLE_FAIL);
        }
        if(ins == null){//流程已删除或不存在
            return Result.find(CfI.R_FLOW_NOTFOUND_FAIL);
        }
        if((ins.getHostId() != null && !ins.getHostId().equals(user.getId()))){//没有审批权限,流程占位
            return Result.find(CfI.R_FLOW_NOACCESS_FAIL);

        }
        if(ins.getUids() != null && ("," + ins.getUids() + ",").contains("," + user.getId() + ",")){//没有审批权限
            return Result.find(CfI.R_FLOW_NOACCESS_FAIL);
        }
        FlowNode node = ins.getFlowNode();
        if(node == null){//该流程审批已完成
            return Result.find(CfI.R_FLOW_NOUSEACCESS_FAIL);
        }
        if(node.getOrders() == 1){//该流程审批已完成
            return Result.find(CfI.R_FLOW_NOTSUBMIT_FAIL);
        }
        //0指定人 -1指定机构+职位 1本机构+职位 2上级机构+职位 3/4/5...依次往上机构+职位 99最高机构+职位
        if(node.getSpType() == -1){
            if(node.getDepId() == null){//指定机构为空,请联系管理员
                return Result.find(CfI.R_FLOW_FLOWNODENOTDEP_FAIL);
            }
            if(user.getDepartId() == null){//用户机构为空,请联系管理员
                return Result.find(CfI.R_FLOW_FLOWNODEUSERNOTDEP_FAIL);
            }
            if(!node.getDepId().equals(user.getDepart().getId())){//没有审批权限
                return Result.find(CfI.R_FLOW_NOACCESS_FAIL);
            }
            if(!("," + node.getRoleIds() + ",").contains("," + user.getDutyId() + ",")){
                return Result.find(CfI.R_FLOW_NOACCESS_FAIL);
            }
        }else if(node.getSpType() == 0){//指定人
            if(node.getUserIds() == null){//指定审批人为空,请联系管理员
                return Result.find(CfI.R_FLOW_FLOWNODENOTUSER_FAIL);
            }
            if(!("," + node.getUserIds() + ",").contains("," + user.getId() + ",")){//没有审批权限
                return Result.find(CfI.R_FLOW_NOACCESS_FAIL);
            }
        }else{//按级别往上查找
            Userinfo u = userinfoDao.getOne(ins.getCreateUID());
            if(user.getDepartId() == null){//用户机构为空,请联系管理员
                return Result.find(CfI.R_FLOW_FLOWNODEUSERNOTDEP_FAIL);
            }
            if(user.getDuty() == null){
                return Result.find(CfI.R_FLOW_FLOWNODEUSERNOTDUTY_FAIL);
            }
            if(StringUtils.isEmpty(node.getRoleIds())){//未指定审批职位,请联系管理员
                return Result.find(CfI.R_FLOW_FLOWNOTDUTYS_FAIL);
            }

            int level = node.getSpType();
            Depart d = u.getDepart();
            while(level-- > 1 && (d.getParent() != null && d.getParent().getDlevel() != 0)){
                d = d.getParent();
            }
            if(node.getSpType() > 1 && u.getDepart() == d){//上级机构不存在,请联系管理员
                return Result.find(CfI.R_FLOW_FLOWNOTSAMEDEP_FAIL);
            }
            if(!user.getDepartId().equals(d.getId()) || !("," + node.getRoleIds() + ",").contains("," + user.getDutyId() + ",")){
                return Result.find(CfI.R_FLOW_NOACCESS_FAIL);
            }
        }
        return null;
    }

    @Override
    public FlowNode flowReject(FlowIns ins, String spInfo, Integer uid) {
        List<FlowNode> list = flowNodeDao.findBy("flowDef", ins.getFlowDef(), new Sort("orders"));
        FlowNode node = list.get(0);
        FlowNode fn = ins.getFlowNode();
        for (int i = ins.getFlowNode().getOrders() - 2; i >= 0; i--) {
            FlowNode n = list.get(i);
            if (n.getBackDot() != null && n.getBackDot()) {
                node = n;break;
            }
        }
        if(node.getOrders() > 1){
            ins.setStatus(3);
        }else if(node.getOrders() == 1){
            ins.setStatus(1);
        }
        ins.setFlowNode(node);//起点节点
        ins.setStatusStr(node == null ? null : node.getNbName());
        ins.setHostId(null);//拒绝
        ins.statusing();
        ins.setCurSpUids(null);
        ins.setUids(null);
        setInsUsers(ins);//流程驳回
        flowInsDao.saveAndFlush(ins);
        saveSpInfo(ins, spInfo, uid, SpInfo.reject, fn);
        return node;
    }

    @Override
    public FlowNode flowAgree(FlowIns ins, String spInfo, Integer uid, String nextUids) {
        FlowNode node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(), ins.getFlowNode().getOrders() + 1);
        if(node != null && node.getSpType() == null && node.getOrders() != 1){
            node = null;
        }
        FlowNode fn = ins.getFlowNode();
        ins.setStatus(3);
        ins.setFlowNode(node);
        ins.setStatusStr(node == null ? null : node.getNbName());
        ins.setHostId(null);//同意
        ins.setUids(nextUids);
        ins.setCurSpUids(null);
        setInsUsers(ins);//流程同意
        if (node == null) {
            ins.setStatus(99);
        }
        ins.statusing();
        flowInsDao.saveAndFlush(ins);
        saveSpInfo(ins, spInfo, uid, SpInfo.agree,fn);
        return node;
    }

    @Override
    public FlowNode flowAdopt(FlowIns ins, String spInfo, Integer uid) {
        FlowNode fn = ins.getFlowNode();

        ins.setFlowNode(null);
        ins.setStatusStr(null);
        ins.setHostId(null);//直接通过
        ins.setCurSpUids(null);
        setInsUsers(ins);//直接通过
        ins.setStatus(99);
        ins.statusing();
        flowInsDao.saveAndFlush(ins);
        saveSpInfo(ins, spInfo, uid, SpInfo.adopt,fn);
        return null;
    }

    @Override
    public FlowNode flowRefuse(FlowIns ins, String spInfo, Integer uid) {
        FlowNode fn = ins.getFlowNode();
        ins.setFlowNode(null);
        ins.setStatus(-1);
        ins.setHostId(null);//不同意
        ins.setUids(null);
        ins.statusing();
        ins.setCurSpUids(null);
        setInsUsers(ins);//流程不同意结束
        flowInsDao.saveAndFlush(ins);
        saveSpInfo(ins,spInfo,uid,SpInfo.refuse,fn);
        return null;
    }

    @Override
    public Result flowFillerRecall(FlowIns ins, Userinfo user) {
        if(ins.getStatus() == 9 || ins.getStatus() == -1 || ins.getFlowNode() == null){//流程已结束
            return Result.find(CfI.R_FLOW_NOUSEACCESS_FAIL);
        }
        if(ins.getStatus() == 0){//流程未提交
            return Result.find(CfI.R_FLOW_NOTSUBMIT_FAIL);
        }
        if(!ins.getCreateUID().equals(user.getId())){//不是提交人
            return Result.find(CfI.R_FLOW_FLOWNOTFILLER_FAIL);
        }
        if(ins.getFlowNode().getUserBack() != null && !ins.getFlowNode().getUserBack()){//提交者是否有撤回权限
            return Result.find(CfI.R_FLOW_FLOWFILLERNOBACK_FAIL);
        }
        FlowNode fn = ins.getFlowNode();
        FlowNode node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),1);
        if(node != null && node.getSpType() == null && node.getOrders() != 1){
            node = null;
        }
        ins.setFlowNode(node);
        ins.setStatus(0);
        ins.setStatusStr(node == null ? null : node.getNbName());
        setInsUsers(ins);//提交者撤回起点
        ins.statusing();
        ins.setUids(null);
        ins.setCurSpUids(null);
        flowInsDao.saveAndFlush(ins);
        saveSpInfo(ins,"手动撤回",user.getId(),SpInfo.recall,fn);
        return null;
    }

    public Result flowAutoBack(FlowIns ins, boolean begin){
        if(ins.getStatus() == 9 || ins.getStatus() == -1 || ins.getFlowNode() == null){//流程已结束
            return Result.find(CfI.R_FLOW_NOUSEACCESS_FAIL);
        }

        if(ins.getStatus() != 3 && ins.getStatus() != 2 && ins.getFlowNode().getOrders() != 1){//该流程未提交
            return Result.find(CfI.R_FLOW_NOTSUBMIT_FAIL);
        }

        FlowNode node = null;

        if(begin){//起点
            node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),1);
        }else{
            node = flowNodeDao.findByFlowDefAndOrders(ins.getFlowDef(),ins.getFlowNode().getOrders() - 1);
        }

        ins.setFlowNode(node);
        node.setFlowDef(ins.getFlowDef());
        if(node.getOrders() == 1){
            ins.setStatus(0);
        }
        ins.setCurSpUids(null);//超时退回
        ins.setUids(null);//超时退回
        setInsUsers(ins);//超时撤回
        ins.statusing();
        if(node.getOrders() == 1){
            ins.setStatusStr("超时退回");
        }

        String sql = MessageFormat.format( "select * from {0} e where fnid = ? order by lastModifyUID desc", SqlUtils.tableName(SpInfo.class));
        List<SpInfo> sps = spInfoDao.executeNativeQuerySql(sql, node.getId());
        //通过审批记录获取上一次审批人并且记录
        ins.setHostId(null);//超时退回
        for(SpInfo si:sps){
            if(si.getCurNode() != null && si.getCurNode().getOrders() != 1){
                ins.setHostId(si.getCreateUID());//超时退回上一步
            }
            break;
        }
        flowInsDao.saveAndFlush(ins);
        saveSpInfo(ins,"超时退回",ins.getCreateUID(),SpInfo.recall,node);
        return null;
    }

    @Override
    public ModelMap getFlowNodesForMap(Integer id) {
        ModelMap modelMap = new ModelMap();
        FlowIns flowIns = flowInsDao.getOne(id);
        FlowDef flowDef = flowIns.getFlowDef();
        List<FlowNode> flowNodes = flowNodeDao.findBy("flowDef", flowDef ,new Sort("orders"));
        modelMap.put("data", flowNodes);
        if(flowIns.getFlowNode() != null){
            modelMap.put("currentNode", flowIns.getFlowNode());
        }else{
            modelMap.put("currentNode", flowNodes.get(flowNodes.size() - 1));
        }
        return modelMap;
    }

    @Override
    public List<SpInfo> getSpHistoryList(Integer id) {
        FlowIns flowIns = flowInsDao.getOne(id);
        List<SpInfo> spInfoList = spInfoDao.findBy("flowIns", flowIns, new Sort(Sort.Direction.DESC, "createTime"));
        return spInfoList;
    }

    @Override
    public Page<FlowIns> query(Pageable pageable, String fname){
        StringBuffer hql ;
        if (StringUtils.isEmpty(fname)) {
            hql = new StringBuffer(MessageFormat.format( "select e.* from {0} e where 1 = 1 ",tableName()));
        } else {
            hql = new StringBuffer(MessageFormat.format( "select e.*,d.fname from {0} e join {1} d on e.fdid=d.id where 1 = 1 ",tableName(), SqlUtils.tableName(FlowDef.class)));
            hql.append(" and d.fname like " + like(fname));
        }
        if (pageable.getSort() == null) {
            hql.append(" order by e.lastModifyUID DESC");
        } else {
            hql.append(" order by ");
            Iterator<Sort.Order> it = pageable.getSort().iterator();
            while (it.hasNext()) {
                Sort.Order o = it.next();
                hql.append("e." + o.getProperty());
                if (o.getDirection() != null) {
                    hql.append(o.getDirection().isAscending() ? " ASC" : " DESC");
                }
                hql.append(",");
            }
            hql.deleteCharAt(hql.length() - 1);
        }
        return flowInsDao.executeQuerySqlByPage(pageable, hql.toString());
    }

    public static String like(String s) {
        return s != null && s.trim().length() >= 1 ? "'" + SqlUtils.sqlLike(s) + "'" : null;
    }

    @Override
    public List<FlowIns> getFlowInsByUser(Integer uid,String sysId) {
        if(StringUtils.isNotBlank(sysId)){
            sysId = " and e.sysId = '" + sysId + "'";
        }else{
            sysId = "";
        }
        String hql = MessageFormat.format( "select e.* from {0} e where (curSpUids like ? or curSpUids like ? or curSpUids = ? or curSpUids like ?) {1}", SqlUtils.tableName(FlowIns.class),sysId);
        List<FlowIns> list = flowInsDao.executeNativeQuerySql(hql, "%," + uid,uid + ",%",uid + "","%," + uid + ",%");
        return list;
    }



    @Override
    public List<Integer> getFlowInsIdByUser(Integer uid,String sysId) {
        if(StringUtils.isNotBlank(sysId)){
            sysId = " and e.sysId = '" + sysId + "'";
        }else{
            sysId = "";
        }
        String hql = MessageFormat.format( "select e.id from {0} e where (curSpUids like ? or curSpUids like ? or curSpUids = ? or curSpUids like ?) {1}" , SqlUtils.tableName(FlowIns.class), sysId);
        List list = flowInsDao.listBySQL(hql,"%," + uid,uid + ",%",uid + "","%," + uid + ",%");
        return list;
    }

    @Override
    public List<Integer> getFlowInsIdByUser(Integer uid,String sysId,Integer type) {
        String sql = null;
        List<Object> param = new ArrayList<Object>();
        List<Object> uids = new ArrayList<Object>();
        uids.add(uid + "");
        uids.add(SqlUtils.sqllLike("," + uid));
        uids.add((uid != null && (uid + "").trim().length() >= 1)? (uid + "").trim() + "%":null);
        uids.add(SqlUtils.sqlLike("," + uid + ","));
        if(type == null){//全部
            sql = MessageFormat.format( "select busiId from {0} where (curspuids = ? or curspuids  like ? or curspuids like ? or curspuids like ?) and sysid = ?"
            + " union all select busiId from {0} where id in (select insid from {1} where createuid = ?) and status = ? and id not in (select id from {0} where curspuids = ? or curspuids  like ? or curspuids like ? or curspuids like ?) and sysid = ?"
            + " union all select busiId from {0} where id in (select insid from {1} where createuid = ?)  and (status = ? or status = ?)  and sysid = ?",tableName(),spInfoDao.tableName());
            param.addAll(uids);param.add(sysId);
            param.add(uid + "");param.add(3);param.addAll(uids);param.add(sysId);
            param.add(uid + "");param.add(99);param.add(-1);param.add(sysId);
        }else if(type == 1){//待审
            sql =MessageFormat.format( "select busiId from {0} where (curspuids = ? or curspuids  like ? or curspuids like ? or curspuids like ?) and sysid = ?", tableName());
            param.addAll(uids);param.add(sysId);
        }else if(type == 2){//审核中
            sql = MessageFormat.format( "select busiId from {0} where id in (select insid from {1} where createuid = ?) and status = ? and id not in (select id from {0} where curspuids = ? or curspuids  like ? or curspuids like ? or curspuids like ?) and sysid = ?", tableName(),spInfoDao.tableName());
            param.add(uid + "");param.add(3);param.addAll(uids);param.add(sysId);
        }else if(type == 3){//已审
            sql = MessageFormat.format( "select busiId from {0} where id in (select insid from {1} where createuid = ?)  and (status = ? or status = ?)  and sysid = ?",flowInsDao.tableName(),spInfoDao.tableName());
            param.add(uid + "");param.add(99);param.add(-1);param.add(sysId);
        }
        List list = flowInsDao.listBySQL(sql,param.toArray());
        uids.clear();
        param.clear();
        return list;
    }

    public Integer[] getFlowInsIdCountByUser(Integer uid,String sysId){
        List<Object> uids = new ArrayList<Object>();
        uids.add(uid + "");
        uids.add(SqlUtils.sqllLike("," + uid));
        uids.add(SqlUtils.sqlrLike(uid + ","));
        uids.add(SqlUtils.sqlLike("," + uid + ","));
        String sql = null;
        List<Object> param = new ArrayList<Object>();
        sql = MessageFormat.format( "select id from {0} where (curspuids = ? or curspuids  like ? or curspuids like ? or curspuids like ?) and sysid = ?", tableName());
        param.clear();
        param.addAll(uids);param.add(sysId);
        int n1 = flowInsDao.listBySQL(sql,param.toArray()).size();
        sql = MessageFormat.format( "select id from {0} where id in (select insid from {1} where createuid = ?) and status = ? and id not in (select id from {0} where curspuids = ? or curspuids  like ? or curspuids like ? or curspuids like ?) and sysid = ?",SqlUtils.tableName(FlowIns.class),SqlUtils.tableName(SpInfo.class));
        param.clear();
        param.add(uid + "");param.add(3);param.addAll(uids);param.add(sysId);
        int n2 = flowInsDao.listBySQL(sql,param.toArray()).size();
        sql = MessageFormat.format( "select id from {0} where id in (select insid from {1} where createuid = ?)  and (status = ? or status = ?)  and sysid = ?",SqlUtils.tableName(FlowIns.class),SqlUtils.tableName(SpInfo.class));
        param.clear();
        param.add(uid + "");param.add(99);param.add(-1);param.add(sysId);
        int n3 = flowInsDao.listBySQL(sql,param.toArray()).size();

        return new Integer[]{n1,n2,n3};
    }


}
