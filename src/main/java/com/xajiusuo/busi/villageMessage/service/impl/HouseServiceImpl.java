package com.xajiusuo.busi.villageMessage.service.impl;

import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.villageMessage.dao.BuildingDao;
import com.xajiusuo.busi.villageMessage.dao.HouseDao;
import com.xajiusuo.busi.villageMessage.dao.UnitDao;
import com.xajiusuo.busi.villageMessage.dao.VillageDao;
import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.busi.villageMessage.service.HouseService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 房屋信息逻辑处理接口
 */
@Service
public class HouseServiceImpl extends BaseServiceImpl<House,String> implements HouseService {
    private final Logger log = LoggerFactory.getLogger(HouseServiceImpl.class);

    @Autowired
    DictionService dictionService;

    @Autowired
    HouseDao houseDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    BuildingDao buildingDao;

    @Autowired
    VillageDao villageDao;

    @Override
    public BaseDao<House, String> getBaseDao() {
        return houseDao;
    }

    @Override
    public Page<House> getsqlpage(Pageable pageable, House house) throws Exception{
        Page<House> page = null;

        String sqlbase = "select a.ID,a.HOUSENO,a.STANDARDADDR,a.STANDARDADDRCODE,a.DETAILADDR,a.HOUSETYPE,a.HOUSEAREA,a.ROOMNUM,a.HOUSECERTIFICATENO,a.OWNERIDNUMBER,a.OWNERNAME,a.OWNERPHONE,a.OWNERIDNUMBER1,a.OWNERNAME1,a.OWNERPHONE1,a.FLOOR,a.UNITID,b.UNIT_NAME as unitname,a.BUILDINGID,c.BUILDINGNAME,a.VILLAGEID,d.VILLAGENAME,a.PEOPLE_NUMBER,a.HOUSE_ATTRUBUTE,a.RENTAL,a.POSITION,a.ROOM_NAME,a.CREATETIME,a.LASTMODIFYTIME,a.CREATEUID,a.LASTMODIFYUID,a.CREATENAME,a.DELFLAG from T_HOUSE_19 a,T_UNIT_19 b,T_BUILDING_19 c,T_VILLAGE_19 d where a.unitid = b.unitno and a.buildingid = c.buildingno and a.villageid = d.villageid ";
        try{
            String sql = "select * from  ("+sqlbase+") where 1=1 and delflag = 0 ";
            if(!"".equals(house.getVillageid()) && null != house.getVillageid()){
                sql += " and villageid = '"+house.getVillageid()+"'";
            }
            if(!"".equals(house.getBuildingid()) && null != house.getBuildingid()){
                sql += " and buildingid = '"+house.getBuildingid()+"'";
            }
            if(!"".equals(house.getUnitid()) && null != house.getUnitid()){
                sql +=" and unitid = '"+house.getUnitid()+"'";
            }
            if(!"".equals(house.getHouseno()) && null != house.getHouseno()){
                sql += " and houseno = '%"+house.getHouseno()+"%' ";
            }
            if(!"".equals(house.getUnitname()) && null!=house.getUnitname()){
                sql += " and unitname like '%"+house.getUnitname()+"%' ";
            }
            if(!"".equals(house.getBuildingname()) && null!=house.getBuildingname()){
                sql += " and buildingname like '%"+house.getBuildingname()+"%'";
            }
            if(!"".equals(house.getVillagename()) && null != house.getVillagename()){
                sql += " and villagename like '%"+house.getVillagename()+"%'";
            }
            if(null != house.getHouse_attrubute()){
                if(null != house.getHouse_attrubute().getKeys() && !"".equals(house.getHouse_attrubute().getKeys())){
                    sql += " and house_attrubute = '"+house.getHouse_attrubute().getKeys()+"'";
                }
            }
            if(null != house.getRental()){
                if(null != house.getRental().getKeys() && !"".equals(house.getRental().getKeys())){
                    sql += " and rental = '"+house.getRental().getKeys()+"' ";
                }
            }
            if(null != house.getOwneridnumber() && !"".equals(house.getOwneridnumber())){
                sql += " and owneridnumber like '%"+house.getOwneridnumber()+"%' ";
            }
            if(null != house.getOwnerphone() && !"".equals(house.getOwnerphone())){
                sql += " and ownerphone like '%"+house.getOwnerphone()+"%' ";
            }
            if(null != house.getOwnername() && !"".equals(house.getOwnername())){
                sql += " and ownername like '%"+house.getOwnername()+"%' ";
            }
            page = this.houseDao.executeQuerySqlByPage(pageable,sql,null,null,null);
            log.info("房屋信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };
    @Override
    public Long chekBs(String villageid,String buildingid,String unitid,String bs,String bsValue)throws Exception{
        String sql = " select count(*) from T_HOUSE_19 where 1=1 and delflag = 0  and "+bs+"= '"+bsValue+"'";
        return this.houseDao.count(sql,null);
    };

    @Override
    public List getSelectHouse(String villageid,String buildingid,String unitid) throws Exception{
        String sql = "select * from T_HOUSE_19 where 1=1 and delflag = ? and villageid = ? and buildingid = ? and unitid = ?";
        return this.houseDao.executeNativeQuerySql(sql, "0",villageid,buildingid, unitid);
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
//    {0=100011010106, 1=, 2=, 3=西安市, 4=, 5=, 6=, 7=112, 8=6101111111111111000000, 9=, 10=, 11=, 12=, 13=, 14=, 15=10001101, 16=, 17=100011, 18=, 19=10001, 20=, 21=, 22=自住-26, 23=待租-31, 24=, 25=}
    @Override
    public List saveUpdate(InputStream in) throws Exception{
        ModelExcel modelExcel = new ModelExcel();
        Map<Integer, Map<Integer, Object>> content= modelExcel.readExcelContent(in);

        List<String> villageNoExit = this.villageDao.getList("villageid","delFlag",false,null,null,null);
        List<String> buildingNoExit = this.buildingDao.getList("buildingno","delFlag",false,null,null,null);
        List<String> unitNoExit = this.unitDao.getList("unitno","delFlag",false,null,null,null);
        List<String> houseNoExist = this.houseDao.getList("houseno","delFlag",false,null,null,null);

        List<House> houses = new ArrayList<>();
        List errorHouse = new ArrayList<>();
        for(int i = 1;i<content.size()+1;i++){
           Map<Integer, Object> aa = content.get(i);
            if(!houseNoExist.contains(aa.get(0)) && unitNoExit.contains(aa.get(15)) && buildingNoExit.contains(aa.get(17)) && villageNoExit.contains(aa.get(19))){
                House house = new House();
                house.setId(null);
                house.setDelFlag(false);
                house.setHouseno(aa.get(0).toString());
                house.setStandardaddr(aa.get(1).toString());
                house.setStandardaddrcode(aa.get(2).toString());
                house.setDetailaddr(aa.get(3).toString());

                if(null!=aa.get(4) &&!"".equals(aa.get(4))){
                    String keys = aa.get(4).toString().split("-")[1].toString();
                    Diction houseType = new Diction();
                    houseType.setKeys(keys);
                    house.setHouse_attrubute(houseType);
                }
                house.setHousearea(aa.get(5).toString());
                house.setRoomnum(aa.get(6).toString());
                house.setHousecertificateno(aa.get(7).toString());
                house.setOwneridnumber(aa.get(8).toString());
                house.setOwnername(aa.get(9).toString());
                house.setOwnerphone(aa.get(10).toString());
                house.setOwneridnumber1(aa.get(11).toString());
                house.setOwnername1(aa.get(12).toString());
                house.setOwnerphone1(aa.get(13).toString());
                house.setFloor(aa.get(14).toString());
                house.setUnitid(aa.get(15).toString());
                house.setUnitname(aa.get(16).toString());
                house.setBuildingid(aa.get(17).toString());
                house.setBuildingname(aa.get(18).toString());
                house.setVillageid(aa.get(19).toString());
                house.setVillagename(aa.get(20).toString());
                house.setPeople_number(aa.get(21).toString());
                if(null!=aa.get(22) &&!"".equals(aa.get(22))){
                    String keys = aa.get(22).toString().split("-")[1].toString();
                    Diction house_attrubute = new Diction();
                    house_attrubute.setKeys(keys);
                    house.setHouse_attrubute(house_attrubute);
                }
                if(null != aa.get(23) && !"".equals(aa.get(23))){
                    String keys = aa.get(23).toString().split("-")[1].toString();
                    Diction rental = new Diction();
                    rental.setKeys(keys);
                    house.setRental(rental);
                }
                house.setPosition(aa.get(24).toString());
                house.setRoom_name(aa.get(25).toString());
                houses.add(house);
//                System.out.println(aa);
            }else{
                Integer m = i+1;
                errorHouse.add("行号:"+m+"    "+aa.get(0));
            };
        };
//        System.out.println(houses);
            this.houseDao.batchSaveOrUpdate(houses);
        return errorHouse;
    };
}
