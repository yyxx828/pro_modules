package com.xajiusuo.busi.user.service;

import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.vo.SelectedVo;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.utils.Node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * @author zlm
 * @Date 2018/1/18
 * @Description 机构逻辑接口，由于机构表存在逻辑删除，因此所有基础crud操作都需要额外实现,除非特别指明，所有的操作都是针对未逻辑删除的机构
 */
public interface DepartService extends BaseService<Depart, Integer> {


     /**
      * 根据Id 查询机构
      * @param id 机构id
      * @return 机构
      */
     Depart findById(Integer id);

     /**
      * 查询所有机构
      * @return 机构列表
      */
     List<Depart> findAllByDel(Boolean del);
     /**
      * 查询所有机构
      * @param pageable 分页对象
      * @return 机构分页
      */
     Page<Depart> findAllByDel(Pageable pageable);

     /**
      * 根据父机构Id查询所有子机构
      * @param departId 父机构Id
      * @return 子机构列表
      */
     List<Depart> findByParentId(Integer departId);

     /**
      * 机构的逻辑删除，当机构存在子机构或用户时禁止删除，弹出警告
      * @param depart 机构
      * @return 返回前台的消息实体
      */
     Result logicDelete(Depart depart);

     /**
      * 查询所有最后修改时间大于输入时间的机构（包括逻辑删除的机构）
      * @param optTime 机构的最后修改时间
      * @return 机构列表
      */
     List<Depart> findGeOptTime(Date optTime);

     /**
      * 查询机构高级查询
      * @param depart 机构
      * @param pageable 分页
      * @return 机构分页
      */
     Page<Depart> queryDepart(Depart depart, Pageable pageable);

     /**
      * 获得机构下挂用户的机构用户树
      * @return 树
      */
     Node getDepartUserTree();
    /**
     * 获得机构树
     * @return 树
     */
    Node getDepartTree();

     /**
      * 校验机构名称是否唯一
      * @param dname 机构名称
      * @param id 机构记录id
      * @return 唯一：true；不唯一：false
      */
    Boolean isValidDname(String dname,Integer parentid, Integer id);

     /**
      * 机构转成node
      * @param depart 机构
      * @param parentId 父节点id
      * @return 树节点
      */
     Node departToNode(Depart depart, Integer parentId);

     /**
      * 根据父机构Id查询所有子机构(不包括本机构)
      * @author  fanhua
      * @param departId 父机构Id
      * @return 子机构列表
      */
     List<Depart> getDeptsByPId(Integer departId);

    /**
     * 根据父机构Id获取本机构及所有父机构id集合
     * @author  fanhua
     * @param departId 父机构Id
     * @return 本机构及所有父机构id集合
     */
    String[] getDeptsIdsByPId(Integer departId);

    public Integer calcDlevel(Integer pid) ;

    /***
     * @Author zlm
     * @Description 根据级别和删除状态查询部门集合
     * @Date 2018/3/29 14:30
     * @param dlevel
     * @param del
     * @return
     */
    public  List<Depart> findByDlevelAndDel(Integer dlevel,Boolean del);

    /*@Author:liangxing
     *@Description:根据部门id查询部门树以及用户
     *@Date:2018-8-31 16:47
     * */
    Node getDepartUserTreeByDid(Integer departId);

    List<SelectedVo> getUserUnderDepartTree();
}
