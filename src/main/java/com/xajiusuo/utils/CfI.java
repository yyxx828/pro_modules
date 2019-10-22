package com.xajiusuo.utils;

import com.xajiusuo.jpa.param.e.Configs;

/**
 * Created by hadoop on 19-6-17.
 */
public interface CfI {
    //                                                参 数 配 置 规 范                                                  //
    //     参数名称C_MODELNAME_BUSINAME_PARAM=new Object[]{"MODELNAME_BUSINAME_PARAM", DataType,"参数描述","value"}       //
    //  以C_开始后面[MODELNAME_BUSINAME_PARAM]必须和定义标识["MODELNAME_BUSINAME_PARAM"],或者放空"",否则抛出异常              //

    Object[] C_VILLAGE_SYSTEMID                             = new Object[]{"",Configs.DataType.STRING,  "项目标识", "village"};

    Object[] C_VILLAGE_NO_PRE                               = new Object[]{"",Configs.DataType.STRING,  "小区上传编号前缀[000-ZZZ]", "000"};
    //昼伏夜出时间段设置
    Object[] C_NIGHTOUTDUR_START                            = new Object[]{"",Configs.DataType.INT,     "昼伏夜出时间段设置-开始", 20};
    Object[] C_NIGHTOUTDUR_END                              = new Object[]{"",Configs.DataType.INT,     "昼伏夜出时间段设置-结束", 8};

    Object[] C_TALK_HISTORY_KEEPDAY                         = new Object[]{"",Configs.DataType.INT,     "群聊记录保留天数(范围[-1,99],-1为不清理)", 30};

    //布控
    Object[] C_CATCH_WIFI_MSGID                             = new Object[]{"",Configs.DataType.STRING,  "WIFI探针布控推送的最新中标数据id", ""};
    Object[] C_CATCH_CAR_MSGID                              = new Object[]{"",Configs.DataType.STRING,  "机动车布控推送的最新中标数据id", ""};
    Object[] C_CATCH_GATE_MSGID                             = new Object[]{"",Configs.DataType.STRING,  "出入口门禁布控推送的最新中标数据id", ""};
    Object[] C_CATCH_BULID_MSGID                            = new Object[]{"",Configs.DataType.STRING,  "楼宇门禁布控推送的最新中标数据id", ""};

    //人员预警昼伏夜出
    Object[] C_EARLYWARN_PERSON_NIGHTOUTDUR_DATARANGE       = new Object[]{"",Configs.DataType.INT,     "人员昼伏夜出预警数据范围（单位：天）", 7};
    Object[] C_EARLYWARN_PERSON_NIGHTOUTDUR_TIMERANGE       = new Object[]{"",Configs.DataType.STRING,  "人员昼伏夜出预警时间范围", "20,8"};
    Object[] C_EARLYWARN_PERSON_NIGHTOUTDUR_NUMS            = new Object[]{"",Configs.DataType.INT,     "人员昼伏夜出预警次数", 1};
    //人员预警频繁出入
    Object[] C_EARLYWARN_PERSON_OFTENOUTIN_DATARANGE        = new Object[]{"",Configs.DataType.INT,     "人员频繁出入预警数据范围（单位：小时）", 3};
    Object[] C_EARLYWARN_PERSON_OFTENOUTIN_NUMS             = new Object[]{"",Configs.DataType.INT,     "人员频繁出入预警次数", 1};

    //车辆长时间停放预警参数
    Object[] C_EARLYWARN_CAR_LONGTIMESTOP_DATARANGE         = new Object[]{"",Configs.DataType.INT,     "人员频繁出入预警数据范围（单位：天）", 30};

    //车辆长频繁出入预警参数
    Object[] C_EARLYWARN_CAR_OFTENOUTIN_DATARANGE           = new Object[]{"",Configs.DataType.INT,     "车辆频繁出入预警数据范围（单位：小时）", 3};
    Object[] C_EARLYWARN_CAR_OFTENOUTIN_NUMS                = new Object[]{"",Configs.DataType.INT,     "车辆频繁出入预警次数", 3};

    //车辆昼伏夜出预警参数
    Object[] C_EARLYWARN_CAR_NIGHTOUTDUR_DATARANGE          = new Object[]{"",Configs.DataType.INT,     "车辆昼伏夜出预警数据范围（单位：天）", 7};
    Object[] C_EARLYWARN_CAR_NIGHTOUTDUR_TIMERANGE          = new Object[]{"",Configs.DataType.STRING,  "车辆昼伏夜出预警时间范围", "0,4"};
    Object[] C_EARLYWARN_CAR_NIGHTOUTDUR_NUMS               = new Object[]{"",Configs.DataType.INT,     "车辆昼伏夜出预警次数", 3};

    //机动车时间段流量统计 时间颗粒度
    Object[] C_CENSUS_MOTORVEEHICLE_TIMECOUNT_TIMEUNIT      = new Object[]{"", Configs.DataType.INT,    "机动车时间段流量统计-时间颗粒度", 2};

    //机动车基础信息码值定义
    Object[] C_CAR_PLATECLASS_DICTION                       = new Object[]{"", Configs.DataType.STRING, "号牌种类字典码值", "k01"};
    Object[] C_CAR_VEHICLECLASS_DICTION                     = new Object[]{"", Configs.DataType.STRING, "车辆类型字典码值", "k02"};
    Object[] C_CAR_VEHICLEBRAND_DICTION                     = new Object[]{"", Configs.DataType.STRING, "车辆品牌字典码值", "k03"};
    Object[] C_CAR_VEHICLESTATUS_DICTION                    = new Object[]{"", Configs.DataType.STRING, "车辆状态字典码值", "k04"};

    //关注人员信息码值
    Object[] C_FOLLOWPERSON_TYPE_DICTION                    = new Object[]{"", Configs.DataType.STRING, "布控人员类型字典码值", "k15"};

    //布控信息码值定义
    Object[] C_CATCHPERSON_TPYE_DICTION                     = new Object[]{"", Configs.DataType.STRING, "关注人员类型字典码值", "k17"};
    Object[] C_CATCHCAR_TPYE_DICTION                        = new Object[]{"", Configs.DataType.STRING, "布控车辆类型字典码值", "k18"};
    Object[] C_CATCHWIFI_TPYE_DICTION                       = new Object[]{"", Configs.DataType.STRING, "布控wifi类型字典码值", "k19"};

    //房屋基础信息码表值定义
    Object[] C_HOUSE_ATTRUBUTE_DICTION                      = new Object[]{"", Configs.DataType.STRING, "房屋属性字典码值", "k11"};
    Object[] C_HOUSE_RENTAL_DICTION                         = new Object[]{"", Configs.DataType.STRING, "租赁状态", "k12"};
    Object[] C_HOUSE_HOUSETYPE_DICTION                         = new Object[]{"", Configs.DataType.STRING, "房屋户型", "k20"};
    //人员基础信息码表值定义
    Object[] C_OWNER_IDTYPE_DICTION                         = new Object[]{"", Configs.DataType.STRING, "证件类型", "k10"};
    Object[] C_OWNER_SEX_DICTION                            = new Object[]{"", Configs.DataType.STRING, "性别", "k06"};
    Object[] C_OWNER_NATION_DICTION                         = new Object[]{"", Configs.DataType.STRING, "民族", "k07"};
    Object[] C_OWNER_POLITICSSTATUS_DICTION                 = new Object[]{"", Configs.DataType.STRING, "政治面貌", "k13"};
    Object[] C_OWNER_EDUCATIONSTATUS_DICTION                = new Object[]{"", Configs.DataType.STRING, "文化程度", "k14"};
    Object[] C_OWNER_IDENTIFY_TYPE_DICTION                  = new Object[]{"", Configs.DataType.STRING, "人员类型", "k08"};
    Object[] C_OWNER_IDENTIFY_COUNTRY_DICTION               = new Object[]{"", Configs.DataType.STRING, "所属国家", "k09"};
    //停车厂基础信息码值定义
    Object[] C_PARKING_PARKINGTYPE_DICTION                  = new Object[]{"", Configs.DataType.STRING, "所属国家", "k16"};
    //租客身份类型码值
    Object[] C_OWNER_TENANT_DICTION                         = new Object[]{"", Configs.DataType.STRING, "租客身份类性", "k0802"};
    //本地人口
    Object[] C_OWNER_LOCATION_DICTION                       = new Object[]{"", Configs.DataType.STRING,"本地人口类别", "4101"};
    //常住人口
    Object[] C_OWNER_RESIDENT_DICTION                       = new Object[]{"", Configs.DataType.STRING,"常住人口(不能修改)", "'k0801','k0802','k0803'"};
    //暂住人口
    Object[] C_OWNER_TEMPORARY_DICTION                      = new Object[]{"", Configs.DataType.STRING,"暂住人口(不能修改)", "'k0804'"};
    //寄住人
    Object[] C_OWNER_LODGE_DICTION                          = new Object[]{"", Configs.DataType.STRING,"寄住人口(不能修改)", "'k0805'"};
    //中国人口
    Object[] C_OWNER_CHINA_DICTION                          = new Object[]{"", Configs.DataType.STRING,"中国人口", "k0901"};
    //男性
    Object[] C_OWNER_MAN_DICTION                            = new Object[]{"", Configs.DataType.STRING,"男性", "k0601"};
    //女性
    Object[] C_OWNER_WOMAN_DICTION                          = new Object[]{"", Configs.DataType.STRING,"女性", "k0602"};


    //门禁卡 时间颗粒度
    Object[] C_CENSUS_ACCESSCARD_TIMECOUNT_TIMEUNIT         = new Object[]{"", Configs.DataType.INT,    "门禁卡-时间颗粒度", 2};
    //门禁卡昼伏夜出时间段设置
    Object[] C_ACCESSCARD_NIGHTOUTDUR_START                 = new Object[]{"",Configs.DataType.INT,     "门禁卡昼伏夜出时间段设置-开始", 0};
    Object[] C_ACCESSCARD_NIGHTOUTDUR_END                   = new Object[]{"",Configs.DataType.INT,     "门禁卡昼伏夜出时间段设置-结束", 8};

    //数据源
    Object[] C_DATASOURCE_SECOND_RUN                        = new Object[]{"", Configs.DataType.BOOLEAN, "是否启动同步", true};
    Object[] C_DATASOURCE_SECOND_DRIVER                     = new Object[]{"", Configs.DataType.STRING, "同步数据源驱动", "oracle.jdbc.OracleDriver"};
    Object[] C_DATASOURCE_SECOND_URL                        = new Object[]{"", Configs.DataType.STRING, "同步数据源URL", "jdbc:oracle:thin:@192.168.1.12:1521:orcl"};
    Object[] C_DATASOURCE_SECOND_USERNAME                   = new Object[]{"", Configs.DataType.STRING, "同步数据源用户名", "maildata"};
    Object[] C_DATASOURCE_SECOND_PASSWORD                   = new Object[]{"", Configs.DataType.STRING, "同步数据源密码", "maildata"};

    //                                             请 求 描 述 配 置 规 范                                                //
    //     参数名称R_MODELNAME_DESC_CODE = "MODELNAME_DESC_CODE:请求描述"                                                 //
    //  以R_开头后面[MODELNAME_DESC_CODE] ["MODELNAME_DESC_CODE"]必须相同,否则抛出异常,以:拼接描述内容 或留空即可无需:          //

    //---------------------------------------------成功----------------------------------------------------------------//
    //机动车
    String[] R_CAR_CCBHREPEAT_SUCCESS               = new String[]{"","车辆编号不重复"};
    String[] R_CAR_CPREPEAT_SUCCESS                 = new String[]{"","车牌号不重复"};
    String[] R_CATCH_TASKREPEAT_SUCCESS             = new String[]{"","任务名称不重复"};

    String[] R_CAR_BHREPEAT_SUCCESS                 = new String[]{"","车辆编号重复"};
    String[] R_CAR_REPEAT_SUCCESS                   = new String[]{"","车牌号重复"};
    String[] R_CATCH_TASKRISEPEAT_SUCCESS           = new String[]{"","任务名称重复"};

    //流程
    String[] R_FLOW_RUNON_SUCCESS                   = new String[]{"","流程启用成功"};
    String[] R_FLOW_RUNOFF_SUCCESS                  = new String[]{"","流程禁用成功"};
    String[] R_FLOW_RUNNOALTER_SUCCESS              = new String[]{"","流程启禁用未变化"};
    String[] R_FLOW_FLOWAPPROLING_SUCCESS           = new String[]{"","操作成功"};
    String[] R_FLOW_FLOWAPPROL_SUCCESS              = new String[]{"","审批成功"};

    //用户
    String[] R_USER_LOGIN_SUCCESS                   = new String[]{"","登陆成功"};
    String[] R_USER_EXPORT_SUCCESS                  = new String[]{"","导出成功"};
    String[] R_USER_PASSWORDCHANGE_SUCCESS          = new String[]{"","修改密码成功"};
    String[] R_USER_IMPORT_SUCCESS                  = new String[]{"","导入成功"};

    //消息
    String[] R_MAIL_SEND_SUCCESS                    = new String[]{"", "发送成功"};

    //---------------------------------------------失败----------------------------------------------------------------//
    //机动车
    String[] R_CAR_CCBHREPEAT_FAIL                  = new String[]{"","车辆编号重复"};
    String[] R_CAR_CPREPEAT_FAIL                    = new String[]{"","车牌号重复"};
    String[] R_CATCH_TASKREPEAT_FAIL                = new String[]{"","任务名称重复"};

    //机构
    String[] R_DEPART_EMPTY_FAIL                    = new String[]{"","父机构不能为空"};
    String[] R_DEPART_DNAMEEXIST_FAIL               = new String[]{"","机构名称已存在"};
    String[] R_DEPART_RECONVERDNAMEEXIST_FAIL       = new String[]{"","机构名称已存在,无法恢复"};
    String[] R_DEPART_EXISTSUBDEPART_FAIL           = new String[]{"","机构存在子机构无法删除"};
    String[] R_DEPART_EXISTUSER_FAIL                = new String[]{"","机构存在用户无法删除"};
    String[] R_DEPART_DEPART_NOTEXIST_FAIL          = new String[]{"","机构不存在"};

    //职位
    String[] R_DUTY_NAMEEXIST_FAIL                  = new String[]{"","职位名称已存在"};
    String[] R_DUTY_EXISTUSER_FAIL                  = new String[]{"","职位存在用户无法删除"};
    String[] R_DUTY_NOTEXIST_FAIL                   = new String[]{"","职位不存在"};

    //用户
    String[] R_USER_DISABLE_FAIL                    = new String[]{"","该用户已被禁用"};

    String[] R_USER_NOTEXIST_FAIL                   = new String[]{"","用户不存在"};
    String[] R_USER_LOGINFAIL_FAIL                  = new String[]{"","登陆校验失败"};
    String[] R_USER_NOLOGIN_FAIL                    = new String[]{"","用户未登陆"};
    String[] R_USER_USERNAMEEXIST_FAIL              = new String[]{"","用户名称已存在"};
    String[] R_USER_CARDIDEXIST_FAIL                = new String[]{"","身份证号已存在"};
    String[] R_USER_POLICENUMEXIST_FAIL             = new String[]{"","警号已存在"};
    String[] R_USER_EMPTYPASSWORD_FAIL              = new String[]{"","用户密码不能为空"};
    String[] R_USER_NOPOLICENUM_FAIL                = new String[]{"","警号不能为空"};
    String[] R_USER_OPERATEADMIN_FAIL               = new String[]{"","超级管理员不可操作"};
    String[] R_USER_EXPORTFAIL_FAIL                 = new String[]{"","导出失败"};
    String[] R_USER_IMPORTNOTENOUGH_FAIL            = new String[]{"","此行记录不完整"};
    String[] R_USER_EMPTYOPERGROUP_FAIL             = new String[]{"","权限不能为空"};
    String[] R_USER_EMPTYDUTY_FAIL                  = new String[]{"","职位不能为空"};
    String[] R_USER_EMPTYDEPT_FAIL                  = new String[]{"","部门不能为空"};
    String[] R_USER_LINENULL_FAIL                   = new String[]{"","有空行"};
    String[] R_USER_IMPORT_FAIL                     = new String[]{"","导入失败"};
    String[] R_USER_FILEISNULL_FAIL                 = new String[]{"","文件不能为空"};
    String[] R_USER_FORMATERROR_FAIL                = new String[]{"","文件格式不对"};
    String[] R_USER_DOWNLOAD_FAIL                   = new String[]{"","下载失败"};
    String[] R_USER_EMPTYOLD_FAIL                   = new String[]{"","原密码不能为空"};
    String[] R_USER_PASSWORDEMPTY_FAIL              = new String[]{"","密码不能为空"};
    String[] R_USER_NOTEQ_FAIL                      = new String[]{"","两次密码不一致"};
    String[] R_USER_PASSWORD_FAIL                   = new String[]{"","原密码错误"};
    String[] R_USER_PWDSAME_FAIL                    = new String[]{"","新密码和原密码不能一致"};
    String[] R_USER_USERNAME_FAIL                   = new String[]{"","姓名不能为空,且长度小于24,1中文=2英文长度"};
    String[] R_USER_USERNOTEXIST_FAIL               = new String[]{"","该用户不存在,无法审批"};
    String[] R_USER_NOTNEWUSER_FAIL                 = new String[]{"","该用户不是新注册用户,无法审批"};
    String[] R_USER_NOTLOGIN_FAIL                   = new String[]{"","用户未登录"};
    String[] R_USER_NOTENOUSER_FAIL                 = new String[]{"","无用户信息"};


    String[] R_DUTY_VALIDATE_USEDUTYNAME_SUCCESS    = new String[]{"", "此职位名称可以使用"};


    String[] R_HOUSE_HASRENT_FAIL                   = new String[]{"","此房屋正在出租"};
    String[] R_PARKINGLOG_PARKINGNO_CHECK_FAIL      = new String[]{"","此车位正在出租"};
    String[] R_EXCELLOG_ALL_CHECK_FAIL              = new String[]{"","导入文件失败"};
    String[] R_HOUSE_DELETE_FAIL                    = new String[]{"","此房屋下存在人员信息,不能删除"};
    String[] R_UNIT_DELETE_FAIL                     = new String[]{"","此单元下存在房屋信息,不能删除"};
    String[] R_BUILDING_DELETE_FAIL                 = new String[]{"","此楼栋下存在单元信息,不能删除"};
    String[] R_PRAKINGNO_DELETE_FAIL                = new String[]{"","此车位正在租赁中,不能删除"};
    String[] R_OWNER_DELETE_FAIL                    = new String[]{"","此人员信息门禁卡或者出入口在使用,不能删除"};


    //字典
    String[] R_DICTION_FIELDEXIST_FAIL              = new String[]{"","该字段已存在"};
    String[] R_DICTION_DICTIONMULTI_FAIL            = new String[]{"","字典数量超过数量限制"};
    String[] R_DICTION_DICTIONHASERRORS_FAIL        = new String[]{"","列表存在异常字典,无法正常保存"};
    String[] R_DICTION_DICTIONHASERROR_FAIL         = new String[]{"","key为空或者没以k开头或者长度不是3或者5"};
    String[] R_DICTION_DICTIONNOTEXIST_FAIL         = new String[]{"","该字典不存在"};
    String[] R_DICTION_KEYSRULEERROR_FAIL           = new String[]{"","keys不符合规则"};
    String[] R_DICTION_DICTIONHASEXIST_FAIL         = new String[]{"","已经存在该keys"};
    String[] R_DICTION_DICTIONHASCHILD_FAIL         = new String[]{"","存在子字典,无法删除"};
    String[] R_DICTION_FIELDHASEXIST_FAIL           = new String[]{"","该字段已存在"};

    //流程
    String[] R_FLOW_NOTDELETEDEFAULT_FAIL           = new String[]{"","此流程为默认流程,不能删除"};
    String[] R_FLOW_NOTDEFAULT_FAIL                 = new String[]{"","无默认流程"};
    String[] R_FLOW_NOTFOUND_FAIL                   = new String[]{"","流程已删除或不存在"};
    String[] R_FLOW_NOTUSE_FAIL                     = new String[]{"","流程已禁用"};
    String[] R_FLOW_DEPTNOTNULL_FAIL                = new String[]{"","请指定部门"};
    String[] R_FLOW_USERNOTNULL_FAIL                = new String[]{"","请指定用户"};
    String[] R_FLOW_DUTYNOTNULL_FAIL                = new String[]{"","请指定职位"};
    String[] R_FLOW_TYPEERR_FAIL                    = new String[]{"","参数异常"};
    String[] R_FLOW_NOACCESS_FAIL                   = new String[]{"","没有审批权限"};
    String[] R_FLOW_NOREJECTPOWER_FAIL              = new String[]{"","没有驳回权限"};
    String[] R_FLOW_NOADOPTPOWER_FAIL               = new String[]{"","没有通过权限"};
    String[] R_FLOW_NOUSEACCESS_FAIL                = new String[]{"","该流程审批已完成"};
    String[] R_FLOW_NOTSUBMIT_FAIL                  = new String[]{"","该流程未提交"};
    String[] R_FLOW_FLOWNODENOTUSER_FAIL            = new String[]{"","指定审批人为空,请联系管理员"};
    String[] R_FLOW_FLOWNODENOTDEP_FAIL             = new String[]{"","指定部门为空,请联系管理员"};
    String[] R_FLOW_FLOWNODEUSERNOTDEP_FAIL         = new String[]{"","用户部门为空,请联系管理员"};
    String[] R_FLOW_FLOWNODEUSERNOTDUTY_FAIL        = new String[]{"","用户职位为空,请联系管理员"};
    String[] R_FLOW_FLOWNOTSAMEDEP_FAIL             = new String[]{"","上级部门不存在,请联系管理员"};
    String[] R_FLOW_FLOWNOTDUTYS_FAIL               = new String[]{"","未指定审批职位,请联系管理员"};
    String[] R_FLOW_FLOWFILLERNOBACK_FAIL           = new String[]{"","提交者无撤回权限"};
    String[] R_FLOW_FLOWAUDITORNOBACK_FAIL          = new String[]{"","审批者无撤回权限"};
    String[] R_FLOW_FLOWNOTFILLER_FAIL              = new String[]{"","非流程提交者"};
    String[] R_FLOW_FLOWNOTAUDITOR_FAIL             = new String[]{"","非流程审批者"};
    String[] R_FLOW_FLOWNAMEEXIST_FAIL              = new String[]{"","该流程名称已存在"};
    String[] R_FLOW_BUSINOTNULL_FAIL                = new String[]{"","业务不能为空"};
    String[] R_FLOW_BUSINOEXIST_FAIL                = new String[]{"","该业务不存在"};
    String[] R_FLOW_FLOWALLOTEXIST_FAIL             = new String[]{"","该分配对象已设置流程"};
    String[] R_FLOW_FLOWNOTEXIST_FAIL               = new String[]{"","该流程不存在"};

    //操作
    String[] R_OPER_NOTFOUND_FAIL                   = new String[]{"", "该权限不存在"};
    String[] R_OPER_ISINIT_FAIL                     = new String[]{"", "该权限为默认权限不能删除"};
    String[] R_OPER_ISMAKEUSER_FAIL                 = new String[]{"", "该权限已分配到用户,不能删除"};
    String[] R_OPER_NOTFOUNDORMAKEUSER_FAIL         = new String[]{"", "该操作不存在或者已被分配权限组"};


    //消息
    String[] R_MAIL_SEND_FAIL                       = new String[]{"", "发送失败"};
    String[] R_MAIL_BEGINPAGE_FAIL                  = new String[]{"", "已在第一页"};
    String[] R_MAIL_ENDPAGE_FAIL                    = new String[]{"", "已在最后一页"};
    String[] R_MAIL_TYPEERR_FAIL                    = new String[]{"", "消息类型错误"};
    String[] R_MAIL_NOTSUBSCRIBE_FAIL               = new String[]{"", "该消息类型不支持订阅"};






    //公告
    String[] R_NOTE_TOP_SUCCESS                      = new String[]{"","公告置顶成功"};
    String[] R_NOTE_UNTOP_SUCCESS                    = new String[]{"","公告取消置顶成功"};

    String[] R_NOTE_ISTOP_FAIL                       = new String[]{"","公告已置顶"};
    String[] R_NOTE_ISUNTOP_FAIL                     = new String[]{"","公告已取消置顶!"};
    String[] R_NOTE_NOTOP_FAIL                       = new String[]{"","公告未发布不能置顶!"};
    String[] R_NOTE_NOTEXIST_FAIL                    = new String[]{"","该公告不存在!"};
    String[] R_NOTE_TOPTEN_FAIL                      = new String[]{"","置顶公告已达10条"};





    //模版
    String[] R_TEMPLATE_BUINESSNOTEXIST_FAIL        = new String[]{"", "业务表不存在,无法保存"};




}
