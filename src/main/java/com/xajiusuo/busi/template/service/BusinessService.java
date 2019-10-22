package com.xajiusuo.busi.template.service;

import com.xajiusuo.busi.template.entity.Business;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;


public interface BusinessService extends BaseService<Business, Integer> {

    Page<Business> findPageByEntity(Pageable pageable, Business manageTemplates);

    Business autoSaveByName(String name, HttpServletRequest request);
}
