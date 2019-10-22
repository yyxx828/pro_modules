package com.xajiusuo.busi.template.service;

import com.xajiusuo.busi.template.entity.IntoRecord;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IntoRecordService extends BaseService<IntoRecord, Integer> {

    Page<IntoRecord> findPageByEntity(Pageable pageable, IntoRecord manageTemplates);

}
