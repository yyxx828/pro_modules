package com.xajiusuo.busi.out.service;

import com.xajiusuo.jpa.util.ResponseBean;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <P>远程服务接口</P>
 * <p>
 * 基于Feign实现，项目中所有的远程接口都定义在此接口中，以内部接口形式定义。
 * 每定义一个用于远程服务定义的内部接口，必须定义一个用于初始化接口对象的方法，此方法必须在实现类里实现。
 * </p>
 *
 * @author yanpeng
 * @date 2018年8月21日11:00:59
 */
public interface RemoteService {

    /***
     * 获取用户管理远程服务接口
     *
     * @return
     */
    FileService getFileService();

    //demo
    interface FileService {

        @RequestLine("GET /api/sys/isLogin?r={sessionId}")
        ResponseBean isLogin(@Param(value = "sessionId") String sessionId);

        @RequestLine("POST /api/sys/login?systemId={account}&pwd={password}")
        ResponseBean login(@Param(value = "account") String account,@Param(value = "password") String password);

        @RequestLine("GET /api/see/existFile/{type}/{fileName}?r={sessionId}")
        ResponseBean fileExist(@Param(value = "type") String type,@Param(value = "fileName") String fileName,@Param(value = "sessionId") String sessionId);

        @RequestLine("GET /api/see/existPath/{type}/{path}/{fileName}?r={sessionId}")
        Object fileExist(@Param(value = "type") String type,@Param(value = "path") String path,@Param(value = "fileName") String fileName,@Param(value = "sessionId") String sessionId);
    }

}
