package com.xajiusuo.busi.out.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * 远程服务地址配置
 *
 * @author lizhidong
 * @date 2018年5月24日15:26:26
 */
@Component
@ConfigurationProperties(prefix = "spring.remoteservice", ignoreUnknownFields = false)
public class RemoteConfigurationProperties {

    @NotNull
    private String dataip;

    @NotNull
    private String requestTime;

    @NotNull
    private String responseTime;

    public String getDataip() {
        return dataip;
    }

    public void setDataip(String dataip) {
        this.dataip = dataip;
    }



    public int getRequestTime() {
        return requestTime != null ? Integer.parseInt(requestTime) : 10000;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public int getResponseTime() {
        return responseTime != null ? Integer.parseInt(responseTime) : 60000;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

}
