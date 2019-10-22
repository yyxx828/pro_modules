package com.xajiusuo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用短信服务mysql数据库
 */
public class MySqlJdbc {
    public ResultSet conMysql(String sql, String mesageIp, String mySqlUser, String mySqlPassword) {
        //传入要执行的SQL语句
        Connection con;//声明Connection对象
        ResultSet rs = null;
        String driver = "com.mysql.jdbc.Driver";//驱动程序名
        String urls = "jdbc:mysql://" + mesageIp + ":3308/smsserver";
        try {
            Class.forName(driver);//加载驱动程序
            con = DriverManager.getConnection(urls, mySqlUser, mySqlPassword);//1.getConnection()方法，连接MySQL数据库！！
            if (!con.isClosed())
                System.out.println("数据库连接成功!");
            Statement statement = con.createStatement();//2.创建statement类对象，用来执行SQL语句！！
            rs = statement.executeQuery(sql);//3.ResultSet类，用来存放获取的结果集！！
            con.close();
            System.out.println("数据库数据成功获取！！");
        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("数据库驱动异常!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("数据库连接异常!");
            //数据库连接失败异常处理
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return rs;
    }

    public static void main(String[] args) {
        String mesageIp = "localhost";
        String mySqlUser = "root";
        String mySqlPassword = "123456";
        Connection con;//声明Connection对象
        ResultSet rs;
        String driver = "com.mysql.jdbc.Driver";//驱动程序名
        String urls = "jdbc:mysql://" + mesageIp + ":3308/smsserver";
        String sql = "select * from smsserver_in";
        try {
            Class.forName(driver);//加载驱动程序
            con = DriverManager.getConnection(urls, mySqlUser, mySqlPassword);//1.getConnection()方法，连接MySQL数据库！！
            if (!con.isClosed())
                System.out.println("数据库连接成功!");
            Statement statement = con.createStatement();//2.创建statement类对象，用来执行SQL语句！！
            rs = statement.executeQuery(sql);//3.ResultSet类，用来存放获取的结果集！！
            if (null != rs) {
                try {
                    while (rs.next()) {
                        String taskId;
                        String isArgee;
                        String originator;
                        String text;
                        String[] notes;
                        //获取text短信内容这列数据
                        originator = rs.getString("originator");
                        Map<String, String> map = new HashMap<>();
                        if (11 == originator.length()) {//发送号码为手机号
                            text = rs.getString("text");
                            if (text.contains("www.sendsms.com.cn>")) {
                                notes = text.split("cn>");
                                notes = notes[1].split("-");
                                if (2 == notes.length) {
                                    taskId = notes[0];
                                    isArgee = notes[1];
                                    map.put("msgId", taskId);
                                    map.put("note", isArgee);
                                    System.out.println("发送号码：" + originator + "；任务Id：" + taskId + "；审批意见：" + isArgee);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            rs.close();
            con.close();
            System.out.println("数据库数据成功获取！！");
        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("数据库驱动异常!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("数据库连接异常!");
            //数据库连接失败异常处理
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
