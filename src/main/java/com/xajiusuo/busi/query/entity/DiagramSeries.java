package com.xajiusuo.busi.query.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class DiagramSeries implements Serializable {

    private Set<DiagramSeries.Data> datas;

    private Set<DiagramSeries.Link> links;


    public Set<Data> getDatas() {
        return datas != null ? datas : new HashSet<DiagramSeries.Data>();
    }


    public Set<Link> getLinks() {
        return links != null ? links : new HashSet<DiagramSeries.Link>();
    }

    @lombok.Data
    public class Data {

        private String id;

        private String name;

        private String type;

        private String keyword;

        private Boolean draggable = true;

        private String symbol;

        private Number symbolSize;

        private Label label;

        private Map<String, Object> info;

        @lombok.Data
        public class Label {
            private Boolean show = true;

            private String color;

            private int[] offset = {0, 30};

        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return Objects.equals(id, data.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

    }

    @lombok.Data
    public class Link {

        private String source;

        private String target;

        private String value = "";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Link link = (Link) o;
            return ((Objects.equals(target, link.target) && Objects.equals(source, link.source))
                    || (Objects.equals(target, link.source) && Objects.equals(source, link.target)));
        }

        @Override
        public int hashCode() {
            return (target + source + source + target).hashCode();
        }


    }


}
