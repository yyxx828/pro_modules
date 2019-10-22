package com.xajiusuo.busi.user.service.impl;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.busi.user.dao.OperateDao;
import com.xajiusuo.busi.user.service.OperateService;
import com.xajiusuo.busi.user.entity.Operate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zlm 2018/1/18
 *         角色逻辑接口实现类
 */
@Service
public class OperateServiceImpl extends BaseServiceImpl<Operate, Integer> implements OperateService {

    @Autowired
    OperateDao operateRepository;


    @Override
    public BaseDao<Operate, Integer> getBaseDao() {
        return operateRepository;
    }


    @Override
    public Page<Operate> query(Operate entity, Pageable page) {

        StringBuffer hql = new StringBuffer("select e.* from S_OPERATE_11 e where 1 = 1");

        if (StringUtils.isNotBlank(entity.getOcode())) {
            hql.append(" and e.ocode like '" + SqlUtils.sqlLike(entity.getOcode()) + "'");
        }
        if (StringUtils.isNotBlank(entity.getTypeName())) {
            hql.append(" and e.typeName like '" + SqlUtils.sqlLike(entity.getTypeName()) + "'");
        }
        if (StringUtils.isNotBlank(entity.getOdesc())) {
            hql.append(" and e.odesc like '" + SqlUtils.sqlLike(entity.getOdesc()) + "'");
        }
        if (StringUtils.isNotBlank(entity.getOname())) {
            hql.append(" and e.oname like '" + SqlUtils.sqlLike(entity.getOname()) + "'");
        }
        if (StringUtils.isNotBlank(entity.getOurl())) {
            hql.append(" and e.ourl like '" + SqlUtils.sqlLike(entity.getOurl()) + "'");
        }
        /*if(page.getSort() == null){*/
        hql.append(" order by e.lastModifyUID DESC");
        /*}else{
            hql.append(" order by ");
            Iterator<Sort.Order> it = page.getSort().iterator();
            while(it.hasNext()){
                Sort.Order o = it.next();
                hql.append("e."+o.getProperty());
                if(o.getDirection() != null){
                    hql.append(o.getDirection().isAscending() ? " ASC" : " DESC");
                }
                hql.append(",");
            }
            hql.deleteCharAt(hql.length() - 1);
        }*/

        return operateRepository.executeQuerySqlByPage(page, hql.toString());
    }

    @Override
    public List<String> getOperTypenameList() {
        List os = operateRepository.listBySQL("select e.typeName from S_OPERATE_11 e group by e.typeName");
        if (os.contains(null)) {
            os.remove(null);
            os.add("其它");
        }
        return os;
    }

    @Override
    public Map<String, List<Operate>> operaterByGroupList(String typeName) {
        List<Operate> list = null;
        if (StringUtils.isNotBlank(typeName)) {
            list = findBy("typeName", typeName);
        } else {
            list = getAll();
        }
        Map<String, List<Operate>> map = new HashMap<String, List<Operate>>();
        for (Operate o : list) {
            if (o.getTypeName() == null) {
                o.setTypeName("其它");
            }
            List<Operate> os = map.get(o.getTypeName());
            if (os == null) {
                os = new ArrayList<Operate>();
                map.put(o.getTypeName(), os);
            }
            os.add(o);
        }
        return map;
    }
}
