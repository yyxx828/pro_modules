package com.xajiusuo.busi.sync.vo;

import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.P;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by 杨勇 on 19-6-27.
 * 字段转换
 */
public class ColVo {

    private String fromColName;//来源
    private Class<?> fromColType;//字段类型

    private String toColName;
    private Class<?> toColType = null;

    public ColVo(){
    }

    public ColVo(String fromColName,String toColName){
        this.fromColName = fromColName;
        this.toColName = toColName;
    }

//    public ColVo setFromColName(String fromColName) {
//        this.fromColName = fromColName;
//        return this;
//    }

    public ColVo setFromColType(String fromColType) {
        try {
            this.fromColType = Class.forName(fromColType);
        } catch (Exception e) {
        }
        return this;
    }

    public ColVo setFrom(String fromColName, String fromColType){
        if(StringUtils.isNoneBlank(fromColName))
        this.fromColName = fromColName;
        setFromColType(fromColType);
        return this;
    }

    public ColVo setTo(String toColName, String toColType){
        if(StringUtils.isNoneBlank(toColName))
        this.toColName = toColName;
        setToColType(toColType);
        return this;
    }

//    public ColVo setToColName(String toColName) {
//        this.toColName = toColName;
//        return this;
//    }

    public ColVo setToColType(String toColType) {
        try {
            this.toColType = Class.forName(toColType);
        } catch (Exception e) {
            this.toColName = null;
        }
        return this;
    }

    public Object toVal(Object v){
        if(v == null){
            return v;
        }
        if(toColType.isAssignableFrom(fromColType)){
            return v;
        }
        try {
            if(fromColType.equals(String.class)){
                if(Number.class.isAssignableFrom(toColType)){
                    Double d = Double.parseDouble(v.toString());
                    if(d - d.intValue() < 0.0001){
                        return d.intValue();
                    }else{
                        return d;
                    }
                }else if(Date.class.isAssignableFrom(toColType)){
                    return DateUtils.parse(v.toString(), P.S.fmtYmd11);
                }
            }else if(Date.class.isAssignableFrom(fromColType)){
                if(String.class.equals(toColType)){
                    return DateUtils.format((Date)v,P.S.fmtYmd11);
                }
            }else if(Number.class.isAssignableFrom(fromColType)){
                if(String.class.equals(toColType)){
                    return v.toString();
                }
            }
        }catch (Exception e){
        }
        return null;
    }

    public String getFromColName() {
        return fromColName;
    }

    public Class<?> getFromColType() {
        return fromColType;
    }

    public String getToColName() {
        return toColName;
    }

    public Class<?> getToColType() {
        return toColType;
    }

}
