package com.xajiusuo.busi.template.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务表自动创建
 * Created by 杨勇 on 19-1-15.
 */
public class BusiProper {

    //数据库列标识
    private static List<String> columnList = new ArrayList<String>();

    //数据库列标识对应中文描述,会在用户页面展示,务必要避免出现口头语
    private static List<String> properList = new ArrayList<String>();

    //业务表集合
    private static Map<String, String> busiMap = new HashMap<String, String>();

    //业务表寄递集合
    private static Map<String, String> expressMap = new HashMap<String, String>();

    //所有业务表描述集合
    private static Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

    private static Map<String, String[]> keyList = new HashMap<String, String[]>();


    private static void init() {
        if (busiMap.size() > 0) {
            return;
        }

        //涉恐人员
//        busiMap.put("skryxx", "涉恐人员");
//        columnList.add("skryxx:xm,sfzh,hjszd,txxx,xzxxdz,yxx,rksj,xzsj,cxsj,jlbgsj,lbbj,xlmc,bz");
//        properList.add("skryxx:姓名,身份证号,户籍地,通讯信息,现住详细地址,有效性,纳入部级重点人员库时间,记录新增时间,记录撤销时间,记录变更时间,重点人类别标记,重点人员细类名称,备注");


        ///////////////////////////////快递///////////////////////////////////////////

        //全一快递
//        expressMap.put("qykd", "全一快递");
//        columnList.add("qykd:express_company_code,waybill_id,business_type,item_weight,sender_city_code,receiver_city_code,sendre_nation_code,receiver_nation_code,sender_name,sender_mobile,sender_tel,sender_province,sender_city,sender_county,sender_address,receiver_name,receiver_mobile,receiver_tel,receiver_province,receiver_city,receiver_county,receiver_address,item_type,item_name,limit_time_code,sender_delay_time,insure_amount");
//        properList.add("qykd:物流公司编号,运单单号,运单业务类型,物品重量,始发市代码,目的地市代码,始发国代码,目的地国代码,发货人姓名,发货人手机,发货人电话,始发省,始发市,始发区/县,发货人地址,收货人姓名,收货人手机,收货人电话,目的地省,目的地市,目的地区/县,收货人地址,物品类型,物品名称,寄达时限产品代码,发货时间,保价金额");

    }


    public static Map<String, String> getExpressMap() {
        init();
        return expressMap;
    }

    public static boolean isPool(String key) {
        if (busiMap.get(key) != null)
            return true;
        return expressMap.get(key) != null;
    }

    public static Map<String, String> getProper(String key) {
        if (key == null) {
            return null;
        }

        init();
        if (map.size() == 0) {
            for (int i = 0; i < columnList.size(); i++) {
                try {
                    String[] msg = columnList.get(i).split(":");
                    String[] ps = msg[1].split(",");
                    String[] psms = properList.get(i).split(":")[1].split(",");
                    Map<String, String> m = new HashMap<String, String>();
                    for (int j = 0; j < ps.length; j++) {
                        m.put(ps[j], psms[j]);
                    }
                    map.put(msg[0], m);
                } catch (Exception e) {//对于出错信息跳过处理
                    System.err.println(columnList.get(i) + "\n" + e.getMessage());
                    continue;
                }
            }
        }
        return map.get(key.toLowerCase());
    }

    public static String[] getKeys(String key) {
        if (keyList.size() == 0) {
            for (String s : columnList) {
                String[] ss = s.split(":");
                keyList.put(ss[0].toLowerCase(), ss[1].toLowerCase().split(","));
            }
        }
        return keyList.get(key);
    }


    public static void main(String[] args) {
        init();
        if (columnList.size() != properList.size()) {
            System.out.println("整体数据不对称");
        } else {
            System.out.println("整体数据对称");
        }

        for (int i = 0; i < columnList.size(); i++) {
            if (properList.size() > i) {
                if (columnList.get(i).split(",").length != properList.get(i).split(",").length) {
                    System.out.println(columnList.get(i).split(":")[0] + "字段不对应");
                } else {
                    if (columnList.get(i).contains(":") && properList.get(i).contains(":")) {
                        System.out.println("校验通过");
                    } else {
                        System.out.println("缺失标识");
                    }
                }
            } else {
                System.out.println("描述缺失");
            }
        }
    }
}
