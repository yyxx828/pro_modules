package com.xajiusuo.busi.electroPatrol.vo;

import com.xajiusuo.busi.electroPatrol.entity.ElectroPatrolLog;
import lombok.Data;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 巡更比率
 *
 * @author lizhidong
 * @date 2019-6-13 14:51
 */
@Data
public class PatrolRate {

    private String point;//点位
    private Integer mustRoundSum;//应巡次数
    private Integer roundSum;//巡逻次数
    private String rate;//巡更率

    public PatrolRate(String point, Integer mustRoundSum, Integer roundSum, String rate) {
        this.point = point;
        this.mustRoundSum = mustRoundSum;
        this.roundSum = roundSum;
        this.rate = rate;
    }

    /**
     * 生成巡更比率数据
     *
     * @param patrolLogs 巡更日志
     * @return
     */
    public static List<PatrolRate> generate(List<ElectroPatrolLog> patrolLogs) {
        return
                Optional.ofNullable(patrolLogs).map(n -> {
                    List<PatrolRate> patrolRates = new ArrayList<>();
                    NumberFormat percent = NumberFormat.getPercentInstance();
                    percent.setMaximumFractionDigits(2);
                    n.stream().collect(Collectors.groupingBy(ElectroPatrolLog::getPoint)).forEach((k, v) -> {
                        String rate;
                        Integer round_sum = v.stream().mapToInt(ElectroPatrolLog::getRoundSum).sum();
                        Integer must_round_sum = v.get(0).getMustRoundSum();
                        if (0 == must_round_sum) {
                            rate = "0";
                        } else {
                            BigDecimal s = new BigDecimal(round_sum.doubleValue() / must_round_sum.doubleValue());
                            rate = percent.format(s);
                        }
                        patrolRates.add(new PatrolRate(k, round_sum, must_round_sum, rate));
                    });
                    return patrolRates;
                }).orElse(null);
    }

}
