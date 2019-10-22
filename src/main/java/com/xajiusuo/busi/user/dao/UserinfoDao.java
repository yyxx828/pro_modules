package com.xajiusuo.busi.user.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.entity.Userinfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Date;
import java.util.List;

/**
 * @Author zlm
 * @Date 2018/1/17
 * @Description 数据层
 */
public interface UserinfoDao extends BaseDao<Userinfo,Integer>,JpaSpecificationExecutor<Userinfo> {

    /***
     * @Author zlm
     * @Description 根据id和删除状态进行查询用户
     * @Date 2018/1/23 13:35
     * @param id
     * @param del
     * @return
     */
    Userinfo findByIdAndDelFlag(Integer id, Boolean del);

    /***
     * @Author zlm
     * @Description 根据删除状态和启用状态进行用户列表查询
     * @Date 2018/1/20 13:36
     * @param del
     * @param able
     * @return
     */
    List<Userinfo> findByDelFlagAndAble(Boolean del, Boolean able);

    /***
     * @Author zlm
     * @Description 根据机构和删除状态进行用户列表查询
     * @Date 2018/1/23 17:37
     * @param depart
     * @param del
     * @return
     */
    List<Userinfo> findByDepart_IdAndDelFlag(Integer depart, Boolean del);

    /***
     * @Author zlm
     * @Description 根据职位查询未删除的用户列表
     * @Date 2018/1/24 10:58
     * @param dutyId
     * @return
     */
    List<Userinfo> findByDuty_IdAndDelFlagFalse(Integer dutyId);

    /***
     * @Author zlm
     * @Description 查询大于等于修改时间的用户列表
     * @Date 2018/1/23 13:38
     * @param optTime
     * @return
     */
    List<Userinfo> findByLastModifyTimeGreaterThanEqual(Date optTime);

    /***
     * @desc 通过机构职位查询用户列表
     * @author 杨勇 18-2-2 下午3:35
     * @param depId 机构id
     * @param dutyId 职务id
     * @return
     */
    List<Userinfo> findByDepartAndDutyAndDelFlagFalse(Depart depId, Duty dutyId);

    Userinfo findByUsernameAndPassword(String username,String password);

    /***
     * 从张李梅用户复制
     * 杨勇 19-4-9
     * @param cardId
     * @return
     */
    Userinfo findByCardIdAndDelFlagAndAble(String cardId, Boolean del, Boolean able);
}
