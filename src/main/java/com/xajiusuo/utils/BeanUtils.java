package com.xajiusuo.utils;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * created by yuhonglei at 2018/02/02
 * 不想将该对象交给spring 管理的话去掉@Configuration注解即可
 * @author yhl
 * @Date 2018/03/05
 */
@Configuration
public class BeanUtils implements ApplicationContextAware{

    private static Map<String,Class> tableMap;
    private static ApplicationContext applicationContext;

    /**
     * 将一个对象bean转换为键值对Map
     *
     * @param <T>  对象类型
     * @param bean 对象
     * @param skips
     * @return Map
     */
    public static <T> Map<String, Object> beanToMap(T bean, String... skips) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                String value = Objects.toString(beanMap.get(key), "");
                if (CommonUtils.isNotEmpty(value) && !"null".equals(value)) {
                    map.put(key + "", beanMap.get(key));
                }
            }
        }
        for(String s:skips){
            map.remove(s);
        }
        return map;
    }
    /**
     * 将一个键值对Map转换为sql查询
     *
     * @param map 对象
     * @return sql
     */
    public static String mapToSql(Map<String, Object> map) {
        StringBuilder sql = new StringBuilder();
        if (null != map && map.size() > 0) {
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() instanceof String) {
                    sql.append(" and ").append(entry.getKey()).append(" like '").append(SqlUtils.sqlLike(Objects.toString(entry.getValue(), ""))).append("' ");
                } else if (entry.getValue() instanceof Integer) {
                    sql.append(" and ").append(entry.getKey()).append(" = ").append(entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    if(!"delflag".equals(entry.getKey().toString().toLowerCase())) continue;
                    sql.append(" and ").append(entry.getKey()).append(" = ").append((Boolean) entry.getValue() ? 1 : 0);
                }
            }
        }
        return sql.toString();
    }

    /**
     * 将一个对象bean转换为sql查询
     * @param bean 对象
     * @param skips
     * @return sql
     * @author yhl
     * @Date 2018/03/05
     */
    public static <T>String beanToSql(T bean, String... skips){
        String table = SqlUtils.tableName(bean.getClass());
        String sql = "select * from " + table + " e where 1 = 1 ";
        return sql + mapToSql(beanToMap(bean,skips));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BeanUtils.applicationContext == null){
            BeanUtils.applicationContext = applicationContext;
        }
        //System.out.println("=======ApplicationContext配置成功，在普通类中可以通过调用" +
               // "BeanUtils.getApplicationContext()获取，applicationContext=" + BeanUtils.applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获得bean
     * @param name bean name
     * @return bean object
     * @author yhl
     * @Date 2018/03/05
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }
    /**
     * 通过class获得bean
     * @param clazz bean class
     * @return bean object
     * @author yhl
     * @Date 2018/03/05
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
    /**
     * 通过name获得bean
     * @param name bean name
     * @param clazz bean class
     * @return bean object
     * @author yhl
     * @Date 2018/03/05
     */
    public static <T> T getBean(String name, Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("300MB");
        factory.setMaxRequestSize("300MB");
        return factory.createMultipartConfig();
    }




    /**
     * 获取所有bean
     * @return bean object
     * @author 杨勇
     * @Date 2019/01/04
     */
    public static List<Class> getAllEntity() {
        getEntity(null);
        return new ArrayList<>(tableMap.values());
    }

    /**
     * 获取所有bean
     * @return bean object
     * @author 杨勇
     * @Date 2019/01/04
     */
    public static Class getEntity(String name) {
        if(tableMap == null || tableMap.size() == 0){
            tableMap = new HashMap<String,Class>();
            List<Class> list = null;
            try{
                List<String> list1 = getClassName("com.xajiusuo.busi");
                list = beans(list1,"com.xajiusuo.busi");
            }catch (Exception e){
                e.printStackTrace();
            }
            if(list != null){
                for(Class c:list){
                    tableMap.put(c.getSimpleName().toLowerCase(),c);
                }
            }
        }
        return tableMap.get(name);
    }

    private static List<Class> beans(List<String> list,String packages){
        List<Class> teList = new ArrayList<Class>();
        for(String cs:list){
            try {
                if(cs.startsWith(packages)){
                    Class t = Class.forName(cs);
                    if(t.getAnnotation(Entity.class) != null){
                        teList.add(t);
                    }
                }
            } catch (Throwable e) {
            }
        }
        return teList;
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    public static List<String> getClassName(String packageName) throws IOException {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName  包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    public static List<String> getClassName(String packageName, boolean childPackage) throws IOException {
        List<String> fileNames = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> urls = loader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null)
                continue;
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames.addAll(getClassNameByFile(url.getPath(), childPackage));
            } else if (type.equals("jar")) {
                fileNames.addAll(getClassNameByJar(url.getPath(), childPackage));
            }
        }
        fileNames.addAll(getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage));
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath     文件路径
     *                     类名集合
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    private static List<String> getClassNameByFile(String filePath, boolean childPackage) throws UnsupportedEncodingException {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        if (childFiles == null)
            return myClassName;
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("/classes/") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("/", ".");
                    myClassName.add(childFilePath);
                }
            }
        }
        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath      jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) throws UnsupportedEncodingException {
        List<String> myClassName = new ArrayList<String>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class") || entryName.endsWith(".java")) {
                    try {
                        if (childPackage) {
                            if (entryName.startsWith(packagePath)) {
                                entryName = entryName.replace("/", ".").replace("\\", ".").substring(0, entryName.lastIndexOf("."));
                                myClassName.add(entryName.replace("BOOT-INF.classes.",""));
                            }
                        } else {
                            int index = entryName.lastIndexOf("/");
                            String myPackagePath;
                            if (index != -1) {
                                myPackagePath = entryName.substring(0, index);
                            } else {
                                myPackagePath = entryName;
                            }
                            if (myPackagePath.equals(packagePath)) {
                                entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                                myClassName.add(entryName.replace("BOOT-INF.classes.",""));
                            }
                        }
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
            }
//            Callable<String> c = new FutureTask<String>();

            CompletableFuture c = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls         URL集合
     * @param packagePath  包路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) throws UnsupportedEncodingException {
        List<String> myClassName = new ArrayList<String>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

}
