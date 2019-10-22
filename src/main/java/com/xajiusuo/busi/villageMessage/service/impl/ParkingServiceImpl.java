package com.xajiusuo.busi.villageMessage.service.impl;

import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.villageMessage.dao.OwnerDao;
import com.xajiusuo.busi.villageMessage.dao.ParkingDao;
import com.xajiusuo.busi.villageMessage.dao.VillageDao;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.entity.Parking;
import com.xajiusuo.busi.villageMessage.service.ParkingService;
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
import java.util.List;
import java.util.Map;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 停车位信息逻辑处理接口
 */
@Service
public class ParkingServiceImpl extends BaseServiceImpl<Parking,String> implements ParkingService {
    private final Logger log = LoggerFactory.getLogger(ParkingServiceImpl.class);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    DictionService dictionService;

    @Autowired
    ParkingDao parkingDao;

    @Autowired
    OwnerDao ownerDao;



    @Autowired
    VillageDao villageDao;

    @Override
    public BaseDao<Parking, String> getBaseDao() {
        return parkingDao;
    }

    @Override
    public Page<Parking> getsqlpage(Pageable pageable, Parking parking){
        Page<Parking> page = null;

        try{
            String sql = "select * from  T_PARKING_19 where 1=1 and delflag = ? ";
            if(null!= parking.getVillageid() && !"".equals(parking.getVillageid())){
                sql += " and  villageid = '"+parking.getVillageid()+"'";
            }
            if(null != parking.getParkingno() && !"".equals(parking.getParkingno())){ //停车场编号
                sql += " and parkingno like '%"+parking.getParkingno()+"%' ";
            }

            if(null != parking.getOwner()){
                if(null!= parking.getOwner().getIdnumber() && !"".equals(parking.getOwner().getIdnumber())){
                    sql += " and ownerid like '%"+parking.getOwner().getIdnumber()+"%' ";
                }
            }
            page = this.parkingDao.executeQuerySqlByPage(pageable,sql, "0");
            log.info("停车场信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };

    @Override
    public  List  getSelectParking(String villageid,String parkingno) throws Exception{
        List list = new ArrayList<>();
        String sql = "select * from T_PARKING_19 where 1=1 and delflag = ?";
        if(!"".equals(villageid) && null != villageid){
            sql +=" and villageid = '"+villageid.trim()+"'";
        }
        if(!"".equals(parkingno) && null != parkingno){
            sql += "  and   parkingno like '%"+parkingno.trim()+"%' ";
        }
        if(null != parkingno && !"".equals(parkingno)){
            list = this.parkingDao.executeNativeQuerySql(sql, "0",null);
        }

        return  list;
    };

    @Override
    public Long chekBs(String villageid,String bs,String bsValue) throws Exception{
        String sql = " select count(*) from T_PARKING_19 where 1=1 and delflag = 0   and "+bs+"= '"+bsValue+"'";
        if(null != villageid && !"".equals(villageid)){
            sql+= " and villageid = '"+villageid+"'";
        }
        return this.parkingDao.count(sql,null);
    };
    @Override
    public List getDictionForUpload(String  keys) throws Exception{
        List<String> result = new ArrayList<>();
        List<Diction> listDiction = dictionService.listDictions(keys);
        for(int i=0;i<listDiction.size();i++){
            Diction diction = listDiction.get(i);
            result.add(diction.getVal()+"-"+diction.getKeys());
        }

        return result;
    };


    @Override
    public List saveUpdate(InputStream in) throws Exception{
        ModelExcel modelExcel = new ModelExcel();
        Map<Integer, Map<Integer, Object>> content= modelExcel.readExcelContent(in);

        List<String> villageNoExit = this.villageDao.getList("villageid","delFlag",false,null,null,null);
        List<String> parkingnoExist = this.parkingDao.getList("parkingno","delFlag",false,null,null,null);
        List<String> ownerNoExit = this.ownerDao.getList("idnumber","delFlag",false,null,null,null);

        List<Parking> parkings = new ArrayList<>();
        List errorParking = new ArrayList<>();
        for(int i = 1;i<content.size()+1;i++){
            Map<Integer, Object> aa = content.get(i);
            if(!parkingnoExist.contains(aa.get(1)) && villageNoExit.contains(aa.get(0)) && ownerNoExit.contains(aa.get(2))){
                Parking parking = new Parking();

                parking.setParkingno(aa.get(1).toString());
                if(null!=aa.get(3) &&!"".equals(aa.get(3))){
                    String keys = aa.get(3).toString().split("-")[1].toString();
                    Diction parkingtype = new Diction();
                    parkingtype.setKeys(keys);
                    parking.setParkingtype(parkingtype);
                }
                parking.setMotorvehicleid(aa.get(4).toString());

                if(null!=aa.get(2) &&!"".equals(aa.get(2))){
                    Owner owner = new Owner();
                    owner.setIdnumber(aa.get(2).toString());
                    parking.setOwner(owner);
                }
                parking.setBegintime(simpleDateFormat.parse(aa.get(5).toString()));
                parking.setEndtime(simpleDateFormat.parse(aa.get(6).toString()));


                parkings.add(parking);
            }else{
                Integer m = i+1;
                errorParking.add("行号:"+m+"    "+aa.get(0));
            };
        };
//        System.out.println(houses);
        this.parkingDao.batchSaveOrUpdate(parkings);
        return errorParking;
    };
}
