package com.xajiusuo.busi.fileClient.service.impl;

import com.xajiusuo.busi.fileClient.dao.MultiFileDao;
import com.xajiusuo.busi.fileClient.entity.MultiFile;
import com.xajiusuo.busi.fileClient.service.MultiFileService;
import com.xajiusuo.busi.out.service.RemoteService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.ResponseBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * @author 杨勇 19-7-2
 *  导入记录
 */
@Service
public class MultiFileServiceImpl extends BaseServiceImpl<MultiFile, String> implements MultiFileService {

    /***
     * 文件服务器IP
     */
    @Value(value = "${fileService.ip}")
    private String fileServiceIp;

    /***
     * 文件服务帐号
     */
    @Value(value = "${fileService.account}")
    private String fileServiceAccount;

    /***
     * 文件服务密码
     */
    @Value(value = "${fileService.password}")
    private String fileServicePassword;

    private static String sessionId = "";

    @Autowired
    private MultiFileDao entityRepository;

    @Autowired
    private RemoteService service;


    private RestTemplate restTemplate = new RestTemplate();


    private static String tempPath = null;

    @Override
    public BaseDao<MultiFile, String> getBaseDao() {
        return entityRepository;
    }

    public String getTempPath(){
        if(StringUtils.isBlank(tempPath)){
            String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replace("file:","");
            File parent = new File(path);
            if(path.contains(".jar!") || path.contains(".war")){
                while(parent.getAbsolutePath() != null &&(parent.getAbsolutePath().contains(".jar!") || parent.getAbsolutePath().contains(".war"))){
                    parent = parent.getParentFile();
                }
                tempPath = parent.getParentFile().getAbsolutePath();
            }else{
                tempPath = parent.getParentFile().getParentFile().getParent();
            }
            tempPath += "/temp";
        }
        return tempPath;
    }

    public File crateFile(String exampleName){
        String ext = "";
        if(exampleName != null && exampleName.contains(".")){
            ext = exampleName.substring(exampleName.lastIndexOf("."));
        }
        File file = new File(getTempPath(),System.currentTimeMillis() + (1000 + new Random().nextInt(10000)) + ext);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
        }
        return null;
    }


    public synchronized String validSession(){
        ResponseBean bean = service.getFileService().isLogin(sessionId);
        if(bean.getStatusCode() != 200){
            bean = service.getFileService().login(fileServiceAccount,fileServicePassword);
            if(bean.getStatusCode() == 200){
                if(bean.getData() instanceof Map){
                    sessionId = (String) ((Map) bean.getData()).get("sessionId");
                }
            }else{
                throw new RuntimeException(bean.getMessage());
            }
        }
        if(StringUtils.isBlank(sessionId)){
            throw new RuntimeException("文件系统登陆异常");
        }
        return sessionId;
    }

    public MultiFile uploadFile(MultipartFile file, String url, MultiValueMap<String,Object> param){
        validSession();//服务session校验

        MultiFile entity = null;
        try {
            File uploadFile = crateFile(file.getOriginalFilename());
            FileCopyUtils.copy(file.getInputStream(),new FileOutputStream(uploadFile));
            FileSystemResource fs = new FileSystemResource(uploadFile);
            param.add("file",fs);
            param.add("r",sessionId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String,Object>> httpEntity = new HttpEntity<>(param,headers);
            ResponseEntity<MultiFile> responseEntity = restTemplate.exchange(fileServiceIp + url, HttpMethod.POST, httpEntity, MultiFile.class);
            uploadFile.delete();
            entity = responseEntity.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entity;
    }


    public InputStream getPicInputStream(MultiFile entity) throws Exception{
        GetMethod get = new GetMethod(fileServiceIp + MessageFormat.format( "/api/see/showPathPic/{0}?r={1}",entity.getFilePath(),getSessionId()));
        HttpClient client = new HttpClient();
        client.executeMethod(get);
        return get.getResponseBodyAsStream();
    }


    public InputStream getPathInputStream(MultiFile entity) throws Exception{
        GetMethod get = new GetMethod(fileServiceIp + MessageFormat.format( "/api/see/viewPath/{0}?r={1}",entity.getFilePath(),getSessionId()));
        HttpClient client = new HttpClient();
        client.executeMethod(get);
        return get.getResponseBodyAsStream();
    }

    public String getVideoUrl(MultiFile entity){
        return fileServiceIp + MessageFormat.format( "/api/see/playPath/{0}?r={1}",entity.getFilePath(),getSessionId());
    }

    public InputStream getDownInputStream(MultiFile entity) throws Exception{
        GetMethod get = new GetMethod(fileServiceIp + MessageFormat.format( "/api/see/downPath/{0}?r={1}",entity.getFilePath(),getSessionId()));
        HttpClient client = new HttpClient();
        client.executeMethod(get);
        return get.getResponseBodyAsStream();
    }

    public boolean fileExist(MultiFile entity){
        Object res = service.getFileService().fileExist(entity.getType(),entity.getParentPath(),entity.getFileSaveName(),getSessionId());
        if(res instanceof Boolean){
            return (boolean) res;
        }
        return "true".equals(res.toString().toLowerCase()) || "1".equals(res.toString().toLowerCase());
    }

    @Override
    public byte[] getFileBytes(MultiFile file){
        return restTemplate.getForEntity(fileServiceIp + "/api/see/downPath/" + file.getFilePath() + "?r=" + getSessionId(),byte[].class).getBody();
    }

    @Override
    public File downFile(MultiFile multiFile, File file) {
        file.getParentFile().mkdirs();
        try {
            Files.write(Paths.get(file.getAbsolutePath()), Objects.requireNonNull(getFileBytes(multiFile),"未获取到上传的文件"));
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File downFile(MultiFile multiFile, String filePath) {
        return downFile(multiFile,new File(filePath));
    }

    @Override
    public String getSessionId() {
        validSession();
        return sessionId;
    }

    public String r(){
        return "?r=" + getSessionId();
    }

    public String r1(){
        return "r=" + getSessionId();
    }

    public String r2(){
        return "&r=" + getSessionId();
    }
}
