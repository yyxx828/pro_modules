package com.xajiusuo.busi.template.service.impl;

import com.xajiusuo.busi.template.dao.BusFieldDao;
import com.xajiusuo.busi.template.entity.BusField;
import com.xajiusuo.busi.template.entity.Business;
import com.xajiusuo.busi.template.service.BusFieldService;
import com.xajiusuo.busi.template.service.BusinessService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.CfI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class BusFieldServiceImpl extends BaseServiceImpl<BusField, Integer> implements BusFieldService {


    @Autowired
    private BusFieldDao entityRepository;

    @Autowired
    private BusinessService businessService;

    @Override
    public BaseDao<BusField, Integer> getBaseDao() {
        return entityRepository;
    }

    @Override
    public Page<BusField> findPageByEntity(Pageable pageable, BusField entity) {
        String sql = BeanUtils.beanToSql(entity);
        sql += " order by " + pageable.getSort().toString().replaceAll(":", "");
        return entityRepository.executeQuerySqlByPage(pageable,sql);
    }

    @Override
    public Result save(BusField entity, Integer orderType, Map<String, Object> currentInfo) {

        if(orderType == null){
            orderType = 1;
        }
        int oldOrder = -1;
        Business bus = null;

        if(entity.getId() == null){//新增
            if(entity.getBusiness() == null || entity.getBusiness().getId() == null){
                return Result.find(CfI.R_TEMPLATE_BUINESSNOTEXIST_FAIL);
            }
            bus = businessService.getOne(entity.getBusiness().getId());
            if(bus == null){
                return Result.find(CfI.R_TEMPLATE_BUINESSNOTEXIST_FAIL);
            }
            entity.setFromType(0);//新增方式为手动添加

            Integer order = findBy("business",bus).size() + 1;

            oldOrder = order;
            if(entity.getOrders() == null || entity.getOrder() == 0){
                entity.setOrders(order);
            }
        }else{//修改
            BusField bf = getOne(entity.getId());
            if(bf == null || bf.getBusiness() == null || bf.getBusiness().getId() == null){
                return Result.find(CfI.R_TEMPLATE_BUINESSNOTEXIST_FAIL);
            }
            bus = businessService.getOne(bf.getBusiness().getId());
            if(bus == null || bus.getId() == null){
                return Result.find(CfI.R_TEMPLATE_BUINESSNOTEXIST_FAIL);
            }

            entity.setBusiness(bus);
            oldOrder = bf.getOrder();

            if(bf.getFromType() == null)//一键生成
                bf.setFromType(1);
            entity.setFromType(bf.getFromType());
            if(entity.getOrders() == null || entity.getOrder() == 0){
                entity.setOrders(bf.getOrder());
            }
        }

        //顺序调整,和原先顺序不一样进行整体顺序调整 2 - 5
        if(entity.getOrder() != oldOrder){
            if(orderType != 0){//互换
                BusField other = findBy("orders",entity.getOrders()).get(0);
                other.setOrders(oldOrder);
                save(other);
            }else{//插入
                int min = entity.getOrder() < oldOrder ? entity.getOrder() : oldOrder;
                int max = entity.getOrder() > oldOrder ? entity.getOrder() : oldOrder;
                List<BusField> list = executeNativeQuerySql("select * from " + tableName() + " where bsid = ? and orders >= ? and orders <= ? order by orders", bus.getId(), min,max);
                if(entity.getOrder() == min){//上面插入
                    list.remove(list.size() - 1);
                    for(int i = 0;i<list.size();i ++){
                        list.get(i).setOrders(min + 1 + i);
                    }
                }else{//下面插入
                    list.remove(0);
                    for(int i = 0;i<list.size();i++){
                        list.get(i).setOrders(min + i);
                    }
                }
                for(BusField b:list){
                    save(b);
                }
            }
        }

        entity.getBusiness().setNewOrder(orderType + "," + entity.getOrders() + "," + oldOrder);
        save(entity);
        return Result.SAVE_SUCCESS.setData(entity);
    }

    @Override
    public void delete(Integer id) {
        BusField entity = getOne(id);
        List<BusField> bus = executeNativeQuerySql("select * from " + SqlUtils.tableName(BusField.class) + " where bsid = ? and orders > ? order by orders" , entity.getBusiness().getId(), entity.getOrders());
        int index = entity.getOrder();
        for(BusField b:bus){
            b.setOrders(index++);
            save(b);
        }

        entity.getBusiness().setNewOrder("-1," + entity.getOrders() + ",0");
        businessService.save(entity.getBusiness());

        super.delete(id);
    }
}
