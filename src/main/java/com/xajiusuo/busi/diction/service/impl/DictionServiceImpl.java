package com.xajiusuo.busi.diction.service.impl;

import com.xajiusuo.busi.diction.dao.DictionDao;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.utils.CfI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 杨勇 on 18-8-20.
 */
@Service
public class DictionServiceImpl extends BaseServiceImpl<Diction, Integer> implements DictionService {


    private static Map<String,String> map = new HashMap<String,String>();

    @Autowired
    private DictionDao dictionRepository;


    @Override
    public BaseDao<Diction, Integer> getBaseDao() {
        return dictionRepository;
    }

    @Override
    public Page<Diction> baseDictions(Pageable pageable, Integer pid) {
        StringBuilder sql = new StringBuilder(MessageFormat.format("select * from {0} e where pid {1}", tableName(),pid == null ? " is null" : (" = " + pid)));
        return dictionRepository.executeQuerySqlByPage(pageable, sql.toString());
    }

    @Override
    public Result saveDiction(Diction entity) {
        String keys = null;
        int level = 1;
        if(entity.getPid() != null){
            String pk = dictionRepository.getOne(entity.getPid()).getKeys();
            keys = pk + "01";

            String sql = MessageFormat.format("select * from {0} e where pid = ? order by e.keys desc", tableName());
            List<Diction> list = dictionRepository.executeNativeQuerySql(sql, entity.getPid());
            if(list != null && list.size() > 0){
                Diction d = list.get(0);
                list.clear();
                Integer key = Integer.parseInt(d.getKeys().substring(3)) + 1;
                if(key >= 100){
                    return Result.find(CfI.R_DICTION_DICTIONMULTI_FAIL);
                }
                keys = pk + (key < 10 ? ("0" + key) : key);
            }
            level = 2;
        }else{
            keys = "k01";
            String sql = MessageFormat.format( "select * from {0} e where pid is null order by e.keys desc", tableName());
            List<Diction> list = dictionRepository.executeNativeQuerySql(sql);
            if(list != null && list.size() > 0){
                Diction d = list.get(0);
                list.clear();
                Integer key = Integer.parseInt(d.getKeys().substring(1)) + 1;
                if(key >= 100){
                    return Result.find(CfI.R_DICTION_DICTIONMULTI_FAIL);
                }
                keys = "k" + (key < 10 ? ("0" + key) : key);
            }
        }

        String sql = MessageFormat.format( "select * from {0} e where keys = ?", tableName());
        if(getSize("keys",keys,null,null) > 0){
            return Result.find(CfI.R_DICTION_DICTIONHASERRORS_FAIL);
        }
        entity.setKeys(keys);
        entity.setDlevel(level);
        dictionRepository.save(entity);
        return Result.SAVE_SUCCESS.setData(entity);
    }

    @Override
    public void synDics(Map<String, String> dicSet) {
        List<Diction> ds = dictionRepository.findAll();
        Map<String,Integer> parent = new HashMap<String,Integer>();
        for(Diction d:ds){
            dicSet.remove(d.getKeys());
            if(d.getDlevel() != null && d.getDlevel() == 1){
                parent.put(d.getKeys(),d.getId());
            }
        }
        for(String s:dicSet.keySet()){
            if(s.length() < 4){
                String[] ss = dicSet.get(s).split("\t");
                Diction d = new Diction();
                d.setDlevel(1);
                d.setKeys(ss[1]);
                d.setVal(ss[2]);
                d.setDescs(ss[3]);
                dictionRepository.save(d);
                if(parent.get(d.getKeys()) == null){
                    parent.put(d.getKeys(),d.getId());
                }
            }
        }
        for(String s:dicSet.keySet()){
            if(s.length() > 4){
                String[] ss = dicSet.get(s).split("\t");
                Diction d = new Diction();
                d.setDlevel(2);
                d.setPid(parent.get(ss[0]));
                d.setKeys(ss[1]);
                d.setVal(ss[2]);
                d.setDescs(ss[3]);
                dictionRepository.save(d);
            }
        }
    }

    @Override
    public String getValueByKey(String key) {
        if(map == null || map.size() == 0){
            List<Diction> list = dictionRepository.findAll();
            for(Diction d:list){
                map.put(d.getKeys(),d.getVal());
            }
        }
        return map.get(key);
    }

    @Override
    public void clearMap() {
        if(map != null){
            map.clear();
        }
    }

    @Override
    public List<Diction> listDictions(String keys) {
        return executeNativeQuerySql(MessageFormat.format( "select * from {0} e where e.pid in (select id from {0} where keys = ?) order by keys", tableName()), keys);
    }
}
