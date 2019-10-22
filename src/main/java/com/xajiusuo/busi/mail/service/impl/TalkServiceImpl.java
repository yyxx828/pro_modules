package com.xajiusuo.busi.mail.service.impl;

import com.xajiusuo.busi.mail.dao.TalkDao;
import com.xajiusuo.busi.mail.entity.Talk;
import com.xajiusuo.busi.mail.send.MsgSendObservable;
import com.xajiusuo.busi.mail.service.TalkService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TalkServiceImpl extends BaseServiceImpl<Talk, String> implements TalkService {

    private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private TalkDao entityRepository;

    @Override
    public BaseDao<Talk, String> getBaseDao() {
        return entityRepository;
    }

    @Override
    public Talk speak(Talk entity) {
        entity.setCreateTime(new Date());
        entityRepository.save(entity);
        entity.setId(null);
        MsgSendObservable.sendMessage(entity);//消息实时推送
        return entity;
    }

    @Override
    public Talk prevListTask(Date date, Integer userId, String key) {
        Talk entity = new Talk();
        List<Talk> list = prevList(date,userId,key,21);
        entity.setList(list);
        if(list.size() >= 21){
            entity.setMin(list.remove(0).getCreateTime().getTime());
        }
        list = nextList(date,userId,key,2);
        if(list.size() >= 2){
            entity.setMax(list.get(1).getCreateTime().getTime());
        }
        return entity;
    }

    @Override
    public Talk nextListTask(Date date, Integer userId, String key) {
        Talk entity = new Talk();
        List<Talk> list = nextList(date,userId,key,21);
        entity.setList(list);
        if(list.size() >= 21){
            entity.setMax(list.remove(20).getCreateTime().getTime());
        }
        list = prevList(date,userId,key,2);
        if(list.size() >= 2){
            entity.setMin(list.get(1).getCreateTime().getTime());
        }
        return entity;
    }

    public List<Talk> prevList(Date date, Integer userId, String key,int rowNum) {
        String sql = MessageFormat.format("select * from ( select e.*,row_number() over (order by createTime desc) num from {0} e where (receiveUserId is null or receiveUserId = ? or createUID = ?) and createTime <= ?{1} ) t where t.num <= ?", SqlUtils.tableName(Talk.class), StringUtils.isBlank(key) ? "" : (MessageFormat.format(" and (content like ''{0}'' or sendUserName like ''{0}'' or receiveUserName like ''{0}'')",SqlUtils.sqlLike(key))));
        List<Talk> list = entityRepository.executeNativeQuerySql(sql, userId,userId,date,rowNum);
        PageImpl<Talk> page = new PageImpl<Talk>(new ArrayList<Talk>());

        if(list == null){
            list = Collections.emptyList();
        }
        Collections.reverse(list);//将反序颠倒
        return markKey(key, list);
    }

    public List<Talk> nextList(Date date, Integer userId, String key,int rowNum) {
        String sql = MessageFormat.format("select * from ( select e.*,row_number() over (order by createTime asc) num from {0} e where (receiveUserId is null or receiveUserId = ? or createUID = ?) and createTime >= ?{1} ) t where t.num <= ?", SqlUtils.tableName(Talk.class), StringUtils.isBlank(key) ? "" : (MessageFormat.format(" and (content like ''{0}'' or sendUserName like ''{0}'' or receiveUserName like ''{0}'')",SqlUtils.sqlLike(key))));
        List<Talk> list = entityRepository.executeNativeQuerySql(sql, userId,userId,date,rowNum);
        if(list == null){
            list = Collections.emptyList();
        }
        return markKey(key, list);
    }

    private List<Talk> markKey(String key, List<Talk> list){
        List<Talk> temp = list;
        if(StringUtils.isNotBlank(key) && list.size() > 0){//对查询内容进行标记
            temp = new ArrayList<Talk>();
            for(Talk t:list){
                Talk t1 = new Talk();
                BeanUtils.copyProperties(t,t1);
                t1.setContent(t.getContent().replace(key,MessageFormat.format("<span class=''find''>{0}</span>",key)));
                temp.add(t1);
            }
        }
        return temp;
    }

    @Override
    public List<String> listTalkDate(Integer userId) {
        String sql = MessageFormat.format("select date from {0} e where (receiveUserId is null or receiveUserId = ? or createUID = ?) group by date", SqlUtils.tableName(Talk.class));
        List<Object[]> list = getBaseDao().listBySQL(sql,userId,userId);
        if(list.size() == 0){
            return Collections.emptyList();
        }
        List<String> dates = list.stream().map(os -> os[0].toString()).collect(Collectors.toList());
        return dates;
    }

    @Override
    @Scheduled(cron = "1 0 0 * * ?")
    public void clearHistory() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        int keepDay = Configs.find(CfI.C_TALK_HISTORY_KEEPDAY).getInt();

        if(keepDay <= 0){
            log.info("聊天历史记录不清除");
            return;
        }

        String sql = MessageFormat.format("delete from {0} e where e.createTime + {1} < sysdate", SqlUtils.tableName(Talk.class),keepDay);
        int num = executeUpdateSql(sql);
        log.info(MessageFormat.format("清理超过[{0} 天]群聊记录[{1}]条",keepDay,num));
    }

}
