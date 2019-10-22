package com.xajiusuo.utils;

import com.xajiusuo.jpa.config.BaseIntEntity;

/**
 * Created by hadoop on 18-1-29.
 */
public class BaseEntityUtil {

    /***
     * @desc 新增对象参数处理
     * @author 杨勇 18-1-30 下午1:54
     * @param entity
     * @param uid
     */
    public static void addEntity(BaseIntEntity entity, Integer uid){
        entity.setId(null);
        entity.setCreateTime(null);
        entity.updateDate();
        entity.setCreateUID(uid);
        entity.setLastModifyUID(uid);
    }

    /***
     * @desc 修改对象参数处理
     * @author 杨勇 18-1-30 下午1:54
     * @param entity
     * @param uid
     */
    public static void updateEntity(BaseIntEntity entity,Integer uid){
        entity.updateDate();
        if(entity.getCreateUID() != null) {
            entity.setCreateUID(uid);
            entity.setLastModifyUID(uid);
        }
    }


}
