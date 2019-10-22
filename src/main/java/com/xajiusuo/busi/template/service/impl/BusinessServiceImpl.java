package com.xajiusuo.busi.template.service.impl;

import com.xajiusuo.busi.template.dao.BusFieldDao;
import com.xajiusuo.busi.template.dao.BusinessDao;
import com.xajiusuo.busi.template.data.BusiProper;
import com.xajiusuo.busi.template.entity.BusField;
import com.xajiusuo.busi.template.entity.Business;
import com.xajiusuo.busi.template.service.BusinessService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.config.PropertiesConfig;
import com.xajiusuo.jpa.config.TableEntity;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.MD5FileUtil;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Map;


@Service
public class BusinessServiceImpl extends BaseServiceImpl<Business, Integer> implements BusinessService {

    @Autowired
    private BusinessDao entityRepository;

    @Autowired
    private BusFieldDao busFieldRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public BaseDao<Business, Integer> getBaseDao() {
        return entityRepository;
    }

    @Override
    public Page<Business> findPageByEntity(Pageable pageable, Business entity) {
        String sql = BeanUtils.beanToSql(entity);
        sql += " order by " + pageable.getSort().toString().replaceAll(":", "");
        return entityRepository.executeQuerySqlByPage(pageable,sql);
    }

    @Override
    public synchronized Business autoSaveByName(String name, HttpServletRequest request) {
        UserInfoVo user = UserUtil.getCurrentUser(request);
        Business entity = null;
        try{
            int size = findBy("tcode",name).size();
            if(size > 0){
                return null;
            }
            Class<Object> tn = BeanUtils.getEntity (name);
            if(tn == null){
                //寄递表
                String desc = BusiProper.getExpressMap().get(name);
                if(desc != null){

                    entity = new Business();
                    entity.setTcode(name);
                    entity.setTdesc(desc);
                    entity.setUseType(false);
                    entity.setErrorSkip(true);

                    save(entity);

                    //寄递业务表存在
                    Map<String, String> map = BusiProper.getProper(name);

                    int index = 1;
                    for(String s:BusiProper.getKeys(name)){
                        BusField bf = new BusField();
                        bf.setOrders(index++);
                        bf.setFromType(1);
                        bf.setBusiness(entity);
                        bf.setfCol(s);
                        bf.setFproper(map.get(s));
                        bf.setUser(user);
                        busFieldRepository.save(bf);
                    }
                }
                return entity;
            }

            ApiModel am = tn.getAnnotation(ApiModel.class);
            Table table = tn.getAnnotation(Table.class);

            entity = new Business();
            entity.setTcode(name);
            entity.setTname(table.name().toLowerCase());
            entity.setTdesc(am.description());
            entity.setErrorSkip(true);
            entity.setUseType(true);

            save(entity);

            //初始化表的列
            String sf1 = "select * from {0} where rownum <= 1";//todo 查询语句

            SqlRowSet qrs = jdbcTemplate.queryForRowSet(MessageFormat.format(sf1,table.name()));
            SqlRowSetMetaData md = qrs.getMetaData();

            Map<String,String> busiMap = BusiProper.getProper(name);

            boolean isEntity = false;
            String skip = "";//无继承 TableEntity

            if(TableEntity.class.isAssignableFrom(tn)){
                skip = ",id,createuid,createname,lastmodifyuid,createtime,lastmodifytime,";
            }

            int index = 1;
            for(int i =1;i<=md.getColumnCount();i++){
                String col = md.getColumnName(i).toLowerCase();
                String type = md.getColumnTypeName(i);
//                getColumnClassName
                if(skip.contains("," +  col + ",")){
                    continue;
                }
                BusField bf = new BusField();
                bf.setOrders(index++);
                bf.setFromType(1);
                bf.setBusiness(entity);
                bf.setfCol(col);
                if(busiMap != null){
                    bf.setFproper(busiMap.get(col));
                }else{
                    bf.setFproper(col);
                }
                bf.setfType(type);
                bf.setUser(user);
                busFieldRepository.save(bf);
            }
        }catch (Exception e){
            return  null;
        }
        return entity;
    }
}
