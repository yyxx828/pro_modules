package com.xajiusuo.busi.entranceandexit.service;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceCard;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

/**
 * Created by wangdou on 2019/6/6
 */
public interface GateEntranceCardService extends BaseService<GateEntranceCard, String> {
    Page<GateEntranceCard> query(GateEntranceCard gateEntranceCard, Pageable page);

    List uploadAccessCarExcel(InputStream in) throws Exception;
}
