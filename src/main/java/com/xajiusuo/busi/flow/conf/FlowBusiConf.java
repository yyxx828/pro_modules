package com.xajiusuo.busi.flow.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程业务配置
 * Created by 杨勇  on 2019/2/18.
 */
public enum FlowBusiConf {

  /***
   * 待办事项
   */
  TODOTASK("TodoTask","待办事项"),
  CASECENTER("CaseCenter","案件中心"),
  DATACOMPARE("DataCompare","数据对比");

  //TODO 新增业务需要在此处新增业务枚举

  String busi;//业务标识
  String busiName;//业务名称

  static List<String> busis = new ArrayList<String>();
  static List<Map<String,String>> list = new ArrayList<Map<String,String>>();

  FlowBusiConf(String busi,String busiName){
    this.busi = busi;
    this.busiName = busiName;
  }

  public String getBusi() {
    return busi;
  }

  public String getBusiName() {
    return busiName;
  }

  public static List<String> getBusis() {
    if(busis.size() == 0){
      for(FlowBusiConf cof: FlowBusiConf.values()){
        busis.add(cof.busi);
      }
    }
    return busis;
  }

  public static FlowBusiConf getConf(String conf){
    try {
      return FlowBusiConf.valueOf(conf);
    }catch (Exception e){
      for(FlowBusiConf cf: FlowBusiConf.values()){
        if(cf.getBusi().equals(conf)){
          return cf;
        }
      }
    }
    return null;
  }

  public static List<Map<String,String>> getBusiMap(){
    if(list.size() == 0){
      for(FlowBusiConf cof: FlowBusiConf.values()){
        Map<String,String> busi = new HashMap<String,String>();
        busi.put("value",cof.busi);
        busi.put("name",cof.busiName);
        list.add(busi);
      }
    }
    return list;
  }

}