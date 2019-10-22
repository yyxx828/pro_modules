package com.xajiusuo.busi.template.service;

import com.xajiusuo.busi.template.entity.BusField;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.jpa.param.e.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;


public interface BusFieldService extends BaseService<BusField, Integer> {

    Page<BusField> findPageByEntity(Pageable pageable, BusField manageTemplates);

    Result save(BusField entity, Integer orderType, Map<String, Object> currentInfo);
}
