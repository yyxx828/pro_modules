package com.xajiusuo.busi.echartsUtil;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 折线图、柱状图渲染数据处理
 *
 * @author lizhidong
 * @date 2019-6-14 10:06
 */
public class LineBarCharts {

    private List<LineBarData> data;

    public LineBarCharts() {
        if (null == data) {
            data = new ArrayList<>();
        }
    }

    /**
     * 逐条添加图表数据
     *
     * @param xAxis  x轴项目名称
     * @param legend 图例名称
     * @param value  值
     * @return
     */
    public LineBarCharts add(String xAxis, String legend, Object value) {
        data.add(new LineBarData(xAxis, legend, value));
        return this;
    }

    /**
     * 批量添加图表数据
     *
     * @param lineBarDatas
     * @return
     */
    public LineBarCharts add(List<LineBarData> lineBarDatas) {
        data.addAll(lineBarDatas);
        return this;
    }

    /**
     * 数据格式化成拆线图或柱状图所需数据
     *
     * @return json
     */
    public Object generate() {
        LineBarChartsData charts = new LineBarChartsData();
        data.stream().collect(Collectors.groupingBy(LineBarData::getXAxis)).forEach((k, v) -> charts.addXList(k));
        charts.getXList().sort((p1, p2) -> p1.compareTo(p2));
        data.stream().collect(Collectors.groupingBy(LineBarData::getLegend))
                .forEach((k, v) -> charts.addList(new KV(k, v.stream().sorted((p1, p2) -> p1.getXAxis().compareTo(p2.getXAxis())).map(LineBarData::getValue).toArray())));
        return charts.toJSON();
    }

    @Data
    private final static class LineBarChartsData {
        private List<String> xList;
        private List<KV> list;

        public LineBarChartsData() {
            if (null == xList) xList = new ArrayList<>();
            if (null == list) list = new ArrayList<>();
        }

        public void addXList(Object v) {
            if (v instanceof String) {
                xList.add(v + "");
            } else if (v instanceof List) {
                xList.addAll((List) v);
            }
        }

        public void addList(Object v) {
            if (v instanceof KV) {
                list.add((KV) v);
            } else if (v instanceof List) {
                list.addAll((List<KV>) v);
            }
        }

        public Object toJSON() {
            return this;
        }
    }

    @Data
    private final static class KV {
        private String name;
        private Object[] data;

        public KV(String name, Object[] data) {
            this.name = name;
            this.data = data;
        }
    }

    @Data
    public final static class LineBarData {
        private String xAxis;//类目项(x轴)
        private String legend;//图例(数据类型)
        private Object value;//数据值

        public LineBarData() {
        }

        public LineBarData(String xAxis, String legend, Object value) {
            this.xAxis = xAxis;
            this.legend = legend;
            this.value = value;
        }
    }
}
