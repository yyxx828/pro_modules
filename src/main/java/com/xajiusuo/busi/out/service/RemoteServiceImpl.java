package com.xajiusuo.busi.out.service;

import com.xajiusuo.busi.out.configuration.RemoteConfigurationProperties;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.utils.CfI;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: 杨勇
 * @Description:
 * @Date:Created 2019/7/8
 * @Modified By:
 */
@Service
public class RemoteServiceImpl implements RemoteService {
    @Autowired
    private RemoteConfigurationProperties remoteConfigurationProperties;

    /***
     * 文件服务器IP
     */
    @Value(value = "${fileService.ip}")
    private String fileServiceIp;

    @Override
    public FileService getFileService() {
        return builder(FileService.class, fileServiceIp);
    }


    public <T> T builder(Class<T> t, String url) {
        return Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder()).options(new Request.Options(remoteConfigurationProperties.getRequestTime(), remoteConfigurationProperties.getResponseTime())).retryer(new Retryer.Default(5000, 5000, 3)).target(t, url);
    }
}
