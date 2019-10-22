package com.xajiusuo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.user.entity.Userinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 杨勇 on 18-8-22.
 */
public class JSONUtils {

    private static String[] fields = "lastModifyUID,createTime,lastModifyTime,createUID,createName".split(",");

    private JSONUtils(){
    }

    /***
     * 杨勇 2018-08-22
     * 去除多余属性
     * @param e 要去除的对象
     * @param base 是否去除基础属性lastModifyUID,createTime,lastModifyTime,createUID,createname
     * @param fs 自定义去除字段
     */
    public static Object removeField(JSONObject e, boolean base, String ... fs){
        if(base){
            for(String s:fields){
                e.remove(s);
            }
        }
        if(fs != null && fs.length > 0){
            for(String s:fs){
                e.remove(s);
            }
        }
        return e;
    }


    /***
     * 杨勇 2018-08-22
     * 自动匹配去除对象包含属性也是对象中一些无用属性,一直级联到最底层
     * @param e
     * @param base
     * @param fs
     * @return
     */
    public static Object autoRemoveField(Object e,boolean base,String ... fs){
        if(e instanceof JSONObject){
            JSONObject o = (JSONObject) e;
            removeField(o,base,fs);
            for(String key:o.keySet()){
                autoRemoveField(o.get(key),base,fs);
            }
        }else if(e instanceof JSONArray){
            JSONArray a = (JSONArray) e;
            for(int i = 0;i<a.size();i++){
                autoRemoveField(a.get(i),base,fs);
            }
        }
        return e;
    }

    /***
     * 杨勇 2018-08-22
     * 自动匹配去除对象包含属性也是对象中一些无用属性,一直级联到最底层
     * @param e
     * @param fs
     * @return
     */
    public static Object onlyField(Object e,String ... fs){
        List<String> list = Arrays.asList(fs);
        if(e instanceof JSONObject){
            JSONObject o = (JSONObject) e;
            for(String k:new ArrayList<String>(o.keySet())){
                if(!list.contains(k)){
                    o.remove(k);
                }
            }
        }else if(e instanceof JSONArray){
            JSONArray a = (JSONArray) e;
            for(int i = 0;i<a.size();i++){
                onlyField(a.get(i),fs);
            }
        }
        return e;
    }

}
