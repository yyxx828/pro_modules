package com.xajiusuo.busi.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.utils.SelectList;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 杨勇 on 19-1-2.
 */
@Entity
@ApiModel(value = "INTOTEMP", description = "导入模板")
@Table(name = "T_INTOTEMP_11", catalog = "mailMana")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class IntoTemp extends BaseIntEntity {

    private String tempName;  //模板名称
    private String charSet;   //模板字符编码

    private String fileds;    //模板对应的字段顺序,以','分割,空白则为空字符串

    private String converts;//类型转换 xm:1/0,80>1,81>282>3;...

    private String createname;//创建人

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "bsid")
    private Business business;//业务表

    @Transient
    private List<BusField> useList;//使用列表

    @Transient
    private List<BusField> noUseList;//未使用列表

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getFileds() {
        return fileds;
    }

    public void setFileds(String fileds) {
        this.fileds = fileds;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<BusField> getUseList() {
        return useList;
    }

    public void setUseList(List<BusField> useList) {
        this.useList = useList;
    }

    public List<BusField> getNoUseList() {
        return noUseList;
    }

    public void setNoUseList(List<BusField> noUseList) {
        this.noUseList = noUseList;
    }

    public String getConverts() {
        return converts;
    }

    public void setConverts(String converts) {
        this.converts = converts;
    }

    public void list(BusField bus){
        if(StringUtils.isBlank(converts) || bus == null || StringUtils.isBlank(bus.getfCol())){
            return;
        }
        String pro = bus.getfCol() + ":";
        if(converts.startsWith(pro) || converts.contains(";" + pro)){
            String[] ss = converts.split(";");
            for(String s:ss){
                if(s.startsWith(pro)){
                    bus.setNoToVal(s.contains(":1,"));
                    s = s.substring(s.indexOf(",") + 1);
                    String[] ps = s.split(",");
                    List<SelectList> sl = new ArrayList<SelectList>();
                    Map<String,String> map = new HashMap<String,String>();
                    for(String s1:ps){
                        String[] ss2 = s1.split(">");
                        SelectList l = new SelectList();
                        l.setName(ss2[0]);
                        l.setValue(ss2[1]);
                        sl.add(l);
                        map.put(ss2[0],ss2[1]);
                    }
                    bus.setList(sl);
                    bus.setCvMap(map);
                }
            }
        }
    }

}
