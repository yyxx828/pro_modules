package com.xajiusuo.busi.villageMessage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.villageMessage.dao.HouseLogDao;
import com.xajiusuo.busi.villageMessage.entity.HouseLog;
import com.xajiusuo.busi.villageMessage.service.HouseLogService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 房屋租赁逻辑处理接口
 */
@Service
public class HouseLogServiceImpl extends BaseServiceImpl<HouseLog,String> implements HouseLogService {
    private final Logger log = LoggerFactory.getLogger(HouseLogServiceImpl.class);
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    @Autowired
    HouseLogDao houseLogDao;
    @Override
    public BaseDao<HouseLog, String> getBaseDao() {
        return houseLogDao;
    }

    @Override
    public Page<HouseLog> getsqlpage(Pageable pageable, String buildingid, String unitid, String houseid, String idnumber) throws Exception{
        Page<HouseLog> page = null;
        try{
            String sql = "select a.* from  T_HOUSELOG_19 a,T_HOUSE_19 b where 1=1 and a.delflag = 0 and a.houseno = b.houseno ";
            if(!"".equals(buildingid) && null !=buildingid){ //楼栋号
                sql += "and b.buildingid = '"+buildingid+"' ";
            }
            if(null != unitid && !"".equals(unitid)){
                sql += "and b.unitid = '"+unitid+"' ";
            }
            if(null != houseid && !"".equals(houseid)){
                sql += " and b.houseno = '"+houseid+"' ";
            }
            if(null != idnumber && !"".equals(idnumber)){
                sql += " and a.idnumber like '%"+idnumber+"%' ";
            }
            page = this.houseLogDao.executeQuerySqlByPage(pageable,"select * from ("+sql+")");
            log.info("房屋租赁信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };
    @Override
    public Long chekBs(String idnumber,String houseno,Date starttime,Date endtime) throws Exception{
//        String sql = "select count(*) from T_HOUSELOG_19 where 1=1 and houseno = '"+houseno+"' and '"+simpleDateFormat.format(endtime)+"'>to_char(endtime,'yyyy-MM-dd HH:mm:ss')";
        String sql = " select count(*) from T_HOUSELOG_19 where 1=1  and idnumber  = '"+idnumber+"' and houseno = '"+houseno+"' " +
                " and( " +
                " (to_char(starttime,'yyyy-MM-dd') < '"+simpleDateFormat.format(starttime)+"'  and '"+simpleDateFormat.format(starttime)+"' <to_char(endtime,'yyyy-MM-dd'))  " +
                " or  (to_char(starttime,'yyyy-MM-dd') < '"+simpleDateFormat.format(endtime)+"'  and '"+simpleDateFormat.format(endtime)+"' <to_char(endtime,'yyyy-MM-dd')) " +
                " or (to_char(starttime,'yyyy-MM-dd') > '"+simpleDateFormat.format(starttime)+"'  and '"+simpleDateFormat.format(endtime)+"' >to_char(endtime,'yyyy-MM-dd')) " +
                " ) ";
        Long count =  this.houseLogDao.count(sql,null);
        return count;
    };
    @Deprecated
    @Override
    public List getDictionForUpload(Integer pid){
        return this.houseLogDao.listBySQL("select val||'-'||id from p_diction_11 where pid = ? ",pid);
    };

    @Deprecated
    @Override
    public List saveUpdate(InputStream in) throws Exception{
        ModelExcel modelExcel = new ModelExcel();
        JSONObject result = new JSONObject();
        Map<Integer, Map<Integer, Object>> content= modelExcel.readExcelContent(in);
        List<HouseLog> houseLogs = new ArrayList<>(); //房屋租赁流水信息
        List errorOwner = new ArrayList<>();
        for(int i = 1;i<content.size();i++){
            Map<Integer, Object> aa = content.get(i);
            System.out.println(aa);
                Integer m = i+1;
//                errorOwner.add("行号:"+m+"    "+aa.get(1) +": "+aa.get(3));

        };
        this.houseLogDao.batchSaveOrUpdate(houseLogs);
        return errorOwner;
    };

    @Override
    public Integer getCountHouseNo() throws Exception{
        int result = 0;
        result = this.houseLogDao.queryCountSql("select count (distinct houseno) from T_HOUSELOG_19 where 1=1 ",null);
        return result;
    };
}
