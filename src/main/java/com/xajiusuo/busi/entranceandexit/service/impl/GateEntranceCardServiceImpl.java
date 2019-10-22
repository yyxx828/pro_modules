package com.xajiusuo.busi.entranceandexit.service.impl;

import com.xajiusuo.busi.buildingaccess.dao.BuildingEntranceCardDao;
import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceCard;
import com.xajiusuo.busi.entranceandexit.dao.GateEntranceCardDao;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceCard;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceCardService;
import com.xajiusuo.busi.entranceandexit.vo.AccessCardVo;
import com.xajiusuo.busi.villageMessage.service.OwnerService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.P;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
 * Created by wangdou on 2019/6/6
 */
@Service
public class GateEntranceCardServiceImpl extends BaseServiceImpl<GateEntranceCard, String> implements GateEntranceCardService {

    @Autowired
    private GateEntranceCardDao gateEntranceCardDao;
    @Autowired
    private OwnerService ownerService; //业主信息
    @Autowired
    private BuildingEntranceCardDao buildingEntranceCardDao;
    private SimpleDateFormat sdf = new SimpleDateFormat(P.S.fmtYmd11);

    @Override
    public BaseDao<GateEntranceCard, String> getBaseDao() {
        return gateEntranceCardDao;
    }

    @Override
    public Page<GateEntranceCard> query(GateEntranceCard gateEntranceCard, Pageable page) {
        StringBuilder hql = new StringBuilder("select * from " + tableName() + " where 1=1 ");
        if (!StringUtils.isEmpty(gateEntranceCard.getEntrancecardno())) {
            hql.append(" and ENTRANCECARDNO = '" + gateEntranceCard.getEntrancecardno() + "'");
        }
        return gateEntranceCardDao.executeQuerySqlByPage(page, hql.toString());
    }

    @Override
    public List uploadAccessCarExcel(InputStream in) throws Exception {
        ModelExcel modelExcel = new ModelExcel();
        Map<Integer, Map<Integer, Object>> content = modelExcel.readExcelContent(in);
        Map<String, String> onwerMap = ownerService.getOwnerInfO();  //获取业主信息
        List<GateEntranceCard> gateEntranceCardList = new ArrayList<>();
        List<BuildingEntranceCard> buildingEntranceCardList = new ArrayList<>();
        List<String> gateCardNoExit = this.gateEntranceCardDao.getList("entrancecardno", "delFlag", false, null, null, null);
        List<String> buildCardNoExit = this.buildingEntranceCardDao.getList("entrancecardno", "delFlag", false, null, null, null);
        List<String> errors = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Object>> entry : content.entrySet()) {
            int lineNum = entry.getKey();
            Map<Integer, Object> map = entry.getValue();
            if (!gateCardNoExit.contains(map.get(2).toString()) || !buildCardNoExit.contains(map.get(2).toString())) {
                AccessCardVo accessCardVo = new AccessCardVo();
                //小区编号不能为空
                String valliageId = map.get(0) != null && !"".equals(map.get(0)) ? map.get(0).toString() : "";
                if ("".equals(valliageId)) continue;
                //门禁卡不能为空
                String carId = map.get(2) != null && !"".equals(map.get(2)) ? map.get(2).toString() : "";
                if ("".equals(carId)) continue;
                //证件号码不能为空
                String carOwnerIdcard = map.get(6) != null && !"".equals(map.get(6)) ? map.get(6).toString() : "";
                if ("".equals(carOwnerIdcard)) continue;
                String carOwnerName = onwerMap.get(carOwnerIdcard); //业主姓名
                if (!"".equals(carOwnerName)) {
                    accessCardVo.setPersonname(carOwnerName);
                }
                if (!"".equals(carOwnerIdcard)) {
                    accessCardVo.setPersonidnumber(carOwnerIdcard);
                }
                if (!"".equals(valliageId)) {
                    accessCardVo.setVillageid(valliageId);
                }
                accessCardVo.setEntrancecardno(carId);
                accessCardVo.setEntrancecardtype(map.get(1) != null && !"".equals(map.get(1)) ? map.get(1).toString() : "");
                accessCardVo.setIsvalid(map.get(3) != null && !"".equals(map.get(3)) ? map.get(3).toString() : "");
                accessCardVo.setPasstime(sdf.parse(map.get(4) != null && !"".equals(map.get(4)) ? map.get(4).toString() : DateUtils.currentDatetime()));
                accessCardVo.setAuthscope(Integer.parseInt(map.get(5).toString()));
                if (0 == accessCardVo.getAuthscope()) {
                    GateEntranceCard gateEntranceCard = new GateEntranceCard();
                    BuildingEntranceCard buildingEntranceCard = new BuildingEntranceCard();
                    BeanUtils.copyProperties(accessCardVo, gateEntranceCard);
                    BeanUtils.copyProperties(accessCardVo, buildingEntranceCard);
                    gateEntranceCardList.add(gateEntranceCard);
                    buildingEntranceCardList.add(buildingEntranceCard);
                } else if (1 == accessCardVo.getAuthscope()) {
                    GateEntranceCard gateEntranceCard = new GateEntranceCard();
                    BeanUtils.copyProperties(accessCardVo, gateEntranceCard);
                    gateEntranceCardList.add(gateEntranceCard);
                } else {
                    BuildingEntranceCard buildingEntranceCard = new BuildingEntranceCard();
                    BeanUtils.copyProperties(accessCardVo, buildingEntranceCard);
                    buildingEntranceCardList.add(buildingEntranceCard);
                }
            } else {
                errors.add("行号:" + (lineNum + 1) + "    " + map.get(2));
            }
        }
        gateEntranceCardDao.batchSaveOrUpdate(gateEntranceCardList);
        buildingEntranceCardDao.batchSaveOrUpdate(buildingEntranceCardList);
        return errors;
    }
}
