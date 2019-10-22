package com.xajiusuo.busi.villageMessage.service.impl;

import com.xajiusuo.busi.villageMessage.dao.ParkingLogDao;
import com.xajiusuo.busi.villageMessage.entity.ParkingLog;
import com.xajiusuo.busi.villageMessage.service.ParkingLogService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 车位租赁逻辑处理接口
 */
@Service
public class ParkingLogServiceImpl extends BaseServiceImpl<ParkingLog,String> implements ParkingLogService {
    private final Logger log = LoggerFactory.getLogger(ParkingLogServiceImpl.class);
    private String dateFormat = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    @Autowired
    ParkingLogDao parkingLogDao;
    @Override
    public BaseDao<ParkingLog, String> getBaseDao() {
        return parkingLogDao;
    }

    @Override
    public Page<ParkingLog> getsqlpage(Pageable pageable, ParkingLog parkingLog){
        Page<ParkingLog> page = null;

        try{
            String sql = "select * from  T_PARKINGLOG_19 where 1=1 and delflag = 0";
            if(null != parkingLog.getParkingno() && !"".equals(parkingLog.getParkingno())){ //车位编号
                sql += "and parkingno like '%"+parkingLog.getParkingno().trim()+"%' ";
            }

            if(null!=parkingLog.getPlateno()){
                if(null != parkingLog.getPlateno().getPlateNo() && !"".equals(parkingLog.getPlateno().getPlateNo())){ //车牌号
                    sql += "and plateno like '%"+parkingLog.getPlateno().getPlateNo().trim()+"%'";
                }
            }

//            if(null != parkingLog.getStarttime() && !"".equals(parkingLog.getStarttime())){ // 开始时间
//                sql += "and to_char(starttime,'"+dateFormat+"')  >='"+simpleDateFormat.format(parkingLog.getStarttime())+"";
//            }
//            if(null != parkingLog.getEndtime() && !"".equals(parkingLog.getEndtime())){ // 结束日期
//                sql += "and to_char(endtime,'"+dateFormat+"')  <='"+simpleDateFormat.format(parkingLog.getEndtime())+"";
//            }
            page = this.parkingLogDao.executeQuerySqlByPage(pageable,sql);
            log.info("车位租赁信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };
    @Override
    public Long chekBs(String parkingno, Date starttime, Date endtime) throws Exception{
//        String sql = "select count(*) from T_PARKINGLOG_19 where 1=1 and parkingno = '"+parkingno+"' and '"+simpleDateFormat.format(endtime)+"'>to_char(endtime,'yyyy-MM-dd HH:mm:ss')";
        String sql = " select count(*) from T_PARKINGLOG_19 where 1=1  and parkingno  = '"+parkingno+"' " +
                " and(  " +
                " (to_char(starttime,'yyyy-MM-dd') < '"+simpleDateFormat.format(starttime)+"'  and '"+simpleDateFormat.format(starttime)+"' <to_char(endtime,'yyyy-MM-dd'))  " +
                " or  (to_char(starttime,'yyyy-MM-dd') < '"+simpleDateFormat.format(endtime)+"'  and '"+simpleDateFormat.format(endtime)+"' <to_char(endtime,'yyyy-MM-dd')) " +
                " or (to_char(starttime,'yyyy-MM-dd') > '"+simpleDateFormat.format(starttime)+"'  and '"+simpleDateFormat.format(endtime)+"' >to_char(endtime,'yyyy-MM-dd')) " +
                " ) ";
        Long count =  this.parkingLogDao.count(sql);
        return count;
    };
    @Deprecated
    @Override
    public List getDictionForUpload(Integer pid){
        return this.parkingLogDao.listBySQL("select val||'-'||id from p_diction_11 where pid = ? ",pid);
    };
}
