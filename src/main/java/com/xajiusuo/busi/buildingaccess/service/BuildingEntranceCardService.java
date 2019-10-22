package com.xajiusuo.busi.buildingaccess.service;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceCard;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by wangdou on 2019/6/6
 */
public interface BuildingEntranceCardService extends BaseService<BuildingEntranceCard, String> {
    Page<BuildingEntranceCard> query(BuildingEntranceCard buildingEntranceCard, Pageable page);
}
