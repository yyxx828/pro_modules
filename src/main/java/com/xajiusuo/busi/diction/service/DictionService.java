package com.xajiusuo.busi.diction.service;

import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.jpa.param.e.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by 杨勇 on 18-8-20.
 */
public interface DictionService extends BaseService<Diction,Integer> {

    /***
     * 杨勇 2018-08-23
     * 字典查询
     * 查询字典列表
     * @return
     */
    Page<Diction> baseDictions(Pageable pageable, Integer pid);

    /***
     *
     * 杨勇 2018-08-23
     * 字典保存
     * @param entity
     * @return
     */
    Result saveDiction(Diction entity);

    /***
     * 杨勇 2018-09-11
     * 字典同步操作
     * @param dicSet
     */
    void synDics(Map<String, String> dicSet);

    /***
     * 通过字典key获取对应值
     * @param key
     * @return
     */
    String getValueByKey(String key);

    /***
     * 重值缓冲值
     */
    void clearMap();

    List<Diction> listDictions(String keys);
}
