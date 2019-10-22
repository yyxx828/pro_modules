package com.xajiusuo.busi.sync.service;

import com.xajiusuo.busi.sync.entity.MultiFile;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * @author 杨勇
 * @Date 2019/7/2
 * @Description 导入记录
 */
public interface MultiFileService extends BaseService<MultiFile, String> {

    /***
     * 获取本地临时文件夹
     * @return
     */
    String getTempPath();


    /***
     * 获取本地临时文件夹
     * @return
     */
    File crateFile(String exampleName);

    /***
     * 文件普通上传
     * @param file 文件
     * @param type 上传存储类型
     * @return
     */
    default MultiFile uploadFile(MultipartFile file, Type type){
        return uploadFile(file,type.getUrl(),new LinkedMultiValueMap());
    }

    /***
     * 文件断点上传
     * @param file 文件碎片
     * @param type 上传存储类型
     * @param fileName 文件名,文件系统存储名称
     * @param md5 对应md5,提供md5和最终文件md5不相同,则文件保存失败
     * @param pos 文件起始位置,起始位置不正确不会进行保存
     * @param totalSize 文件总大小,传送完毕,文件真实大小和该大小不一致,则保存失败
     * @return
     */
    default MultiFile uploadPartFile(MultipartFile file, PartType type,String fileName,String md5,Long pos,Long totalSize){
        MultiValueMap<String,Object> param = new LinkedMultiValueMap();
        param.add("fileName",fileName);
        param.add("md5",md5);
        param.add("pos",pos);
        param.add("totalSize",totalSize);
        return uploadFile(file,type.getUrl(),param);
    }

    MultiFile uploadFile(MultipartFile file,String url,MultiValueMap<String,Object> param);


    /***
     * 获取图片文件流
     * @param entity
     * @return
     */
    InputStream getPicInputStream(MultiFile entity) throws Exception;

    /***
     * 获取文件流
     * @param entity
     * @return
     */
    InputStream getPathInputStream(MultiFile entity) throws Exception;

    /***
     * 获取下载文件流
     * @param entity
     * @return
     * @throws Exception
     */
    InputStream getDownInputStream(MultiFile entity) throws Exception;

    /***
     * 获取视频播放url
     * @param entity
     * @return
     */
    String getVideoUrl(MultiFile entity);

    /***
     * 判断文件是否存在
     * @param entity
     * @return
     */
    boolean fileExist(MultiFile entity);

    /***
     * 获取文件对应流信息
     * @param file
     * @return
     */
    byte[] getFileBytes(MultiFile file);

    /***
     * 文件下载
     * @param multiFile
     * @param file
     */
    File downFile(MultiFile multiFile, File file);

    /***
     * 文件下载
     * @param multiFile
     * @param filePath
     */
    File downFile(MultiFile multiFile, String filePath);

    String getSessionId();



    /***
     * 整体上传
     */
    enum Type {
        /***
         * 长期
         */
        STATIC("/api/upload/uploadStatic"),
        /***
         * 中长期(3个月),后台默认90天,定时清理关闭需要本系统定时触发删除记录
         */
        RECORD("/api/upload/uploadRecord"),
        /***
         * 短期(7天),文件系统会定时删除临时文件
         */
        TEMP("/api/upload/uploadTemp");

        Type(String url){
            this.url = url;
        }

        private String url;

        public String getUrl() {
            return url;
        }
    }

    /***
     * 断点上传
     */
    enum PartType {
        /***
         * 长期
         */
        STATIC("/api/upload/partUploadStatic"),
        /***
         * 中长期(3个月),后台默认90天,定时清理关闭需要本系统定时触发删除记录
         */
        RECORD("/api/upload/partUploadRecord"),
        /***
         * 短期(7天),文件系统会定时删除临时文件
         */
        TEMP("/api/upload/partUploadTemp");

        PartType(String url){
            this.url = url;
        }

        private String url;

        public String getUrl() {
            return url;
        }
    }
}
