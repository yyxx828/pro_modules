package com.xajiusuo.utils;

import com.xajiusuo.MainApplication;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.RsaKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Random;

/**
 * Created by 杨勇 on 19-6-26.
 */
@Slf4j
@Component
public class CfIRun implements CfI {
    static {
        CfI c = new CfI() {};

        Configs.setWillGo(new Thread(() -> {
            log.info("配置参数初始");

            synchronized (c) {
                for (Field field : CfI.class.getDeclaredFields()) {
                    try {
                        String s = field.getName();
                        if (s.startsWith("C_")) {
                            Object[] os = (Object[]) field.get(c);
                            String v = (String) os[0];
                            if (StringUtils.isBlank(v)) {
                                os[0] = s.substring(2);
                            }
                            if (!s.substring(2).equals(v) && StringUtils.isNotBlank(v)) {
                                throw new RuntimeException(MessageFormat.format("属性名称[{0}]和值[{1}]名称不相同", s, v));
                            }
                            Configs.findAdd((String) os[0], (Configs.DataType) os[1], (String) os[2], os[3]);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }

                RsaKeyUtil.seri(MainApplication.proName);
            }
        }));

        Result.setWillGo(new Thread(() -> {
            log.info("请求描述初始");

            synchronized (c) {
                for (Field field : CfI.class.getDeclaredFields()) {
                    try {
                        String s = field.getName();
                        if (s.startsWith("R_")) {
                            String[] ss = (String[]) field.get(c);
                            if (StringUtils.isBlank(ss[0])) {
                                ss[0] = s.substring(2);
                            }
                            if (!s.substring(2).equals(ss[0])) {
                                throw new RuntimeException(MessageFormat.format("属性名称[{0}]和值[{1}]名称不相同", s, ss[0]));
                            }
                            Result.findAdd(ss[0], ss[1]);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }));

        Thread t = new Thread(() -> {
            do{
                synchronized (c){
                    for (Field field : CfI.class.getDeclaredFields()) {
                        String s = field.getName();
                        try {
                            Object[] os = (Object[]) field.get(c);
                            if(StringUtils.isBlank((String)os[0])){
                                os[0] = s.substring(2);
                            }
                        } catch (Exception e) {
                        }
                    }
                    try {c.wait(3000);} catch (Exception e) {}
                }
            }while(true);
        });
        t.setDaemon(true);
        t.start();
    }
}
