package com.xajiusuo.busi.entranceandexit.service;

import com.xajiusuo.busi.entranceandexit.entity.FollowOwnerVo;
import com.xajiusuo.busi.entranceandexit.entity.FollowPerson;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonDetialsVo;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonRelationVo;
import com.xajiusuo.busi.entranceandexit.vo.FollowPersonVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/17.
 */
public interface FollowPersonService extends BaseService<FollowPerson,String>{


    /**
     * @Author shirenjing
     * @Description 
     * @Date 16:25 2019/6/25
     * @Param [pageable, followPersonVo]
     * @return
     **/
    Page<Map> queryPageFollowPerson(Pageable pageable, FollowPersonVo followPersonVo);

    /**
     * @Author shirenjing
     * @Description 获取关注人员详情
     * @Date 16:13 2019/6/20
     * @Param [fpId] 关注人员id
     * @return
     **/
    FollowPersonDetialsVo queryDetials(String fpId);

    /**
     * @Author shirenjing
     * @Description 获取关注人员亲属信息
     * @Date 18:28 2019/6/20
     * @Param [idcard] 关注人员证件编号
     * @return
     **/
    List<FollowPersonRelationVo> queryRelations(String idcard);

    /**
     * @Author shirenjing
     * @Description 获取未关注业主信息查询
     * @Date 14:14 2019/6/20
     * @Param [fieldName, fieldValue]
     * @return
     **/
    List<FollowOwnerVo> queryUnFollowOwner(String fieldName, String fieldValue);


}
