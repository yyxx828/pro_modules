package com.xajiusuo;

import com.xajiusuo.configuration.EnableCasClient;
import com.xajiusuo.jpa.config.BaseRepositoryFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by zlm on 2018/1/17.
 * @author zlm
 */
@Slf4j
@EnableCasClient
@EnableScheduling
@SpringBootApplication
@ServletComponentScan
@EnableJpaRepositories(basePackages = {"com.xajiusuo.busi.*"}, repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)//指定自己的工厂类
public class MainApplication {

    public static String proName = null;

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        return new MethodValidationPostProcessor();
    }

    public static void main(String[] args) throws UnknownHostException{

        SpringApplication app = new SpringApplication(MainApplication.class);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        proName = env.getProperty("spring.datasource.url");
        log.info("\n----------------------------------------------------------\n\t"
                        + "Application '{}' is running! \n\tAccess URLs:\n\t"
                        + "Local: \t\t{}://localhost:{}\n\t"
                        + "External: \t{}://{}:{}\n" + "----------------------------------------------------------",
                env.getProperty("spring.application.name"), protocol, env.getProperty("server.port"), protocol,
                InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port"));
        System.out.println( "用户流程模块已经正常启动!>-_-<" );
    }
}
