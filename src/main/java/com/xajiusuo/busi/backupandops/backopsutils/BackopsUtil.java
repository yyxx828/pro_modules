package com.xajiusuo.busi.backupandops.backopsutils;

import com.xajiusuo.busi.backupandops.entity.BackupVo;
import com.xajiusuo.busi.backupandops.entity.DelLogVo;
import com.xajiusuo.busi.backupandops.entity.DevVo;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangdou on 2019/6/17
 */
public class BackopsUtil {

    private static int count = 0;

    public static List<String> readerTxt(String fileName) {
        List<String> list = new ArrayList<>();
        try {
            list = Files.lines(Paths.get(fileName), Charset.defaultCharset()).flatMap(line -> Arrays.stream(line.split("\n"))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List getResultList(List ulist, String s, String ip, String flag) {
        String[] values = s.split(",");
        if ("del".equals(flag)) {
            ulist.add(new DelLogVo(values[0], values[1], values[2].substring(values[2].indexOf(":") + 1, values[2].length())));
        } else if ("back".equals(flag)) {
            ulist.add(new BackupVo(ip, values[0].split(":")[1], values[1].split(":")[1], values[2].substring(values[2].indexOf(":") + 1, values[2].length())));
        } else {
            ulist.add(new DevVo(values[0], ("1".equals(values[1])) ? "网络联通" : "网络断开"));
        }
        return ulist;
    }

    public static void writTxt(List<String> list, String filename) {
        File file = new File(filename);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(writer);
            List<String> newList = list.stream().filter(t -> !StringUtils.isEmpty(t)).collect(Collectors.toList());
            newList.forEach(n -> {
                try {
                    count++;
                    out.write(n + (count == newList.size() ? "" : "\n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        count = 0;
    }

    /**
     * 根据ip获取DevVO
     *
     * @param devicepath
     * @param ips
     * @return
     */
    public static Map<String, Object> getDevStatus(String devicepath, String ips) {
        List<String> list = BackopsUtil.readerTxt(devicepath + "ping.txt");
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        list.forEach(t -> map.put(t.split(",")[0], ("1".equals(t.split(",")[1])) ? "网络联通" : "网络断开"));
        if (StringUtils.isEmpty(ips)) {
            return map;
        } else {
            for (String s : ips.split(",")) {
                resultMap.put(s, StringUtils.isEmpty(map.get(s)) ? "网络断开" : "网络联通");
            }
        }
        return resultMap;
    }

    /**
     * 获取当前操作系统
     */
    public static boolean operatingSys() {
        boolean flag = true;
        Properties properties = System.getProperties();
        if (properties.get("os.name").toString().contains("Win")) {
            flag = false;
        }
        return flag;
    }
}
