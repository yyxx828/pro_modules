package com.xajiusuo.busi.fileClient.controller;

import com.xajiusuo.busi.fileClient.entity.MultiFile;
import com.xajiusuo.busi.fileClient.service.MultiFileService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

/**
 * Created by 杨勇 on 19-7-2.
 */
@Slf4j
@Api(description = "文件管理")
@RestController
@RequestMapping(value = "api/village/upload")
public class UploadController extends BaseController {

    @Autowired
    private MultiFileService multiFileService;

    /***
     * @return
     * @desc 文件上传
     * @author 杨勇 19-8-14
     */
    @ApiOperation(value = "文件上传", notes = "文件上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "当前页码，从1开始", allowableValues = "STATIC,RECORD,TEMP", paramType = "path", dataType = "string"),
    })
    @RequestMapping(value = "/uploadFile/{type}", method = RequestMethod.POST)
    public ResponseBean uploadFile(@RequestPart(name = "file",required=true) MultipartFile file,@PathVariable(value = "type") String type) {
        MultiFile entity;
        try{
            entity = multiFileService.uploadFile(file, MultiFileService.Type.valueOf(type));
            if(entity.getStatusCode() != 200){
                return Result.UPLOAD_FAIL.toBean(entity);
            }
            entity.setFileName(file.getOriginalFilename());
            multiFileService.save(entity);
            return Result.UPLOAD_SUCCESS.toBean(entity);
        }catch (Exception e){
            return Result.UPLOAD_FAIL.toBean(e.getMessage());
        }
    }

    /***
     * @return
     * @desc 文件断点上传
     * @author 杨勇 19-8-14
     */
    @ApiOperation(value = "文件断点上传", notes = "文件断点上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "当前页码，从1开始", allowableValues = "STATIC,RECORD,TEMP", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "fileName", required = false, value = "临时文件名,首次不需要传入,系统生成后必须带入", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "md5", required = false, value = "前端一边生成md5,生成后传入后台", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pos", required = true, value = "文件传入起始位置", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "totalSize", required = true, value = "文件总大小", paramType = "query", dataType = "long"),
    })
    @RequestMapping(value = "/uploadPartFile/{type}", method = RequestMethod.POST)
    public Object uploadPartFile(@RequestPart(name = "file",required=true) MultipartFile file,@PathVariable(value = "type") String type,String fileName,String md5,Long pos,Long totalSize) {
        MultiFile entity = null;
        try{
            entity = multiFileService.uploadPartFile(file, MultiFileService.PartType.valueOf(type),fileName,md5,pos,totalSize);
            if(entity.getStatusCode() != 200){
                return Result.REQUEST_FAIL.toBean(entity);
            }
            return entity;
        }catch (Exception e){
            return Result.REQUEST_FAIL.toBean(e.getMessage());
        }
    }

    /***
     * @return
     * @desc 图片预览
     * @author 杨勇 19-7-31
     */
    @ApiOperation(value = "图片预览", notes = "图片预览", httpMethod = "GET")
    @RequestMapping(value = "/showPic/{id}", method = RequestMethod.GET)
    public String showPic(@PathVariable(value = "id") String id) {
        try {
            MultiFile entity = multiFileService.get(id);
            String content = entity.getContentType();
            if(content.startsWith("image")){
                fileDownOrShow("", entity.getContentType(),multiFileService.getPicInputStream(entity));
            }else {
                throw new RuntimeException("该文件不是图片文件");
            }
        } catch (Exception e) {
            if(e instanceof FileNotFoundException){
                throw new RuntimeException("文件系统无该文件信息.");
            }else{
                throw new RuntimeException(e);
            }
        }
        return "NONE";
    }

    /***
     * @return
     * @desc 文档预览
     * @author 杨勇 19-7-31
     */
    @ApiOperation(value = "文档预览", notes = "文档预览", httpMethod = "GET")
    @RequestMapping(value = "/preview/{id}", method = RequestMethod.GET)
    public String preview(@PathVariable(value = "id") String id) {
        MultiFile entity = multiFileService.get(id);
        try {
            fileDownOrShow("", entity.getContentType(),multiFileService.getPathInputStream(entity));
        } catch (Exception e) {
            if(e instanceof FileNotFoundException){
                throw new RuntimeException("文件系统无该文件信息.");
            }else{
                throw new RuntimeException(e);
            }
        }
        return "NONE";
    }

    /***
     * @return
     * @desc 音视频路径拖动播放
     * @author 杨勇 19-7-31
     */
    @ApiOperation(value = "音视频路径拖动播放", notes = "音视频路径拖动播放", httpMethod = "GET")
    @RequestMapping(value = "/playVideo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean playVideo(@PathVariable(value = "id") String id) {
        try {
            return Result.QUERY_SUCCESS.toBean(multiFileService.getVideoUrl(multiFileService.get(id)));
        } catch (Exception e) {
            return Result.QUERY_FAIL.toBean(e.getMessage());
        }
    }

    /***
     * @return
     * @desc 音视频路径拖动播放
     * @author 杨勇 19-7-31
     */
    @ApiOperation(value = "音视频路径拖动播放", notes = "音视频路径拖动播放", httpMethod = "GET")
    @RequestMapping(value = "/play/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String play(@PathVariable(value = "id") String id) {
        try {
            String url = multiFileService.getVideoUrl(multiFileService.get(id));
            return "<html><body style=\"overflow:hide;margin:0 0 0 0;\"><iframe src=\"" + url + "\" style=\"width:100%;height:100%;border:0;\"></body></html>";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /***
     * @return
     * @desc 文件下载
     * @author 杨勇 19-7-31
     */
    @ApiOperation(value = "文件下载", notes = "文件下载", httpMethod = "GET")
    @RequestMapping(value = "/down/{id}", method = RequestMethod.GET)
    public String down(@PathVariable(value = "id") String id) {
        try {
            MultiFile entity = multiFileService.get(id);
            fileDownOrShow(entity.getFileName(), entity.getContentType(),multiFileService.getDownInputStream(multiFileService.get(id)));
        } catch (Exception e) {
            if(e instanceof FileNotFoundException){
                throw new RuntimeException("文件系统无该文件信息.");
            }else{
                throw new RuntimeException(e);
            }
        }
        return "NONE";
    }

}