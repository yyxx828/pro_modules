package com.xajiusuo.busi.note.dao;

import com.xajiusuo.busi.note.entity.Note;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NoteDao extends BaseDao<Note,String>,JpaSpecificationExecutor<Note> {
}
