package com.xajiusuo.busi.query.entity;


import lombok.Data;

import java.util.Date;


/**
 * 瞬时刷新的物化视图
 * <p>
 * 关联表 t_house_19（房）,T_HOUSEOWNER_19（房屋業主關係表）,t_owner_19（業主表）,t_motorvehicle_16（車）
 */
@Data
public class AssociationQueryVO {

    private String house_Id;  //房屋id

    private String owner_Id; //业主id

    private String motorVehicle_Id;  //车辆id

    private String UnitName;    //单元名

    private String BuildingName;  //楼栋名

    private String houseNo;

    private String iDNumber;  //身份证

    private String name;   //姓名

    private String sex;

    private String nation;

    private String idType;

    private String birthday;   //生日

    private String plateno;    //车牌

    private String vehicleModel;   //车辆型号

    private String vehicleBrand;  //车辆品牌

    private String vehicleColor;  //车身颜色

    private String vehicleClass;

    private Number motorVehicle_delFlag;

    private Number owner_delFlag;

    private Number house_delFlag;

    private String room_name;

    private String floor;
}
