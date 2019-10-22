package com.xajiusuo.busi.note.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.note.entity.Note;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteService extends BaseService<Note,String> {



    Note saveOrUpdateNote(Note note, UserInfoVo user);

    /**
     *  公告的分页
     * @param pageable
     * @param note
     * @param release
     * @return
     */
    Page queryNoteForPage(Pageable pageable, Note note, boolean release);


    List queryNoteForList();

}
