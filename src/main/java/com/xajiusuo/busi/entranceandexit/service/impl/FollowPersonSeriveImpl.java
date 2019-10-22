package com.xajiusuo.busi.entranceandexit.service.impl;

import com.xajiusuo.busi.entranceandexit.dao.FollowPersonDao;
import com.xajiusuo.busi.entranceandexit.entity.FollowOwnerVo;
import com.xajiusuo.busi.entranceandexit.entity.FollowPerson;
import com.xajiusuo.busi.entranceandexit.service.FollowPersonService;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonDetialsVo;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonRelationVo;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/17.
 */
@Service
public class FollowPersonSeriveImpl extends BaseServiceImpl<FollowPerson,String> implements FollowPersonService {

    @Autowired
    private FollowPersonDao followPersonDao;

    @Override
    public BaseDao<FollowPerson, String> getBaseDao() {
        return followPersonDao;
    }


    @Override
    public Page<Map> queryPageFollowPerson(Pageable pageable, FollowPersonVo followPersonVo) {
        followPersonDao.queryRangeAll();
        List<Object> param = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select f.id id,f.owneridcard owneridcard,d.val followtype,o.name ownername,o.PHOTO photo,o.IDADDR IDADDR from t_followperson_16 f \n" +
                "    left join t_owner_19 o on f.owneridcard=o.idnumber " +
                "    left join p_diction_11 d on f.followtype = d.keys where (f.delflag = 0 or f.delflag is null)");
        if(followPersonVo!=null){
            if(CommonUtils.isNotEmpty(followPersonVo.getFpType())){ //关注人员类型
                sb.append(" and f.followtype = ?");
                param.add(followPersonVo.getFpType());
            }
            if(CommonUtils.isNotEmpty(followPersonVo.getFpCardId())){  //关注人员证件编号
                sb.append(" and f.owneridcard like ?");
                param.add(SqlUtils.sqlLike(followPersonVo.getFpCardId()));
            }
            if(CommonUtils.isNotEmpty(followPersonVo.getFpName())){  //关注人员姓名
                sb.append(" and o.name like ?");
                param.add(SqlUtils.sqlLike(followPersonVo.getFpName()));
            }
        }
        sb.append(" order by f.lastModifyTime DESC");
        return followPersonDao.queryPageSqlBean(pageable,sb.toString(),param.toArray());
    }

    @Override
    public FollowPersonDetialsVo queryDetials(String fpId) {
        String sql = "select t1.name ownerName,t.owneridcard,t2.val followtype,t1.idaddr,t1.idphoto,t3.val identifyType from t_followperson_16 t \n" +
                "left join t_owner_19 t1 on t.owneridcard=t1.idnumber \n" +
                "left join p_diction_11 t2 on t.followtype=t2.keys\n" +
                "left join p_diction_11 t3 on t1.identify_type=t3.id\n" +
                "where t.id = '"+fpId+"'";
        FollowPersonDetialsVo detialsVo = null;
        try {
            List<FollowPersonDetialsVo> detialsList = followPersonDao.querySqlBean(sql,FollowPersonDetialsVo.class,null);
            if(detialsList!=null && detialsList.size()>0){
                detialsVo = detialsList.get(0);
            }
            if(detialsVo!=null){
                String idcard = detialsVo.getOwneridcard(); //证件编号
                if(CommonUtils.isNotEmpty(idcard)){
                    //查询出入口门禁最新记录
                    String sql_crk = "select tt.id,tt.passtype,tt.passtime from (\n" +
                            "select row_number() over(partition by entrancecardno,passtype order by passtime) rn,t.* from (\n" +
                            "select g.* from t_gateentranceguardlog_33 g left join t_gateentrancecard_33 g1 on g.entrancecardno = g1.entrancecardno where g1.personidnumber = '"+idcard+"'\n" +
                            ") t \n" +
                            ") tt where rn = 1";
                    List<Object[]> crkList = followPersonDao.listBySQL(sql_crk,null);
                    if(crkList!=null && crkList.size()>0){
                        for(Object[] object:crkList){
                            String lx = object[1]!=null ? object[1].toString():"";
                            if("0".equals(lx)){
                                detialsVo.setLastGateEntranceOutDate(object[2].toString());
                            }
                            if("1".equals(lx)){
                                detialsVo.setLastGateEntranceInDate(object[2].toString());
                            }
                        }
                    }
                    //查询楼宇门禁最新记录
                    String sql_lycrk = "select tt.id,tt.passtype,tt.passtime from (\n" +
                            "select row_number() over(partition by entrancecardno,passtype order by passtime) rn,t.* from (\n" +
                            "select b.* from t_buildingentranceguardlog_33 b left join T_BUILDINGENTRANCECARD_33 b1 on b.entrancecardno = b1.entrancecardno where b1.personidnumber = '"+idcard+"'\n" +
                            ") t \n" +
                            ") tt where rn = 1";
                    List<Object[]> lycrkList = followPersonDao.listBySQL(sql_lycrk,null);
                    if(lycrkList!=null && lycrkList.size()>0){
                        for(Object[] object:lycrkList){
                            String lx = object[1]!=null ? object[1].toString():"";
                            if("0".equals(lx)){
                                detialsVo.setLastBuildingEntranceOutDate(object[2].toString());
                            }
                            if("1".equals(lx)){
                                detialsVo.setLastBuildingEntranceInDate(object[2].toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detialsVo;
    }

    @Override
    public List<FollowPersonRelationVo> queryRelations(String idcard) {
        String sql = "select idnumber relationIdcard,name relationName,phone relationPhone,photo relationPhoto from t_owner_19 where idnumber in (select ownerid from t_houseowner_19 where houseid in (select houseid from t_houseowner_19 where ownerid = '"+idcard+"')) --and identify_type= 11";
        try {
            return followPersonDao.querySqlBean(sql,FollowPersonRelationVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<FollowOwnerVo> queryUnFollowOwner(String fieldName, String fieldValue) {
        followPersonDao.queryRangeAll();
        String sql = "select t.id,t.name ownerName,t.idnumber ownerIdcard,t.villageId,t1.villagename from t_owner_19 t " +
                "left join t_village_19 t1 on t.villageid = t1.villageid " +
                "where t.delFlag = 0 and t.idnumber not in (select owneridcard from t_followperson_16 where (delflag =0 or delflag is null) )" +
                "and t."+fieldName+" like '%"+fieldValue+"%'";
        try {
            return followPersonDao.querySqlBean(sql,FollowOwnerVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
