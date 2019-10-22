package com.xajiusuo.busi.sync.execute;

import com.alibaba.druid.pool.DruidDataSource;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.sync.entity.InDataLog;
import com.xajiusuo.busi.sync.entity.SyncConf;
import com.xajiusuo.busi.sync.service.InDataLogService;
import com.xajiusuo.busi.sync.service.SyncConfService;
import com.xajiusuo.busi.sync.vo.ColVo;
import com.xajiusuo.busi.sync.vo.SyncConfFactory;
import com.xajiusuo.jpa.config.DataSourceConfig;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.utils.CfI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;

/**
 * 数据同步程序
 * Created by 杨勇 on 2019/7/2.
 */
@Component
@Slf4j
public class DataSyncRun {

    @Autowired
    private SyncConfService syncConfService;

    @Autowired
    private InDataLogService inDataLogService;

    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    /***
     * 定时数据同步
     */
    @Scheduled(cron = "50 */1 * * * ?")
    public void execute() throws Exception {
        if(!Configs.find(CfI.C_DATASOURCE_SECOND_RUN).getBoolean()){//是否启动同步功能
            return;
        }

        JdbcTemplate outJdbcTemplate = new JdbcTemplate(getDataSource());//获取连接
        Connection conn = null;
        try{
            conn = outJdbcTemplate.getDataSource().getConnection();
            sync(outJdbcTemplate);//数据同步
        }catch (Exception e){
            log.info("外表连接异常无法,无法进行同步:" + e.getMessage());
        }finally {
            close(conn);//关闭连接
        }
    }

    private void sync(JdbcTemplate outJdbcTemplate){
        SyncConfFactory.setJdbc(outJdbcTemplate,jdbcTemplate);

        List<SyncConf> list = syncConfService.getAll();
        for(SyncConf sc:list){
            if(sc.getDelFlag() || !sc.getEnabled()) {
                continue;
            }
            InDataLog inlog = new InDataLog();
            inlog.numinit(sc.getId());
            try{
                sync0(sc,outJdbcTemplate,inlog);
            }catch (Exception e){
                String msg = "同步错误信息:" + e.getMessage();
                inlog.setErrInfo(msg.substring(0,msg.length() > 400 ? 400 : msg.length()));
                log.info(msg);
            }finally {
                if(inlog.getAllNum() != null && inlog.getAllNum() > 0){
                    inDataLogService.saveOrUpdate(inlog);
                }
            }
        }
    }

    private void sync0(SyncConf sc, JdbcTemplate outJdbcTemplate, InDataLog inlog){
        SyncConfFactory dc = SyncConfFactory.get(sc);
        //获取被导入的最新时间

        SqlRowSet rs =  jdbcTemplate.queryForRowSet(MessageFormat.format("select max({0}) from {1}",sc.getInDeduplicate(),sc.getInTable()));
        Object val = null;
        if(rs.next()){
            val = rs.getObject(1);
        }
        //读取增量数据
        SqlRowSet rs1;
        outJdbcTemplate.setMaxRows(50000);
        if(val != null){
            rs1 =  outJdbcTemplate.queryForRowSet(MessageFormat.format("select * from {0} where {1} > ? {2} order by {1} asc",sc.getOutTable(),sc.getDeduplicate(),sc.getPlugSql() == null ? "" : sc.getPlugSql()),val);
        }else{
            rs1 =  outJdbcTemplate.queryForRowSet(MessageFormat.format("select * from {0} where 1=1 {1} order by {2} asc",sc.getOutTable(),sc.getPlugSql() == null ? "" : sc.getPlugSql(),sc.getDeduplicate()));
        }
        //增量读取记录 同步导入 对导入进行日志记录
        List<Object> list = Arrays.asList(new Object[dc.listCol().size()]);
        int index = 0;
        List<Object[]> params = new ArrayList<Object[]>();
        boolean imports = false;
        while(rs1.next()){
            inlog.addAllNum();
            index = 0;
            for(ColVo v:dc.listCol()){
                val =  v.toVal(rs1.getObject(v.getFromColName()));
                if(val == null && SyncConfFactory.initFields.contains(v.getFromColName())){
                    if(SyncConfFactory.initFields.indexOf(v.getFromColName()) < 2){
                        val = new Timestamp(System.currentTimeMillis());
                    }else{
                        val = false;
                    }
                }
                list.set(index++,val);
            }
            params.add(list.toArray());
            if(params.size() >= 1000){
                try{
                    jdbcTemplate.batchUpdate(dc.insertSql(),params);
                    imports = true;
                    inlog.setSuccessNum(inlog.getSuccessNum() + params.size());
                }catch (Exception e1){
                    if(StringUtils.isBlank(inlog.getErrInfo())){
                        inlog.setErrInfo("导入异常,信息:" + e1.getMessage());
                    }
                    inlog.setErrNum(inlog.getErrNum() + params.size());
                }
                params.clear();
            }
        }
        //进行保存
        if(params.size() > 0){
            try{
                jdbcTemplate.batchUpdate(dc.insertSql(),params);
                imports = true;
                inlog.setSuccessNum(inlog.getSuccessNum() + params.size());
            }catch (Exception e1){
                inlog.setErrNum(inlog.getErrNum() + params.size());
            }
            params.clear();
        }
        if(imports && StringUtils.isBlank(inlog.getErrInfo())){
            inlog.setErrInfo("导入成功");
        }
    }

    private static DruidDataSource dataSource;

    public DataSource getDataSource(){
        if(dataSource != null && Configs.find(CfI.C_DATASOURCE_SECOND_DRIVER).getValue().equals(dataSource.getDriverClassName()) &&Configs.find(CfI.C_DATASOURCE_SECOND_URL).getValue().equals(dataSource.getUrl()) &&Configs.find(CfI.C_DATASOURCE_SECOND_USERNAME).getValue().equals(dataSource.getUsername()) &&Configs.find(CfI.C_DATASOURCE_SECOND_PASSWORD).getValue().equals(dataSource.getPassword())){
            return dataSource;
        }
        dataSource = dataSourceConfig.getDruidDataSource(Configs.find(CfI.C_DATASOURCE_SECOND_DRIVER).getValue(),Configs.find(CfI.C_DATASOURCE_SECOND_URL).getValue(),Configs.find(CfI.C_DATASOURCE_SECOND_USERNAME).getValue(),Configs.find(CfI.C_DATASOURCE_SECOND_PASSWORD).getValue(),2);
        SyncConfFactory.removeAll();
        return dataSource;
    }

    public void close(Connection conn){
        try {
            if(conn != null && !conn.isClosed())
            conn.close();
        } catch (Exception e) {
        }
    }

}