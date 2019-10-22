package com.xajiusuo.busi.electroPatrol.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lizhidong
 * @date 2019-6-12 09:39
 */
@ApiModel(value = "electro_patrol", description = "电子巡更采集表")
@Setter
@Getter
@Entity
@Table(name = "T_ELECTROPATROLLOG_20", catalog = "village")
public class ElectroPatrolLog implements Serializable {

    private static final long serialVersionUID = 9099788198558737529L;

    @Id
    @GeneratedValue(
            generator = "uuid"
    )
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid"
    )
    private String id;
    private String patrolPersonNo;//巡更人编号
    private String patrolPersonName;//巡更人姓名
    private String patrolPersonPhone;//巡更人电话
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date patrolTime;//巡更时间
    private String villageId;//小区编号
    private String manager;//管理处
    private String patrolAddr;//巡更地址
    private String patrolStood;//巡更岗位
    private String departType;//班次类型
    private Integer round;//轮次
    private Integer roundSum;//巡逻次数
    private String apeId;//设备编码
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;//创建时间
    private String point;//点位
    private Integer mustRoundSum;//应巡次数

    @ApiModelProperty(value = "用于巡更时间区间查询", required = false)
    @Transient
    private String patrolTimeRange;

    public ElectroPatrolLog() {
    }

    public ElectroPatrolLog(String patrolPersonNo, String patrolPersonName, String patrolPersonPhone, Date patrolTime, String villageId, String manager, String patrolAddr, String patrolStood, String departType, Integer round, Integer roundSum, String apeId, Date createDate) {
        this.patrolPersonNo = patrolPersonNo;
        this.patrolPersonName = patrolPersonName;
        this.patrolPersonPhone = patrolPersonPhone;
        this.patrolTime = patrolTime;
        this.villageId = villageId;
        this.manager = manager;
        this.patrolAddr = patrolAddr;
        this.patrolStood = patrolStood;
        this.departType = departType;
        this.round = round;
        this.roundSum = roundSum;
        this.apeId = apeId;
        this.createDate = createDate;
    }
}
