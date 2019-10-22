package com.xajiusuo.busi.note.controller;

import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.busi.note.entity.Note;
import com.xajiusuo.busi.note.service.NoteService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 孙凯
 * 杨勇修改 19-04-10
 * Created by hadoop on 18-1-19.
 */
@Slf4j
@Api(description = "公告管理")
@RestController
@RequestMapping(value = "api/village/note")
public class NoteController extends BaseController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private MsgService msgService;

    /***
     * @return
     * @desc 公告新增
     * @author martin 18-10-16
     */
    @ApiOperation(value = "[01] - 公告新增", httpMethod = "POST", response = Note.class)
    @PostMapping(value = "/saveNote")
    @ResponseBody
    public ResponseBean saveNote(@RequestBody Note note) {
        UserInfoVo userinfo = UserUtil.getCurrentUser(request);
        note.setRelease(note.getRelease() == null ? false : note.getRelease());
        note.setRoof(note.getRoof() == null ? false : note.getRoof());
        boolean fb = false;//发布标记
        boolean must = note.getMust() == null ? false : note.getMust();
        if (null != userinfo && userinfo.getUserId() != null) {
            if (StringUtils.isBlank(note.getId())) {
                note.setId(null);
                fb = note.getRelease() == null ? false : note.getRelease();
            } else {
                Note old = noteService.getOne(note.getId());
                old.setRelease(old.getRelease() == null ? false : old.getRelease());
                if (note.getRelease()) {//发布状态,且之前时未发布状态
                    if (!old.getRelease()) {
                        fb = true;
                        note.setCreateTime(new Date());
                    }
                } else if (old.getRelease()) {
                    note.setRelease(true);
                }
            }
            Note entity = noteService.saveOrUpdateNote(note, userinfo);

            if (fb) {//发布
                msgService.sendMessage(Note.conf, note.getNoteTitle(), note.getId(), null, must, null, null);//公告消息提醒
            }
            return Result.SUBMIT_SUCCESS.toBean(entity);
        } else {
            return Result.find(CfI.R_USER_NOTENOUSER_FAIL).toBean();
        }
    }

    /***
     * @return
     * @desc 公告新增
     * @author martin 18-10-16
     */
    @ApiOperation(value = "[02] - 公告置顶", httpMethod = "POST", response = Note.class)
    @PostMapping(value = "/topNote/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "公告id", paramType = "path", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean topNote(@PathVariable(value = "id") String id) {
        List<Note> list = noteService.findBy("roof", true);
        if (list.size() >= 10) {
            return Result.find(CfI.R_NOTE_TOPTEN_FAIL).toBean();
        }
        Note entity = noteService.getOne(id);
        if (entity == null) {
            log.error("公告信息不存在:" + id);
            return Result.find(CfI.R_NOTE_NOTEXIST_FAIL).toBean();
        }

        if (entity.getRelease() == null || !entity.getRelease()) {
            return Result.find(CfI.R_NOTE_NOTOP_FAIL).toBean();
        }
        if (entity.getRoof() == null && entity.getRoof()) {
            return Result.find(CfI.R_NOTE_ISTOP_FAIL).toBean();
        }

        entity.setRoof(true);
        noteService.save(entity);
        return Result.find(CfI.R_NOTE_TOP_SUCCESS).toBean(entity);
    }


    /***
     * @return
     * @desc 公告新增
     * @author martin 18-10-16
     */
    @ApiOperation(value = "[03] - 公告取消置顶", httpMethod = "POST", response = Note.class)
    @PostMapping(value = "/untopNote/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "公告id", paramType = "path", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean untopNote(@PathVariable(value = "id") String id) {
        Note entity = noteService.getOne(id);
        if (entity.getRoof() == null || !entity.getRoof()) {
            return Result.find(CfI.R_NOTE_ISUNTOP_FAIL).toBean();
        }

        entity.setRoof(false);
        noteService.save(entity);
        return Result.find(CfI.R_NOTE_UNTOP_SUCCESS).toBean(entity);
    }

    @ApiOperation(value = "[04] - 公告修改", httpMethod = "POST", response = Note.class)
    @PostMapping(value = "/updateNote")
    @ResponseBody
    public ResponseBean updateNote(@RequestBody Note note) {
        return saveNote(note);
    }

    @ApiOperation(value = "查询公告信息", httpMethod = "GET")
    @RequestMapping(value = "/queryNoteForPage", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", allowMultiple = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "title", required = false, value = "", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "release", required = false, allowableValues = "true,false", defaultValue = "true", paramType = "query", dataType = "boolean"),
    })
    @ResponseBody
    public ResponseBean queryNoteForPage(Pageable pageable, String title, Boolean release) {
        if (release == null) {
            release = true;
        }
            Page result = noteService.queryNoteForPage(pageable, new Note(title), release);
            return Result.QUERY_SUCCESS.toBean(result);
    }

    @RequestMapping(value = "/deleteNote/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "[05] - 删除公告信息", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "公告id", paramType = "path", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean deleteNote(@PathVariable(value = "id") String id) {
        noteService.delete(id);
        return Result.DELETE_SUCCESS.toBean();
    }

    @ApiOperation(value = "[06] - 查看公告信息", httpMethod = "GET")
    @RequestMapping(value = "/viewNote/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "公告id", paramType = "path", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean viewNote(@PathVariable(value = "id") String id) {
        Note note = noteService.getOne(id);
        if (note != null) {
            UserInfoVo user = UserUtil.getCurrentUser(request);
            if (user != null && user.getUserId() != null) {
                msgService.readMsg(Note.conf, user.getUserId(), id);//公告查看对订阅消息标记已读
            }
            return Result.QUERY_SUCCESS.toBean(note);
        }
        return Result.QUERY_FAIL.toBean();
    }

    @ApiOperation(value = "[07] - 查询符合有效期的公告信息", httpMethod = "GET")
    @RequestMapping(value = "/queryNote", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean queryNoteForList(HttpServletRequest request) {
        List list = noteService.queryNoteForList();
        return Result.QUERY_SUCCESS.toBean(list);
    }
}