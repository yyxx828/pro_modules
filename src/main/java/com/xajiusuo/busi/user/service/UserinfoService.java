package com.xajiusuo.busi.user.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.user.entity.Userinfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author zlm
 * @Date 2018/1/17
 * @Description 用户逻辑接口
 */
public interface UserinfoService extends BaseService<Userinfo, Integer> {

    Userinfo getCurrentUser(HttpServletRequest request);
    /***
     * @Author zlm
     * @Description 根据id获得未删除用户
     * @Date 2018/1/23 13:24
     * @param id
     * @return
     */
    Userinfo getUserInfoById(Integer id);

    /***
     * @Author zlm
     * @Description 根据删除状态和启用状态查询用户列表
     * @Date 2018/1/22 17:25
     * @param del
     * @param able
     * @return
     */
    List<Userinfo> findByDelAndAble(Boolean del,Boolean able);

    /***
     * @Author zlm
     * @Description 根据机构查询未删除用户列表
     * @Date 2018/1/23 10:26
     * @param departId
     * @return
     */
    List<Userinfo> findUserinfosByDepartid(Integer departId);

    /***
     * @Author zlm
     * @Description 查询大于等于操作时间的用户数据（时间为null查全部）
     * @Date 2018/1/24 11:47
     * @param optTime
     * @return
     */
    List<Userinfo> findGeOptTime(Date optTime);

    /***
     * @Author zlm
     * @Description 批量导入模板中的用户
     * @Date 2018/2/23 15:32
     * @param sheet
     * @param request
     * @return
     */
    int importUser(Sheet sheet, HttpServletRequest request);

    /***
     * @Author yhl
     * @Description 将excel的行按照用户字段进行映射实体
     * @Date 2018/2/1 14:34
     * @param row
     * @return
     */
    Userinfo rowToUser(Row row);

    /***
     * @Author zlm
     * @Description 根据字段和值查询用户是否存在
     * @Date 2018/2/1 09:29
     * @param name 字段全名
     * @param value 要校验的值
     * @return
     */
    Boolean isExist(String name,Object value);

    /***
     * @Author zlm
     * @Description 根据字段和值查询用户是否存在
     * @Date 2018/2/1 11:30
     * @param name 字段名称
     * @param value 要校验的值
     * @param notId 不包含id
     * @return
     */
    Boolean isExist(String name, Object value ,Integer notId);

    /***
     * @Author zlm
     * @Description 将用户列表导出EXCEL
     * @Date 2018/2/1 16:31
     * @param list
     * @return
     */
    Workbook listToExcel(List<Userinfo> list);


    /***
     * @Author zlm
     * @Description 根据实体字段进行复杂组合sql查询
     * @Date 2018/2/6 14:32
     * @param userinfo
     * @param pageable
     * @param sql
     * @return
     */
    Page<Userinfo> query(Userinfo userinfo, Pageable pageable , String sql);

    /***
     * @Author zlm
     * @Description 根据部门id查询所有子部门包含当前部门下的所有用户
     * @Date 2018/8/20 9:43
     * @param userinfo
     * @param pageable
     * @param sql
     * @return
     */
    Page<Userinfo> queryByAllDepart(Userinfo userinfo, Pageable pageable, String sql);

    /***
     * @Author zlm
     * @Description 根据实体字段进行复杂组合sql查询
     * @Date 2018/5/16 9:12
     * @param userinfo
     * @param sql
     * @return
     */
    List<Userinfo> query(Userinfo userinfo, String sql);

    /***
     * @Author zlm
     * @Description 获取单点登录的用户以及版本相关信息
     * @Date 2018/4/27 10:41
     * @param request
     * @return
     */
    Map<String,Object> getCurrentInfo(HttpServletRequest request) ;

    Userinfo findByUsernameAndPwd(String username,String pwd);

    /***
     * 从张李梅用户复制
     * 杨勇 19-4-9
     * @param cardId
     * @return
     */
    Userinfo findByCardId(String cardId);
}
