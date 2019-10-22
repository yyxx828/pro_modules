package com.xajiusuo.busi.user.service.impl;

import com.xajiusuo.busi.user.dao.DepartDao;
import com.xajiusuo.busi.user.dao.UserinfoDao;
import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.DepartService;
import com.xajiusuo.busi.user.vo.SelectedVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zlm  2018/1/18
 *         机构逻辑接口实现类
 */
@Slf4j
@Service
public class DepartServiceImpl extends BaseServiceImpl<Depart, Integer> implements DepartService {
    @Autowired
    DepartDao departRepository;

    @Autowired
    UserinfoDao userInfoRepository;

    @Override
    public BaseDao<Depart, Integer> getBaseDao() {
        return departRepository;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Depart findById(Integer id) {
        return departRepository.findByIdAndDelFlag(id, false);
    }

    @Override
    public List<Depart> findAllByDel(Boolean del) {
        return departRepository.findByDelFlag(del);
    }

    @Override
    public Page<Depart> findAllByDel(Pageable pageable) {
        return departRepository.findByDelFlag(false, pageable);
    }

    @Override
    public Result logicDelete(Depart depart) {
        try {
            if (null != depart) {
                List<Userinfo> userinfoList;
                userinfoList = userInfoRepository.findByDepart_IdAndDelFlag(depart.getId(), false);
                if (CommonUtils.isEmpty(userinfoList)) {
                    List<Depart> list = departRepository.findByParent_idAndDelFlag(depart.getId(), false);
                    if (CommonUtils.isEmpty(list)) {
                        departRepository.delete(depart);
                        log.info("删除机构" + depart.getDname());
                        return Result.DELETE_SUCCESS;
                    } else {
                        return Result.find(CfI.R_DEPART_EXISTSUBDEPART_FAIL);
                    }
                } else {
                    return Result.find(CfI.R_DEPART_EXISTUSER_FAIL);
                }
            } else {
                return Result.find(CfI.R_DEPART_DEPART_NOTEXIST_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.DELETE_FAIL;
        }
    }

    @Override
    public List<Depart> findGeOptTime(Date optTime) {
        return this.departRepository.findByLastModifyTimeGreaterThanEqual(optTime);
    }

    @Override
    public Page<Depart> queryDepart(final Depart depart, Pageable pageable) {
        String sql = BeanUtils.beanToSql(depart, "departNames", "parent", "parentDname", "delFlag");
/*        sql += " order by " + pageable.getSort().toString();
        sql = sql.replaceAll(":", " ");*/
//        Map<String, Object> map = BeanUtils.beanToMap(depart);
//        return departRepository.findAll((root, query, cb) -> {
//            Predicate p1 = null;
//            for (String key : map.keySet()) {
//                if (!"parentid".equals(key)) {
//                    if (map.get(key) instanceof String) {
//                        Path<String> dnamePath = root.get(key);
//                        p1 = cb.like(dnamePath, SqlUtils.sqlLike(map.get(key).toString()));
//                    }
//                    if (map.get(key) instanceof Integer) {
//                        Path<Integer> path = root.get(key);
//                        p1 = cb.equal(path, map.get(key));
//                    }
//                    if (map.get(key) instanceof Depart){
//                    }
//                }
//
//            }
//            return p1;
//        }, pageable);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("dname", ExampleMatcher.GenericPropertyMatchers.contains());
////                .withMatcher("createtime");
//        Example<Depart> ex = Example.of(depart, matcher);
//        List<Depart> ls = departRepository.findAll(ex);
        return departRepository.executeQuerySqlByPage(pageable, sql);
    }

    @Override
    public Node getDepartUserTree() {
        Depart depart = departRepository.findByParent_idNull();
        List<Userinfo> userinfoList = userInfoRepository.findByDelFlagAndAble(false, true);
        List<Depart> departList = departRepository.findByDelFlag(false);
        Node root = new Node().setId(depart.getId()).setTitle(depart.getDname()).setParentid(0);
        return toNode(root, userinfoList, departList);
    }

    @Override
    public Node getDepartTree() {
        Depart depart = departRepository.findByParent_idNull();
        List<Depart> departList = departRepository.findByDelFlag(false);
        Node root = new Node().setId(depart.getId()).setTitle(depart.getDname()).setParentid(0);
        return toNode(root, departList);
    }

    /**
     * 生成树
     *
     * @param node         node对象
     * @param userinfoList 所有用户
     * @param departList   所有机构
     * @return NODE
     * @author yhl
     * @Date 2018/03/01
     */
    private Node toNode(Node node, List<Userinfo> userinfoList, List<Depart> departList) {
        List<Node> dUser = userinfoList.stream()
                .filter(userinfo -> userinfo.getDepartId().equals(node.getId()) && !userinfo.getDelFlag() && userinfo.getAble())
                .map(userinfo -> userToNode(node, userinfo))
                .collect(Collectors.toList());
        List<Node> dDepart = departList.stream()
                .filter(depart -> depart.getParent() != null)
                .filter(depart1 -> depart1.getParentid().equals(node.getId()) && !depart1.getDelFlag())
                .map(depart1 -> departToNode(depart1, node.getId()))
                .collect(Collectors.toList());
        dDepart.forEach(node1 -> toNode(node1, userinfoList, departList));
        dUser.addAll(dDepart);
        return node.setChildren(dUser);
    }

    private Node userToNode(Node node, Userinfo userinfo) {
        return new Node()
                .setTitle(userinfo.getFullname())
                .setId(userinfo.getId())
                .setParentid(node.getId())
                .setParentTitle(node.getTitle())
                .setIsDepart(false);
    }

    /**
     * 生成树
     *
     * @param node       node对象
     * @param departList 所有机构
     * @return NODE
     * @author yhl
     * @Date 2018/03/01
     */
    private Node toNode(Node node, List<Depart> departList) {
        List<Node> dDepart = departList.stream()
                .filter(depart -> depart.getParent() != null)
                .filter(depart1 -> depart1.getParentid().equals(node.getId()) && !depart1.getDelFlag())
                .map(depart1 -> departToNode(depart1, node.getId()))
                .collect(Collectors.toList());
        dDepart.forEach(node1 -> toNode(node1, departList));
        return node.setChildren(dDepart);
    }

    @Override
    public Boolean isValidDname(String dname, Integer parentid, Integer id) {
        List departList;
        if (null == id) {
            departList = departRepository.findByParent_idAndDname(parentid, dname);
        } else {
            departList = departRepository.findByParent_idAndDnameAndIdNot(parentid, dname, id);
        }
        return CommonUtils.isEmpty(departList);
    }

    @Override
    public Node departToNode(@NotNull Depart depart, Integer parentId) {
        return new Node()
                .setTitle(depart.getDname())
                .setId(depart.getId())
                .setParentid(parentId)
                .setSelected(depart.getDlevel() == 0)
                .setChecked(depart.getDlevel() == 0)
                .setExpand(depart.getDlevel() == 0)
                .setParentTitle(depart.getParentDname());
    }

    @Override
    public List<Depart> findByParentId(Integer id) {
        String sql = MessageFormat.format("select d from {0} d where d.delflag =? and (d.id =? or d.parent.id =?)", tableName());
        return executeNativeQuerySql(sql, false, id, id);
    }


    @Override
    public Integer calcDlevel(Integer pid) {
        if (pid == null) {
            return 1;
        } else {
            Integer ppid = departRepository.getOne(pid).getParentid();
            return calcDlevel(ppid) + 1;
        }
    }

    /**
     * 根据父机构Id查询所有子机构(不包括本机构)
     *
     * @param departId 父机构Id
     * @return 子机构列表
     * @author fanhua
     */
    @Override
    public List<Depart> getDeptsByPId(Integer departId) {
        return departRepository.findByParent_idAndDelFlag(departId, false);
    }

    /**
     * 根据父机构Id获取本机构及所有父机构id集合
     *
     * @param departId 父机构Id
     * @return 本机构及所有父机构id集合
     * @author fanhua
     */
    @Override
    public String[] getDeptsIdsByPId(Integer departId) {
        List<String> departIds = jdbcTemplate.queryForList(MessageFormat.format("select id from {0} start with id =? connect by prior parentid = id order by id", tableName()), new Integer[]{departId}, String.class);
        String[] i = new String[departIds.size()];
        return departIds.toArray(i);
    }

    @Override
    public List<Depart> findByDlevelAndDel(Integer dlevel, Boolean del) {
        return departRepository.findByDlevelAndDelFlag(dlevel, del);
    }

    public Node getDepartUserTreeByDid(Integer departId) {
        Depart depart = departRepository.getOne(departId);
        if (depart != null) {
            List<Userinfo> userinfoList = userInfoRepository.findByDelFlagAndAble(false, true);
            List<Depart> departList = departRepository.findByDelFlag(false);
            Node root = new Node().setId(depart.getId()).setTitle(depart.getDname()).setParentid(0);
            return toNode(root, userinfoList, departList);
        } else {
            return null;
        }
    }

    @Override
    public List<SelectedVo> getUserUnderDepartTree() {
        List<Depart> departList = departRepository.findAll();
        List<Userinfo> userinfoList = userInfoRepository.findAll();
        if (departList.size() > 0 && userinfoList.size() > 0) {
            List<SelectedVo> selectedVoList = new ArrayList<>();
            for (Depart depart : departList) {
                SelectedVo departVo = new SelectedVo();
                departVo.setId(depart.getId());
                departVo.setLabel(depart.getDname());
                departVo.setValue(depart.getId());
                List<SelectedVo> userSelectedVo = new ArrayList<>();
                for (Userinfo userinfo : userinfoList) {
                    SelectedVo userVo = new SelectedVo();
                    if (userinfo.getDepartId() == depart.getId()) {
                        userVo.setId(userinfo.getId());
                        userVo.setValue(userinfo.getId());
                        userVo.setLabel(userinfo.getFullname());
                        userSelectedVo.add(userVo);
                    }
                }
                departVo.setChildren(userSelectedVo);
                selectedVoList.add(departVo);
            }
            return selectedVoList;
        }

        return null;
    }
}
