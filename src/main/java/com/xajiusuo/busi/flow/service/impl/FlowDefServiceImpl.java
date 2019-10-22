package com.xajiusuo.busi.flow.service.impl;

import com.xajiusuo.busi.flow.dao.FlowDefDao;
import com.xajiusuo.busi.flow.dao.FlowNodeDao;
import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.entity.FlowNodeVo;
import com.xajiusuo.busi.flow.service.FlowDefService;
import com.xajiusuo.busi.user.dao.DepartDao;
import com.xajiusuo.busi.user.dao.DutyDao;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.xajiusuo.busi.user.service.impl.UserinfoServiceImpl.like;

/**
 * Created by fanhua on 18-1-22.
 */
@Service
public class FlowDefServiceImpl extends BaseServiceImpl<FlowDef, Integer> implements FlowDefService {

    @Autowired
    private FlowDefDao flowDefDao;

    @Autowired
    private FlowNodeDao flowNodeDao;

    @Override
    public BaseDao<FlowDef, Integer> getBaseDao() {
        return flowDefDao;
    }

    @Autowired
    DutyDao dutyRepository;

    @Autowired
    UserinfoDao userInfoRepository;

    @Autowired
    DepartDao departRepository;

    @Override
    public List<FlowDef> findByParentAndLevl(FlowDef flowDef) {
        String sql = MessageFormat.format("select * from {0} f where 1 = 1 and flevel=1 and f.parentId = ?", SqlUtils.tableName(FlowDef.class));
        return flowDefDao.executeNativeQuerySql(sql, flowDef.getId());
    }

    @Override
    public Page<FlowDef> findByParentAndFlevel(FlowDef flowDef, int flevel, Pageable pageable) {
        return flowDefDao.findByParentAndFlevel(flowDef, flevel, pageable);
    }

    @Override
    public Page<FlowDef> findByFlevel(int flevel, Pageable pageable) {
        return flowDefDao.findByFlevel(flevel, pageable);
    }

    @Override
    public Page<FlowDef> findByFlevelAndInitMode(int flevel, Boolean initMode, Pageable pageable) {
        return flowDefDao.findByFlevelAndInitMode(flevel, initMode, pageable);
    }

    @Override
    public FlowDef getDefault() {
        FlowDef def = null;
        List<FlowDef> list = flowDefDao.findBy("initUse",true);
        for(FlowDef d:list){
            def = d;
            if(def.getFlevel().equals(0)){
                break;
            }
        }
        list.clear();
        return def;
    }

    @Override
    public FlowDef findFlowIns(FlowDef def,Integer uid) {
        if(def.getFlevel() >= 1){
            return def;
        }
        FlowDef ins = getOne(def.getInsdefid());
        if(ins != null){
            return ins;
        }
        ins = new FlowDef();
        BeanUtils.copyProperties(def,ins);
        BaseEntityUtil.addEntity(ins,uid);
        ins.setParent(def);//设置父类流程
        ins.setFlevel(1);//设置类型为使用类型
        ins.setInitUse(false);
        flowDefDao.save(ins);//保存流程副本
        def.setInsdefid(ins.getId());//设置定义的实体
        flowDefDao.saveAndFlush(def);//修改流程父类

        List<FlowNode> list = flowNodeDao.findBy("flowDef",def);
        List<FlowNode> list1 = new ArrayList<FlowNode>();
        for(FlowNode n:list){
            FlowNode fn = new FlowNode();
            BeanUtils.copyProperties(n,fn);
            BaseEntityUtil.addEntity(fn,uid);
            fn.setFlowDef(ins);
            list1.add(fn);
        }
        flowNodeDao.saveAll(list1);
        return ins;
    }

    @Override
    public Page<FlowDef> findAll(Pageable pageable) {
        return flowDefDao.findAll(pageable);
    }

    @Override
    public List<FlowDef> findByFname(String flowName) {
        return flowDefDao.getList("fname", flowName,null,null,null);
    }

    @Override
    public FlowNodeVo setRolesAndUsers(FlowNode flowNode, FlowNodeVo flowNodeVo) {
        if (!StringUtils.isEmpty(flowNode.getDepId())) {
            Depart depart = departRepository.getOne(flowNode.getDepId());
            flowNodeVo.setDeptName(depart.getDname());
        }
        if (!StringUtils.isEmpty(flowNode.getRoleIds())) {
            String[] roleIds = flowNode.getRoleIds().split(",");
            StringBuilder roleNames = new StringBuilder();
            for (String roleId : roleIds) {
                Duty duty = dutyRepository.getOne(Integer.parseInt(roleId));
                if (StringUtils.isEmpty(roleNames.toString())) {
                    roleNames.append(duty.getDutyname());
                } else {
                    roleNames.append("，" + duty.getDutyname());
                }
            }
            flowNodeVo.setRoleNames(roleNames.toString());
        }
        if (!StringUtils.isEmpty(flowNode.getUserIds())) {
            String[] userIds = flowNode.getUserIds().split(",");
            StringBuilder userNames = new StringBuilder();
            for (String userId : userIds) {
                Userinfo userinfo = userInfoRepository.getOne(Integer.parseInt(userId));
                if (StringUtils.isEmpty(userNames.toString())) {
                    userNames.append(userinfo.getFullname());
                } else {
                    userNames.append("，" + userinfo.getFullname());
                }
            }
            flowNodeVo.setUserNames(userNames.toString());
        }
        return flowNodeVo;
    }

    @Override
    public Page<FlowDef> query(Pageable pageable, String fname) {
        StringBuffer hql = new StringBuffer(MessageFormat.format("select e.* from {0} e where 1 = 1 and flevel=1",tableName()));
        if (!StringUtils.isEmpty(fname)) {
            hql.append(" and e.fname like " + like(fname));
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
        return flowDefDao.executeQuerySqlByPage(pageable, hql.toString());
    }

    @Override
    public Result flowDefDelete(int id) {
        FlowDef flowDef = getOne(id);
        if(flowDef == null || flowDef.getId() == null){
            return Result.find(CfI.R_FLOW_FLOWNOTEXIST_FAIL);
        }
        if (true == flowDef.getInitUse()) {
            return Result.find(CfI.R_FLOW_NOTDELETEDEFAULT_FAIL);
        }
        List<FlowDef> flowDefList = findByParentAndLevl(flowDef);
        flowDefList.forEach(x -> x.setParent(null));
        flowDefDao.saveAll(flowDefList);
        flowNodeDao.findBy("flowDef", flowDef).forEach(flowNodeDao::delete);
        delete(id);
        return null;
    }
}
