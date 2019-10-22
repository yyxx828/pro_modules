package com.xajiusuo.busi.sync.vo;

import com.alibaba.fastjson.JSONArray;
import com.xajiusuo.busi.sync.entity.SyncConf;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by 杨勇 on 19-6-28.
 * 数据表属性转换
 */
@Component
public class SyncConfFactory {

    private SyncConf dic;

    private static JdbcTemplate outjdbcTemplate;
    private static JdbcTemplate injdbcTemplate;

    private static final String sql_com = "select * from {0} where {1} is null and {1} is not null";

    private SyncConfFactory(){
    }

    //表配置容器
    private static Map<SyncConf, SyncConfFactory> map = new HashMap<SyncConf, SyncConfFactory>();

    //数据来源表字段管理
    private Map<String,ColVo> fromTable;

    //数据写入表字段管理
    private Map<String,ColVo> toTable;

    //数据来源临时待比较字段
    private Map<String,ColVo> tempTable;

    private List<ColVo> list = null;
    private String insertSql = null;

    private List<String> initField = Arrays.asList("createtime","lastmodifytime","delflag");

    public final static List<String> initFields = Collections.unmodifiableList(Arrays.asList("createtime","lastmodifytime","delflag"));

    public static void setJdbc(JdbcTemplate outJdbc,JdbcTemplate inJdbc){
        outjdbcTemplate = outJdbc;
        injdbcTemplate = inJdbc;
    }

    //通过基础配置获取转换工具
    public static SyncConfFactory get(SyncConf dic){
        SyncConfFactory dc = map.get(dic);
        if(dc != null){//如果有直接读取
            return dc;
        }

        dc = new SyncConfFactory();
        dc.dic = dic;
        dc.fromTable = new HashMap<>();
        dc.toTable = new HashMap<>();
        dc.tempTable = new HashMap<>();

        //通过配置获取初始配置条件
        JSONArray arr = dic.getRules();
        for(Object o : arr){
            ColVo cv = toCol((Map<String, String>) o);
            dc.fromTable.put(cv.getFromColName(),cv);
            dc.toTable.put(cv.getToColName(),cv);
        }

        //增加外表字段
        SqlRowSet qrs = outjdbcTemplate.queryForRowSet(MessageFormat.format(sql_com,dic.getOutTable(),dic.getDeduplicate()));
        SqlRowSetMetaData md = qrs.getMetaData();
        for(int i = 1;i<=md.getColumnCount();i++){
            dc.addFrom(md.getColumnName(i).toLowerCase(),md.getColumnClassName(i));
        }

        //增加内表字段 P_DICTION_TEST_11
        qrs = injdbcTemplate.queryForRowSet(MessageFormat.format(sql_com,dic.getInTable(),dic.getDeduplicate()));
        md = qrs.getMetaData();
        for(int i = 1;i<=md.getColumnCount();i++){
            dc.addTo(md.getColumnName(i).toLowerCase(),md.getColumnClassName(i));
        }
        //整理
        dc.finsh();

        map.put(dic,dc);
        return dc;
    }

    public String insertSql(){
        if(insertSql != null){
            return insertSql;
        }
        StringBuilder fieds = new StringBuilder("");
        StringBuilder vals = new StringBuilder("");
        for(ColVo c:listCol()){
            if(fieds.length() != 0){
                fieds.append(",");
                vals.append(",");
            }
            fieds.append(c.getToColName());
            vals.append("?");
        }
        for(String s:initFields){
            if(initField.contains(s)) continue;
            fieds.append(s);
            vals.append("?");
        }
        insertSql = "Insert into " + dic.getInTable() + " (" + fieds.toString() + ") values (" + vals.toString() + ")";
        return insertSql;
    }

    public List<ColVo> listCol(){
        if(list == null || list.size() == 0){
            list = new ArrayList<>(toTable.values());
        }
        return list;
    }

    //首先增加要读取数据的表
    public SyncConfFactory addFrom(String field, String clazz){
        ColVo d =  fromTable.get(field);
        if(d != null){
            d.setFromColType(clazz);
        }else{
            tempTable.put(field,new ColVo().setFrom(field,clazz));
        }
        return this;
    }

    //在addFrom执行完后执行要导入表的字段
    public SyncConfFactory addTo(String field, String clazz){
        ColVo d =  toTable.get(field);
        if(d != null){
            d.setTo(field,clazz);
        }else  {
            d = tempTable.remove(field);
            if(d != null){
                d.setTo(field,clazz);
                toTable.put(field,d);
                fromTable.put(field,d);
            }else{
                initField.remove(field.toLowerCase());
            }
        }
        return this;
    }

    //最终整理
    public void finsh(){
        tempTable.clear();
        fromTable.clear();
        for(String key:new ArrayList<>(toTable.keySet())){
            ColVo cv =  toTable.get(key);
            if(cv== null) continue;
            if(cv.getFromColType() == null || cv.getToColType() == null){
                toTable.remove(key);
            }
        }
    }

    public List<String> getInitField(){
        return initField;
    }

    public static void remove(SyncConf dic){
        map.remove(dic);
    }

    public static void removeAll(){
        map.clear();
    }

    private static ColVo toCol(Map<String,String> m){
        return new ColVo(m.get("f").toLowerCase().trim(),m.get("t").toLowerCase().trim());
    }

}
