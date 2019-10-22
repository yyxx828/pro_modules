package com.xajiusuo.busi.note.service.impl;

import com.xajiusuo.busi.note.dao.NoteDao;
import com.xajiusuo.busi.note.entity.Note;
import com.xajiusuo.busi.note.service.NoteService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NoteServiceImpl extends BaseServiceImpl<Note, String> implements NoteService {

    @Autowired
    private NoteDao noteDao;


    @Override
    public BaseDao<Note, String> getBaseDao() {
        return noteDao;
    }

    @Override
    public Note saveOrUpdateNote(Note newEntity, UserInfoVo user) {
        if (null != user) {
            if (null != newEntity && StringUtils.isNotBlank(newEntity.getId())) {
                Note entity = noteDao.getOne(newEntity.getId());
                entity.setNoteTitle(newEntity.getNoteTitle());
                entity.setExperDays(newEntity.getExperDays());
                entity.setContent(newEntity.getContent());
                entity.setRelease(newEntity.getRelease());
                entity.setRoof(newEntity.getRoof());
                return noteDao.update(entity);
            } else {
                newEntity.setAuthor(user.getFullName());
                return noteDao.save(newEntity);
            }
        }
        return null;
    }

    @Override
    public Page queryNoteForPage(Pageable pageable, Note note, boolean release) {
        //历史数据处理
        String sql = MessageFormat.format("select * from {0} e where e.release is null or e.roof is null", tableName());
        List<Note> tempList = executeNativeQuerySql(sql);
        for(Note n:tempList){
            if(n.getRelease() == null){
                n.setRelease(true);
            }
            if(n.getRoof() == null){
                n.setRoof(false);
            }
            noteDao.save(n);
        }

        StringBuffer sb = new StringBuffer(MessageFormat.format( "select * from {0} where release = ?", SqlUtils.tableName(Note.class)));
        List<String> sqlList = new ArrayList(0);
        if (StringUtils.isNotEmpty(note.getNoteTitle())) {
            sb.append(" and notetitle like '%" + note.getNoteTitle() + "%' ");
        }
        sb.append(" order by roof desc,lastModifyTime desc");
        try {
            Page<Note> pager = executeNativeQuerySqlForPage(pageable,sb.toString(), release);
            for(Note n:pager.getContent()){
                n.setContent(null);
            }
            return pager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List queryNoteForList() {
        String sql = MessageFormat.format("select * from {0} e where e.release = ? and (e.roof = ? or createTime + e.experDays > sysdate) order by roof desc,createTime desc", tableName());
        List<Note> list = executeNativeQuerySql(sql, true,true);
        for (Note note : list) {
            note.setContent(null);
        }
        return list;
    }


}
