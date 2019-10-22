package com.xajiusuo.busi.sync.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by 杨勇 on 19-6-27.
 */
@Entity
@ApiModel(value = "SyncConf", description = "数据导入配置")
@Table(name = "P_SyncConf_10", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class SyncConf extends BaseIntEntity {

    @Transient
    public static final String chars = "[a-zA-Z][a-zA-Z0-9_]*";

    @ApiModelProperty(value = "规则名称", required = true, dataType = "String", example = "XXXX[配置]")
    private String confName;

    @ApiModelProperty(value = "要导出的外表名", required = true, dataType = "String", example = "H_TABLE_NAME")
    private String outTable;

    @ApiModelProperty(value = "外表名额外条件", required = false, dataType = "String", example = " and col = 1")
    private String plugSql;

    @ApiModelProperty(value = "要导入的内表名", required = true, dataType = "String", example = "T_TABLE_NAME")
    private String inTable;

    @ApiModelProperty(value = "是否启用", required = true, dataType = "boolean")
    private Boolean enabled;

    private String rules;//字段规则

    @ApiModelProperty(value = "去重列", required = true, dataType = "String")
    private String deduplicate;//去重列

    public SyncConf(){
    }

    public SyncConf(Integer id){
        setId(id);
    }

    public String getOutTable() {
        return outTable;
    }

    public void setOutTable(String outTable) {
        this.outTable = outTable;
    }

    public String getInTable() {
        return inTable;
    }

    public void setInTable(String inTable) {
        this.inTable = inTable;
    }

    public Boolean getEnabled() {
        return enabled == null ? true : enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public JSONArray getRules(){
        if(StringUtils.isBlank(rules)){
            return JSON.parseArray("[]");
        }
        return JSON.parseArray(rules.toLowerCase().replace(",","\",\"").replace(":","\":\"").replace("{","{\"").replace("}","\"}").replace("}\"","}").replace("\"{","{"));
    }

    public void setRules(JSONArray arr){
        this.rules = JSON.toJSONString(arr).replace("\"","");
    }

    private Map<String,String> errMap (String index,String err){
        Map<String,String> m = new HashMap<String,String>();
        m.put("index",index);
        m.put("err",err);
        return m;
    }

    public List check(){
        List<Map> err = new ArrayList<>(0);

        if(StringUtils.isBlank(confName)){
            err.add(errMap("-1","配置名称不能为空"));
        }
        if(StringUtils.isBlank(outTable) || !outTable.matches(chars)){
            err.add(errMap("-2","导出表名为空或不符合规则"));
        }
        if(StringUtils.isBlank(inTable) || !outTable.matches(chars)){
            err.add(errMap("-3","导入表名为空或不符合规则"));
        }
        if(StringUtils.isBlank(deduplicate) || !deduplicate.matches(chars)){
            err.add(errMap("-4","去重列(唯一标识)为空或不符合规则"));
        }

        JSONArray list = getRules();
        if(list == null || list.size() == 0)
            return err;
        Set<String> froms = new HashSet<String>(list.size());
        Set<String> tos = new HashSet<String>(list.size());

        for (int i = list.size() - 1; i >= 0; i--) {
            Map m = (Map) list.get(i);
            String f = (String) m.get("f"),t = (String) m.get("t");
            m.put("index",i);
            if(StringUtils.isBlank(f) || StringUtils.isBlank(t)){
                err.add(m);
                m.put("err", MessageFormat.format("{0}{1}为空",StringUtils.isBlank(f) ? "[转入字段]" : "",StringUtils.isBlank(t) ? "[接收字段]" : ""));
                continue;
            }
            if(f.equals(t)){
                list.remove(i);
                continue;
            }
            if(!f.matches(chars) && !t.matches(chars)){
                err.add(m);
                m.put("err", MessageFormat.format("{0}{1}不符规则",f.matches(chars) ? "" : "[转入字段]",t.matches(chars) ? "" : "[接收字段]"));
                continue;
            }
            if(froms.contains(f) || tos.contains(t)){
                err.add(m);
                m.put("err", MessageFormat.format("{0}{1}有重复",froms.contains(f) ? "[转入字段]" : "",tos.contains(t) ? "[接收字段]" : ""));
                continue;
            }
            m.remove("index");
            froms.add(f);
            tos.add(t);
        }
        froms.clear();tos.clear();
        if(err.size() == 0)
        setRules(list);
        err.forEach(m -> {m.remove("f");m.remove("t");});
        return err;
    }

    public String getDeduplicate() {
        return deduplicate;
    }


    public String getInDeduplicate() {
        for(Object map:getRules()){
            if(map instanceof Map){
                if(deduplicate.toLowerCase().equals(((Map) map).get("f"))){
                    return (String) ((Map) map).get("t");
                }
            }
        }
        return deduplicate;
    }

    public void setDeduplicate(String deduplicate) {
        this.deduplicate = deduplicate;
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public String getPlugSql() {
        return plugSql;
    }

    public void setPlugSql(String plugSql) {
        this.plugSql = plugSql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SyncConf)) return false;
        SyncConf that = (SyncConf) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
