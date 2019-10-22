package com.xajiusuo.busi.user.service.impl;

import com.xajiusuo.busi.user.dao.DepartDao;
import com.xajiusuo.busi.user.dao.DutyDao;
import com.xajiusuo.busi.user.dao.UserinfoDao;
import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.entity.Operate;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.MD5utils;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @Author zlm
 * @Date 2018/1/17
 * @Description 用户逻辑接口实现类
 */
@Service
@Transactional
public class UserinfoServiceImpl extends BaseServiceImpl<Userinfo, Integer> implements UserinfoService {
    @Autowired
    UserinfoDao userinfoRepository;

    @Autowired
    DepartDao departRepository;

    @Autowired
    DutyDao dutyRepository;

    @Override
    public BaseDao<Userinfo, Integer> getBaseDao() {
        return userinfoRepository;
    }

    @Override
    public Userinfo getUserInfoById(Integer id) {
        return userinfoRepository.findByIdAndDelFlag(id, false);
    }

    @Override
    public List<Userinfo> findUserinfosByDepartid(Integer departId) {
        return userinfoRepository.findByDepart_IdAndDelFlag(departId, false);
    }

    @Override
    public List<Userinfo> findByDelAndAble(Boolean del, Boolean able) {
        return userinfoRepository.findByDelFlagAndAble(del, able);
    }

    public static String like(String s) {
        return s != null && s.trim().length() >= 1 ? "'" + SqlUtils.sqlLike(s) + "'" : null;
    }

    @Override
    public Page<Userinfo> query(Userinfo userinfo, Pageable pageable, String sql) {
        StringBuffer hql = new StringBuffer("select e.* from " + userinfo.getClass().getSimpleName() + " e where 1 = 1 ");
        if (userinfo.getDelFlag() == null) {
            userinfo.setDelFlag(false);
        }
        hql.append(" and e.delflag = " + (userinfo.getDelFlag() ? "1" : "0"));
        if (userinfo.getAble() != null) {
            hql.append(" and e.able = " + (userinfo.getAble() ? "1" : "0"));
        }
        if (StringUtils.isNotBlank(userinfo.getUsername())) {
            hql.append(" and e.username like " + like(userinfo.getUsername()));
        }
        if (StringUtils.isNotBlank(userinfo.getFullname())) {
            hql.append(" and e.fullname like " + like(userinfo.getFullname()));
        }
        if (userinfo.getSex() != null) {
            hql.append(" and e.sex = " + (userinfo.getSex() ? "1" : "0"));
        }
        if (StringUtils.isNotBlank(userinfo.getCardId())) {
            hql.append(" and e.cardId like " + like(userinfo.getCardId()));
        }
        if (StringUtils.isNotBlank(userinfo.getPoliceNum())) {
            hql.append(" and e.policeNum like " + like(userinfo.getPoliceNum()));
        }
        if (userinfo.getUlevel() != null) {
            hql.append(" and e.ulevel = " + userinfo.getUlevel());
        }
        if (userinfo.getDepart() != null && userinfo.getDepart().getId() != null) {
            hql.append(" and e.departid = " + userinfo.getDepartId());
        }
        if (userinfo.getDuty() != null && userinfo.getDuty().getId() != null) {
            hql.append(" and e.dutyid = " + userinfo.getDutyId());
        }
        if (StringUtils.isNotBlank(sql)) {
            hql.append(sql);
        }
        if (pageable.getSort() == null) {
            hql.append(" order by e.id DESC");
        } else {
            hql.append(" order by ");
            Iterator<Order> it = pageable.getSort().iterator();
            while (it.hasNext()) {
                Order o = it.next();
                hql.append("e." + o.getProperty());
                if (o.getDirection() != null) {
                    hql.append(o.getDirection().isAscending() ? " ASC" : " DESC");
                }
                hql.append(",");
            }
            hql.deleteCharAt(hql.length() - 1);
        }
        return userinfoRepository.executeQuerySqlByPage(pageable, hql.toString(), null);
    }

    @Override
    public Page<Userinfo> queryByAllDepart(Userinfo userinfo, Pageable pageable, String sql) {
        StringBuffer hql = new StringBuffer("select e.* from "+ tableName() +" e where 1 = 1 ");
        if (userinfo.getDelFlag() == null) {
            userinfo.setDelFlag(false);
        }
        if (userinfo.getAble() != null) {
            hql.append(" and e.able = " + (userinfo.getAble() ? "1" : "0"));
        }
        if (StringUtils.isNotBlank(userinfo.getUsername())) {
            hql.append(" and e.username like " + like(userinfo.getUsername()));
        }
        if (StringUtils.isNotBlank(userinfo.getFullname())) {
            hql.append(" and e.fullname like " + like(userinfo.getFullname()));
        }
        if (userinfo.getSex() != null) {
            hql.append(" and e.sex = " + (userinfo.getSex() ? "1" : "0"));
        }
        if (StringUtils.isNotBlank(userinfo.getCardId())) {
            hql.append(" and e.cardId like " + like(userinfo.getCardId()));
        }
        if (StringUtils.isNotBlank(userinfo.getPoliceNum())) {
            hql.append(" and e.policeNum like " + like(userinfo.getPoliceNum()));
        }
        if (userinfo.getUlevel() != null) {
            hql.append(" and e.ulevel = " + userinfo.getUlevel());
        }
        if (userinfo.getDepart() != null && userinfo.getDepart().getId() != null) {
            hql.append(" and e.departid in (SELECT d.ID FROM I_DEPART_11 d START WITH d.ID = " + userinfo.getDepartId() + " CONNECT BY d.PARENTID = PRIOR id) ");
        }
        if (userinfo.getDuty() != null && userinfo.getDuty().getId() != null) {
            hql.append(" and e.dutyid = " + userinfo.getDutyId());
        }
        if (StringUtils.isNotBlank(sql)) {
            hql.append(sql);
        }
        return userinfoRepository.executeQuerySqlByPage(pageable, hql.toString(), null);
    }

    @Override
    public List<Userinfo> query(Userinfo userinfo, String sql) {
        StringBuffer hql = new StringBuffer("select e.* from " + userinfo.getClass().getSimpleName() + " e where 1 = 1");
        if (userinfo.getDelFlag() == null) {
            userinfo.setDelFlag(false);
        }
        hql.append(" and e.delflag = " + (userinfo.getDelFlag() ? "1" : "0"));
        if (StringUtils.isNotBlank(userinfo.getUsername())) {
            hql.append(" and e.username like " + like(userinfo.getUsername()));
        }
        if (StringUtils.isNotBlank(userinfo.getFullname())) {
            hql.append(" and e.fullname like " + like(userinfo.getFullname()));
        }
        if (userinfo.getSex() != null) {
            hql.append(" and e.sex = " + (userinfo.getSex() ? "1" : "0"));
        }
        if (StringUtils.isNotBlank(userinfo.getCardId())) {
            hql.append(" and e.cardId like " + like(userinfo.getCardId()));
        }
        if (StringUtils.isNotBlank(userinfo.getPoliceNum())) {
            hql.append(" and e.policeNum like " + like(userinfo.getPoliceNum()));
        }
        if (userinfo.getUlevel() != null) {
            hql.append(" and e.ulevel = " + userinfo.getUlevel());
        }
        if (userinfo.getAble() != null) {
            hql.append(" and e.able = " + (userinfo.getAble() ? "1" : "0"));
        }
        if (userinfo.getDepart() != null && userinfo.getDepart().getId() != null) {
            hql.append(" and e.departid = " + userinfo.getDepartId());
        }
        if (userinfo.getDuty() != null && userinfo.getDuty().getId() != null) {
            hql.append(" and e.dutyid = " + userinfo.getDutyId());
        }
        if (StringUtils.isNotBlank(sql)) {
            hql.append(sql);
        }
        hql.append(" order by e.lastModifyTime DESC");
        return userinfoRepository.executeNativeQuerySql(hql.toString());
    }

    @Override
    public List<Userinfo> findGeOptTime(Date optTime) {
        List<Userinfo> users;
        users = userinfoRepository.findByLastModifyTimeGreaterThanEqual(optTime);
        return users;
    }

    @Override
    public int importUser(Sheet sheet, HttpServletRequest request) {
        int totalRows = sheet.getPhysicalNumberOfRows();
        int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        List<Userinfo> users = new ArrayList<Userinfo>();
        for (int i = 1; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else if (row.getLastCellNum() < totalCells) {
                row.getCell(0).setCellValue("此行记录不完整");
                continue;
            }
            Userinfo userinfo = rowToUser(row);
            userinfo.setCreateUID(getCurrentUser(request).getId());
            userinfo.setLastModifyUID(getCurrentUser(request).getId());
            if (userinfo == null) {
                continue;
            }
            users.add(userinfo);
        }
        userinfoRepository.batchSaveOrUpdate(users);
        int count = users != null ? users.size() : 0;
        users.clear();
        return count;
    }

    @Override
    public Userinfo rowToUser(Row row) {
        try {
            row.createCell(0);
            Userinfo userinfo = new Userinfo();
            String username = row.getCell(1).getStringCellValue();
            String policeNum = row.getCell(5).getStringCellValue();
            String cardId = row.getCell(4).getStringCellValue();
            if (isExist("username", username)) {
                row.getCell(0).setCellValue(Result.find(CfI.R_USER_USERNAMEEXIST_FAIL).getMsg());
                return null;
            }
            if (isExist("cardId", cardId)) {
                row.getCell(0).setCellValue(Result.find(CfI.R_USER_CARDIDEXIST_FAIL).getMsg());
                return null;
            }
            userinfo.setUsername(row.getCell(1).getStringCellValue());
            userinfo.setFullname(row.getCell(2).getStringCellValue());
            Boolean sex = true;
            if (StringUtils.isNotBlank(row.getCell(3).getStringCellValue())) {
                if (row.getCell(3).getStringCellValue().equals("女")) {
                    sex = false;
                }
            }
            userinfo.setSex(sex);
            userinfo.setPoliceNum(row.getCell(4).getStringCellValue());
            userinfo.setCardId(row.getCell(5).getStringCellValue());
            userinfo.setPassword(MD5utils.getMd5("123456"));
            if (row.getCell(6) != null) {
                userinfo.setHiredate(row.getCell(6).getDateCellValue());
            }
            String departName = row.getCell(7).getStringCellValue();
            String dutyName = row.getCell(8).getStringCellValue();
            userinfo.setUlevel(3);
            userinfo.setAble(true);
//            userinfo.setCreateUID(getCurrentUser().getId());
//            userinfo.setLastModifyUID(getCurrentUser().getId());
            userinfo.setDelFlag(false);
            List<Depart> departs = departRepository.findByDname(departName);
            if (CommonUtils.isNotEmpty(departs)) {
                userinfo.setDepart(departs.get(0));
            } else {
                row.getCell(0).setCellValue(Result.find(CfI.R_DEPART_DEPART_NOTEXIST_FAIL).getMsg());
                return null;
            }
            List<Duty> duties = dutyRepository.findByDutyname(dutyName);
            if (CommonUtils.isNotEmpty(duties)) {
                userinfo.setDuty(duties.get(0));
            } else {
                row.getCell(0).setCellValue(row.getCell(0).getStringCellValue() + Result.find(CfI.R_DUTY_NOTEXIST_FAIL).getMsg());
                return null;
            }
            return userinfo;
        } catch (Exception e) {
            e.printStackTrace();
            row.getCell(0).setCellValue(row.getCell(0).getStringCellValue() + NestedExceptionUtils.getRootCause(e));
            return null;
        }
    }

    @Override
    public Boolean isExist(String name, Object value) {
        return isExist(name, value, null);
    }

    @Override
    public Boolean isExist(String name, Object value, Integer id) {
        Assert.notNull(value, "参数不能为空");
        return userinfoRepository.findSame(name,value,id,null);
    }

    public static Specification<Userinfo> specEq(final String fieldName, final Object value, final Integer id) {
        return new Specification<Userinfo>() {
            @Override
            public Predicate toPredicate(Root<Userinfo> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (StringUtils.isNotBlank(fieldName) && value != null) {
                    Path exp = root.get(fieldName);
                    if (id != null) {
                        Path cid = root.get("id");
                        return builder.and(builder.equal(exp, value), builder.notEqual(cid, id));
                    }
                    return builder.and(builder.equal(exp, value));
                } else {
                    return builder.conjunction();
                }
            }
        };
    }

    @Override
    public Workbook listToExcel(List<Userinfo> list) {
        List<String> head = Arrays.asList("用户名称,姓名,性别,警号,身份证,机构,职位,生日,入职日期");
        List<String> field = Arrays.asList("username", "fullname", "sexName", "policeNum", "cardId", "departName", "dutyName", "birthday", "hiredate");
        Workbook book = ExcelUtils.listToExcel(list, head, field, "用户", 0, false);
        return book;
    }


    @Override
    public Userinfo getCurrentUser(HttpServletRequest request) {
        Userinfo user = (Userinfo) getCurrentInfo(request).get("user");
        return user;
    }

    @Override
    public Map<String, Object> getCurrentInfo(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<String, Object>();
        Userinfo user = new Userinfo();
        String copyright = "";
        Object casAssertion = request.getSession().getAttribute("_const_cas_assertion_");
        Assertion assertion = (Assertion) casAssertion;
        if (assertion != null) {
            AttributePrincipal attributePrincipal = assertion.getPrincipal();
            Map<String, Object> cas_map = attributePrincipal.getAttributes();
            Integer id = Integer.parseInt(cas_map.get("id").toString());
            copyright = String.valueOf(cas_map.get("copyright"));
            user = userinfoRepository.getOne(id);
            //获得权限组操作编码集合
            Set<String> os = new HashSet<String>();
            if (user.getOperGroup() != null) {
                if (user.getOperGroup().getOperateSet() != null && user.getOperGroup().getOperateSet().size() > 0) {
                    for (Operate o : user.getOperGroup().getOperateSet()) {
                        os.add(o.getOcode());
                    }
                }
            }
            user.setOcodeList(os);
        }
        data.put("user", user);
        data.put("copyright", copyright);
        return data;
    }

    @Override
    public Userinfo findByUsernameAndPwd(String username, String pwd) {
        return userinfoRepository.findByUsernameAndPassword(username, pwd);
    }

    @Override
    public Userinfo findByCardId(String cardId) {
        return userinfoRepository.findByCardIdAndDelFlagAndAble(cardId, false, true);
    }
}
