package com.xajiusuo.busi.villageMessage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.villageMessage.dao.OwnerDao;
import com.xajiusuo.busi.villageMessage.dao.VillageDao;
import com.xajiusuo.busi.villageMessage.entity.HouseOwner;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.service.HouseOwnerService;
import com.xajiusuo.busi.villageMessage.service.HouseService;
import com.xajiusuo.busi.villageMessage.service.OwnerService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.DateTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 业主信息逻辑处理接口
 */
@Service
public class OwnerServiceImpl extends BaseServiceImpl<Owner,String> implements OwnerService {
    private final Logger log = LoggerFactory.getLogger(OwnerServiceImpl.class);

    @Autowired
    HouseService houseService;

    @Autowired
    HouseOwnerService houseOwnerService;

    @Autowired
    DictionService dictionService;

    @Autowired
    OwnerDao ownerDao;

    @Autowired
    VillageDao villageDao;


    @Override
    public BaseDao<Owner, String> getBaseDao() {
        return ownerDao;
    }

    @Override
    public Page<Owner> getsqlpage(Pageable pageable, Owner owner,String startTime,String endTime){
        Page<Owner> page = null;
//        1110,22220
//        4号楼:2单元:202室,4号楼:2单元:202室

        String sqlBase = "select  o.ID,o.IDTYPE,o.IDNUMBER,o.NAME,o.SEX,o.BIRTHDAY,o.NATION,o.POLITICSSTATUS,o.EDUCATIONSTATUS,o.PHONE,o.JOB,o.WORKPLACE,o.PHOTO,o.IDPHOTO,o.IDADDR,o.VILLAGEID,o.IDENTIFY_TYPE,o.IDENTIFY_COUNTRY,o.CREATETIME,o.LASTMODIFYTIME,o.CREATEUID,o.LASTMODIFYUID,o.CREATENAME,o.DELFLAG ,listagg(h.houseid,',') within group ( order by h.houseid ) houseid \n" +
                ",listagg(d.VILLAGENAME  ||':'|| c.BUILDINGNAME||':'|| b.UNIT_NAME||':'||a.room_name ,',') within group (order by a.houseno) houseaddr\n" +
                "\n" +
                "from t_owner_19 o, t_houseowner_19 h, T_HOUSE_19 a,T_UNIT_19 b,T_BUILDING_19 c,T_VILLAGE_19 d\n" +
                "\n" +
                "where o.idnumber = h.ownerid and h.houseid = a.houseno and a.unitid = b.unitno and a.buildingid = c.buildingno and a.villageid = d.villageid  \n" +
                "\n" +
                "group by o.ID,o.IDTYPE,o.IDNUMBER,o.NAME,o.SEX,o.BIRTHDAY,o.NATION,o.POLITICSSTATUS,o.EDUCATIONSTATUS,o.PHONE,o.JOB,o.WORKPLACE,o.PHOTO,o.IDPHOTO,o.IDADDR,o.VILLAGEID,o.IDENTIFY_TYPE,o.IDENTIFY_COUNTRY,o.CREATETIME,o.LASTMODIFYTIME,o.CREATEUID,o.LASTMODIFYUID,o.CREATENAME,o.DELFLAG\n" +
                "\n" +
                "\n";
        try{
            String sql = "select * from  ("+sqlBase+") where 1=1 and delflag = 0 ";

            if(null != owner.getHouseid() && !"".equals(owner.getHouseid())){
                sql += " and houseid like '%"+owner.getHouseid()+"%' ";
            }
            if(!"".equals(owner.getVillageid()) && null != owner.getVillageid()){// 小区编号 不是必填项
                sql += "and villageid = '"+owner.getVillageid()+"' ";
            }
            if(!"".equals(owner.getName()) && null != owner.getName()){ //姓名
                sql += "and name like '%"+owner.getName()+"%' ";
            }
            if(!"".equals(owner.getIdnumber()) && null!= owner.getIdnumber()){ //证件号码
                sql += "and idnumber like '%"+owner.getIdnumber()+"%' ";
            }
            if(null != startTime && !"".equals(startTime)){ //出生日期 开始时间
                sql += "and birthday  >= '"+startTime+"' ";
            }
            if(null != endTime && !"".equals(endTime)){ //出生日期 结束日期
                sql += "and birthday  <= '"+endTime+"' ";
            }
            if(null != owner.getNationDiction() && !"".equals(owner.getNationDiction().getKeys()) &&null != owner.getNationDiction().getKeys()){ //民族
                sql += "and nation = '"+owner.getNationDiction().getKeys()+"' ";
            }
            if(!"".equals(owner.getPhone()) &&null != owner.getPhone()){ //联系电话
                sql += "and phone like '%"+owner.getPhone()+"%'";
            }
            if(!"".equals(owner.getIdaddr()) &&null != owner.getIdaddr()){ //户籍地址
                sql += "and idaddr like '%"+owner.getIdaddr()+"%' ";
            }
            if(null != owner.getIdentifyTypeDiction() && !"".equals(owner.getIdentifyTypeDiction().getKeys()) &&null != owner.getIdentifyTypeDiction().getKeys()){ //身份类型
                sql += "and identify_type = '"+owner.getIdentifyTypeDiction().getKeys()+"' ";
            }
            page = this.ownerDao.executeQuerySqlByPage(pageable,sql, null);
            log.info("房屋信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };
    @Override
    public Long chekBs(String villageid,String bs,String bsValue) throws Exception{
            String sql = " select count(*) from T_OWNER_19 where 1=1 and delflag = 0   and "+bs+"= '"+bsValue+"'";
        if(!"".equals(villageid) && null != villageid){
            sql += "and villageid = '"+villageid+"' ";
        }
        return this.ownerDao.count(sql,null);
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
    public List  getSelectOwner(String villageid,String nameOrIdNumber) throws Exception{
        List list = new ArrayList();
            String sql = "select * from T_OWNER_19 where 1=1 and delflag = ?";
            if(!"".equals(villageid) && null != villageid){
                sql +=" and villageid = '"+villageid.trim()+"'";
            }
            if(!"".equals(nameOrIdNumber) && null != nameOrIdNumber){
                sql += "  and (  name like '%"+nameOrIdNumber.trim()+"%' ";
            }
            if(!"".equals(nameOrIdNumber) && null != nameOrIdNumber){
                sql += " or idnumber like '%"+nameOrIdNumber.trim()+"%' ";
            }
        sql += " )";
        if(null != nameOrIdNumber && !"".equals(nameOrIdNumber.trim())){
            list = this.ownerDao.executeNativeQuerySql(sql, "0",null);
        }

        return  list;
    };
    @Override
    public  List getSelectHouseOwner(String villageid,String houseno) throws Exception{
        String sql = "select * from T_OWNER_19 where 1=1 and delflag = ? and villageid = ? and IDNUMBER in (select ownerid from t_houseowner_19 where houseid = ?)";
        return this.ownerDao.executeNativeQuerySql(sql, "0", villageid, houseno);
    };

    @Override
    public List saveUpdate(InputStream in) throws Exception{
        ModelExcel modelExcel = new ModelExcel();
        JSONObject result = new JSONObject();
        Map<Integer, Map<Integer, Object>> content= modelExcel.readExcelContent(in);
        List<String> villageNoExit = this.villageDao.getList("villageid","delFlag",false,null,null,null);
        List houseNoExist = this.houseService.getList("houseno","delFlag",false,null,null,null);//存在的房屋编号
        List ownerIdExist = this.ownerDao.getList("idnumber","delFlag",false,null,null,null); //存在的身份证号
        List<Owner> owners = new ArrayList<>();//业主信息
        Set<String> idNumber = new HashSet<>();
        List<HouseOwner> houseOwners = new ArrayList<>(); //房屋业主关联信息
        List errorOwner = new ArrayList<>();
        for(int i = 1;i<content.size()+1;i++){
            Map<Integer, Object> aa = content.get(i);

            Boolean fla = false;
            if(null!= aa.get(1) && !"".equals(aa.get(1))){
                if(aa.get(1).toString().contains(",")){
                    String [] hous = aa.get(1).toString().split(",");
                    fla = true;
                    for(int m=0;m<hous.length;m++){
                        if(!houseNoExist.contains(hous[m])){
                            fla = false;
                            break;
                        }
                    }
                }else{
                  fla  =  houseNoExist.contains(aa.get(1));
                }
            }
            if( fla  && villageNoExit.contains(aa.get(0)) && !ownerIdExist.contains(aa.get(3)) && !idNumber.contains(aa.get(3)) && null!= aa.get(3) && !"".equals(aa.get(3).toString())){
                //存入houseowner 表中
                if(aa.get(1).toString().contains(",")){
                    String [] hous = aa.get(1).toString().split(",");
                    for(int n=0;n<hous.length;n++){
                        HouseOwner houseOwner = new HouseOwner();
                        houseOwner.setHouseid(hous[n].toString());
                        houseOwner.setOwnerid(aa.get(3).toString());
                        houseOwners.add(houseOwner);
                    }
                }else{
                    HouseOwner houseOwner = new HouseOwner();
                    houseOwner.setHouseid(aa.get(1).toString());
                    houseOwner.setOwnerid(aa.get(3).toString());
                    houseOwners.add(houseOwner);
                }

                Owner owner = new Owner();
                owner.setVillageid(aa.get(0).toString());
                owner.setHouseid(aa.get(1).toString());
                if(null != aa.get(2) && !"".equals(aa.get(2).toString())){
                    String keys = aa.get(2).toString().split("-")[1].toString();
                    Diction idtypeDiction = new Diction();
                    idtypeDiction.setKeys(keys);
                    owner.setIdtypeDiction(idtypeDiction);
                }
                owner.setIdnumber(aa.get(3).toString());
                owner.setName(aa.get(4).toString());
                if(null != aa.get(5) && !"".equals(aa.get(5).toString())){
                    String keys = aa.get(5).toString().split("-")[1].toString();
                    Diction sexDiction = new Diction();
                    sexDiction.setKeys(keys);
                    owner.setSexDiction(sexDiction);
                }
                owner.setBirthday(aa.get(6).toString());
                if(null != aa.get(7) && !"".equals(aa.get(7).toString())){
                    String keys = aa.get(7).toString().split("-")[1].toString();
                    Diction nationDiction = new Diction();
                    nationDiction.setKeys(keys);
                    owner.setNationDiction(nationDiction);
                }

                if(null != aa.get(8) && !"".equals(aa.get(8).toString())){
                    String keys = aa.get(8).toString().split("-")[1].toString();
                    Diction politicsstatus = new Diction();
                    politicsstatus.setKeys(keys);
                    owner.setPoliticsstatus(politicsstatus);
                }

                if(null != aa.get(9) && !"".equals(aa.get(9).toString())){
                    String keys = aa.get(9).toString().split("-")[1].toString();
                    Diction EducationStatus = new Diction();
                    EducationStatus.setKeys(keys);
                    owner.setEducationstatus(EducationStatus);
                }
                owner.setPhone(aa.get(10).toString());
                owner.setJob(aa.get(11).toString());
                owner.setWorkplace(aa.get(12).toString());
                owner.setIdaddr(aa.get(13).toString());
                if(null != aa.get(14) && !"".equals(aa.get(14).toString())){
                    String keys = aa.get(14).toString().split("-")[1].toString();
                    Diction identifyTypeDiction = new Diction();
                    identifyTypeDiction.setKeys(keys);
                    owner.setIdentifyTypeDiction(identifyTypeDiction);
                }
                if(null != aa.get(15) && !"".equals(aa.get(15).toString())){
                    String keys = aa.get(15).toString().split("-")[1].toString();
                    Diction identifyCountryDiction = new Diction();
                    identifyCountryDiction.setKeys(keys);
                    owner.setIdentifyCountryDiction(identifyCountryDiction);
                }
                idNumber.add(owner.getIdnumber());
                owners.add(owner);
//                System.out.println(aa);
            }else{
                Integer m = i+1;
                errorOwner.add("行号:"+m+"    "+aa.get(1) +": "+aa.get(3));
            };
        };
//        System.out.println(owners);
        this.ownerDao.batchSaveOrUpdate(owners);
        this.houseOwnerService.getBaseDao().batchSaveOrUpdate(houseOwners);
        return errorOwner;
    }

    @Override
    public Map<String, String> getOwnerInfO() {
        Map<String,String> map = new HashMap<>();
        List<Owner> list = ownerDao.findAll();
        if(list!=null && list.size()>0){
            for (Owner owner:list) {
                map.put(owner.getIdnumber(),owner.getName());
            }
        }
        return map;
    }
    @Override
    public JSONObject getPeoplePercent(String ownerLocation) throws Exception{
        JSONObject all = new JSONObject();
        String resident  = Configs.find(CfI.C_OWNER_RESIDENT_DICTION).getValue();
        Map peoplePercent = new HashMap<>();
            peoplePercent.put("name","[{name:'外来人口'},{name:'本地人口'},{name:'流动人口'},{name:'常住人口'},{name:'其他'}]");
            List peoplePercentValue = new ArrayList<>();
            int ownerNoLocationCount = 0;
            ownerNoLocationCount = this.ownerDao.queryCountSql("select count(substr(idnumber,1,4))  from t_owner_19  where 1=1 and substr(idnumber,1,4) != '"+ownerLocation+"' ",null);
            peoplePercentValue.add(ownerNoLocationCount);
            int ownerLocationCount = 0;
            ownerLocationCount = this.ownerDao.queryCountSql("select count(substr(idnumber,1,4))  from t_owner_19  where 1=1 and substr(idnumber,1,4) = '"+ownerLocation+"' ",null);
            peoplePercentValue.add(ownerLocationCount);
            int floatPeople = 0;
            peoplePercentValue.add(floatPeople);
            int roomPeople = 0;
            roomPeople = this.ownerDao.queryCountSql("select count(identify_type) from t_owner_19  where identify_type in  ("+resident+") ",null);
            peoplePercentValue.add(roomPeople);
            peoplePercentValue.add(0);
        peoplePercent.put("value",peoplePercentValue);
        Map peopleRoom = new HashMap<>();
        List xList = new ArrayList<>();
        List peopleList = new ArrayList<>();
        List houseList = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.set(Calendar.DATE,date.get(Calendar.DATE)-0);
        String one = DateTools.getDateStr(date.getTime());
        Date done = date.getTime();
        xList.add(one);

//        ownerDao.getSizeByHQL(new String[0],new Object[0]," and lastModifyTime <= ?",Arrays.asList(done));
//        peopleList.add(this.ownerDao.queryCountSql(" select count(lastModifyTime) from Owner where lastModifyTime <= ?",done));
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+one+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+one+"'",null));
        date.set(Calendar.DATE,date.get(Calendar.DATE)-1);
        String two = DateTools.getDateStr(date.getTime());
        xList.add(two);
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+two+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+two+"'",null));
        date.set(Calendar.DATE,date.get(Calendar.DATE)-1);
        String three = DateTools.getDateStr(date.getTime());
        xList.add(three);
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+three+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+three+"'",null));
        date.set(Calendar.DATE,date.get(Calendar.DATE)-1);
        String four = DateTools.getDateStr(date.getTime());
        xList.add(four);
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+four+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+four+"'",null));
        date.set(Calendar.DATE,date.get(Calendar.DATE)-1);
        String five = DateTools.getDateStr(date.getTime());
        xList.add(five);
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+five+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+five+"'",null));
        date.set(Calendar.DATE,date.get(Calendar.DATE)-1);
        String six = DateTools.getDateStr(date.getTime());
        xList.add(six);
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+six+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+six+"'",null));
        date.set(Calendar.DATE,date.get(Calendar.DATE)-1);
        String seven = DateTools.getDateStr(date.getTime());
        xList.add(seven);
        peopleList.add(this.ownerDao.queryCountSql(" select count(to_char(lastmodifytime,'yyyy-MM-dd')) from t_owner_19 where to_char(lastmodifytime,'yyyy-MM-dd') <='"+seven+"'",null));
        houseList.add(this.ownerDao.queryCountSql(" select count (distinct houseid) from (select a.lastmodifytime,b.houseid,a.delflag from t_owner_19 a inner join t_houseowner_19 b on a.idnumber = b.ownerid) where 1=1 and  to_char(lastmodifytime,'yyyy-MM-dd') < = '"+seven+"'",null));
        Collections.reverse(xList);
        Collections.reverse(peopleList);
        Collections.reverse(houseList);
        peopleRoom.put("xList",xList);
        JSONObject peopleJson = new JSONObject();
            peopleJson.put("name","人");
            peopleJson.put("data",peopleList);
        JSONObject houseJson = new JSONObject();
            houseJson.put("name","户");
            houseJson.put("data",houseList);
        List list = new ArrayList<>();
             list.add(peopleJson);
             list.add(houseJson);
        peopleRoom.put("list",list);
        Map popleAge = new HashMap<>();
        String woman = Configs.find(CfI.C_OWNER_WOMAN_DICTION).getValue();
        String man = Configs.find(CfI.C_OWNER_MAN_DICTION).getValue();
//        let labelData = ['80岁以上', '61-80岁', '41到60岁', '21到40岁', '0到20岁'];
//        let womanData = [180,100,150,300,120];
//        let manData = [-170,-100,-150,-300,-120];
        List labelData = new ArrayList<>();
        List womanData = new ArrayList<>();
        List manData = new ArrayList<>();
        labelData.add("80岁以上");
        Calendar dateAge1 = Calendar.getInstance();
        dateAge1.setTime(new Date());
        dateAge1.set(Calendar.YEAR,dateAge1.get(Calendar.YEAR)-80);
        String oneAge1 = DateTools.getDateStr(dateAge1.getTime());
        womanData.add(this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday < '"+oneAge1+"' and sex = '"+woman+"' ",null));
        manData.add("-"+this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday < '"+oneAge1+"' and sex = '"+man+"' ",null));
        labelData.add("61-80岁");
        Calendar dateAge2 = Calendar.getInstance();
        dateAge2.setTime(new Date());
        dateAge2.set(Calendar.YEAR,dateAge2.get(Calendar.YEAR)-61);
        String oneAge2 = DateTools.getDateStr(dateAge2.getTime());

        womanData.add(this.ownerDao.queryCountSql("select count(*) from t_owner_19 where  birthday >= '"+oneAge1+"' and  birthday <= '"+oneAge2+"' and sex = '"+woman+"' ",null));
        manData.add("-"+this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday >= '"+oneAge1+"' and  birthday <= '"+oneAge2+"' and sex = '"+man+"' ",null));
        labelData.add("41到60岁");
        Calendar dateAge3 = Calendar.getInstance();
        dateAge3.setTime(new Date());
        dateAge3.set(Calendar.YEAR,dateAge3.get(Calendar.YEAR)-41);
        String oneAge3 = DateTools.getDateStr(dateAge3.getTime());

        womanData.add(this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday > '"+oneAge2+"' and  birthday <= '"+oneAge3+"' and sex = '"+woman+"' ",null));
        manData.add("-"+this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday > '"+oneAge2+"' and  birthday <= '"+oneAge3+"' and sex = '"+man+"' ",null));
        labelData.add("21到40岁");

        Calendar dateAge4 = Calendar.getInstance();
        dateAge4.setTime(new Date());
        dateAge4.set(Calendar.YEAR,dateAge4.get(Calendar.YEAR)-21);
        String oneAge4 = DateTools.getDateStr(dateAge4.getTime());
        womanData.add(this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday > '"+oneAge3+"' and  birthday <= '"+oneAge4+"' and sex = '"+woman+"' ",null));
        manData.add("-"+this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday > '"+oneAge3+"' and  birthday <= '"+oneAge4+"' and sex = '"+man+"' ",null));
        labelData.add("0到20岁");

        womanData.add(this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday > '"+oneAge4+"'  and sex = '"+woman+"' ",null));
        manData.add("-"+this.ownerDao.queryCountSql("select count(*) from t_owner_19 where birthday > '"+oneAge4+"' and sex = '"+man+"' ",null));

        popleAge.put("labelData",labelData);
        popleAge.put("womanData",womanData);
        popleAge.put("manData",manData);

        all.put("peoplePercent",peoplePercent);
        all.put("peopleRoom",peopleRoom);
        all.put("opopleAge",popleAge);

        return all;
    }

    @Override
    public Integer getPeopleType(String idType) throws Exception{
        return this.ownerDao.queryCountSql("select count(identify_type) from t_owner_19  where identify_type in  ("+idType+") ",null);
    }

    @Override
    public Integer getOverseasPeopleCount() throws Exception{
        return this.ownerDao.queryCountSql("select count(*) from t_owner_19 where identify_country !='"+Configs.find(CfI.C_OWNER_CHINA_DICTION).getValue()+"' ",null);
    };
}
