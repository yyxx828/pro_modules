package com.xajiusuo.busi.mail.send.conf;

/**
 * 杨勇
 * 弹框消息设置
 * Created by hadoop on 19-3-29.
 */
public enum TipsConf {
    //定义     模块        路由         消息类型,是否可订阅,显示时长
    //不宜过长 业务总模块   业务页面      业务中文  boolean  前端控制显示时长

    TIPS_TASK("TIPS_TASK","TIPS_TASK","任务",true,5),
    TIPS_AUDIT("k0503","/newCaseCenter.html#/newMission","待审",true,5),//针对审批者
    TIPS_APPR("TIPS_APPR","newCaseCenter.html#/newMission","审批",true,5),//针对提交用户

    TIPS_GATECONTROL("TIPS_GATECONTROL","TIPS_GATECONTROL","出入口门禁布控",true,5), //出入口门禁布控
    TIPS_BUILDCONTROL("TIPS_BUILDCONTROL","TIPS_BUILDCONTROL","楼宇门禁卡布控",true,5), //楼宇门禁卡布控
    TIPS_CARCONTROL("TIPS_CARCONTROL","TIPS_CARCONTROL","机动车布控",true,5), //机动车布控
    TIPS_WIFICONTROL("TIPS_WIFICONTROL","TIPS_WIFICONTROL","WIFI布控",true,5), //WIFI布控

    TIPS_PERSONWARN("TIPS_PERSONWARN","TIPS_PERSONWARN","异常人员预警",true,5), //异常人员预警

    TIPS_ELECTRONICFENCELOG("TIPS_ELECTRONICFENCELOG","TIPS_ELECTRONICFENCELOG","周界报警信息",true,5),




    TIPS_SYS("TIPS_SYS","TIPS_SYS","系统",true,5),
    TIPS_NOTICE("","index.html#/announcement","公告",true,5),
//    TIPS_MAIL("","TIPS_MAIL","站内信",5),
    TIPS_ONLINE("","","上线",3),
    TIPS_OFFLINE("","","下线",3),

    TIPS_NO("","","未知",3);



    private String module;//前端模块
    private String remote;//前端路由
    private String name;//标题名臣
    private boolean subscribe;//是否可订阅(保存)
    private int duration = 0;

    TipsConf(String module,String remote, String name,int duration){
        this(module,remote,name,false,duration);
    }

    /***
     *
     * @param module 前端模块
     * @param remote 前端路由
     * @param name 标题名臣
     * @param subscribe 是否可订阅(消息保存)
     * @param duration 显示时长
     */
    TipsConf(String module,String remote, String name, boolean subscribe,int duration){
        this.module = module;
        this.remote = remote;
        this.name = name;
        this.subscribe = subscribe;
        this.duration = duration;
    }

    public String getRemote() {
        return remote;
    }

    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public int getDuration() {
        return duration;
    }
}