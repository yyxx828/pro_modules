package com.xajiusuo.utils;

import com.xajiusuo.jpa.util.IMapp;
import com.xajiusuo.jpa.util.Mapp;
import com.xajiusuo.jpa.util.P;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 杨勇
 * @Date 2019/06/04 */
public class ExcelUtils {

    private static final String REG03 = "^.+\\.(?i)(xls)$";
    private static final String REG07 = "^.+\\.(?i)(xlsx)$";

    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String REGTIME = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";

    private static final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

    @Deprecated
    public static boolean isExcel03(@NotNull String fileName){
        return fileName.matches(REG03);
    }

    @Deprecated
    public static boolean isNotExcel03(@NotNull String fileName){
        return !isExcel03(fileName);
    }

    @Deprecated
    public static boolean isExcel07(@NotNull String fileName){
        return fileName.matches(REG07);
    }

    @Deprecated
    public static boolean isNotExcel07(@NotNull String fileName){
        return !isExcel07(fileName);
    }

    @Deprecated
    public static boolean validExcel(String fileName){
        return fileName != null && (isExcel03(fileName) || isExcel07(fileName));
    }
    /***
     * 获取excel文件
     * @return
     */
    public static Workbook getBook(){
        Workbook book = null;
        try {
            book = new XSSFWorkbook();
        } catch (Exception e) {
            try {
                book = new HSSFWorkbook();
            } catch (Exception e1) {
                try {
                    SXSSFWorkbook book1 = new SXSSFWorkbook(1000);
                    book1.setCompressTempFiles(true);
                    book = book1;
                } catch (Exception e2) {
                    throw new RuntimeException();
                }
            }
        }
        return book;
    }

    /***
     * 将指定的List数据,转成Excel,并且返回book对象
     * @param list List&lt;T&gt;,T 可以为Object[],Map&lt;?,?&gt;,定义业务对象
     * @param headName Excel中文标题名称
     * @param fields T 为业务对象[属性] 或 Map 的key, Object[] 时不对fields进行处理
     * @param sheetName 导出sheet页的名称,遵循sheet命名规则即可
     * @param maxRow 最大行数,0为不限制,导出list所有条数
     * @param num 是否第一列为序号
     * @return Workbook对象
     */
    public static Workbook listToExcel(List list, List<String> headName, List<String> fields, String sheetName, int maxRow, boolean num) {
        Workbook book = getBook();
        Sheet sheet = book.createSheet(sheetName);
        Row row = sheet.createRow(0);

        for (int i = 0; i < headName.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headName.get(i));
        }

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            int rows = 0;
            if(num){
                row.createCell(rows++).setCellValue(i + 1);
            }
            if(list.get(i) instanceof Object[]){
                for(Object v:(Object[]) list.get(i)){
                    setCellValue(row.createCell(rows++),v);
                }
            }else if(list.get(i) instanceof Map){
                for(String f:fields){
                    setCellValue(row.createCell(rows++),((Map) list.get(i)).get(f));
                }
            }else{
                IMapp m = Mapp.createMapp(list.get(i));
                for(String f:fields){
                    setCellValue(row.createCell(rows++),m.getObjectbyField(f));
                }
            }
            if(maxRow != 0 && i >= maxRow) break;
        }
        return book;
    }

    private static void setCellValue(Cell cell,Object v){
        if(v == null) v = "";
        if(v instanceof Date)
            cell.setCellValue(P.U.dateFormat((Date)v,dateFormat));
        else
            cell.setCellValue(v.toString());
    }


    /***
     * 单元格读取数值格式
     *
     * @param cell
     * @return
     * @author 杨勇
     * @date 2019-01-07
     */
    public static Number getNumber(Cell cell) {
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            try {
                Integer.parseInt(cell.getStringCellValue());
            } catch (Exception e1) {
                try {
                    Double.parseDouble(cell.getStringCellValue());
                } catch (Exception e2) {
                }
            }
        }
        return null;
    }

    /***
     * 单元格读取字符串
     *
     * @param cell
     * @return
     * @author 杨勇
     * @date 2019-01-07
     */
    public static String getString(Cell cell) {
        try {
            if(cell != null){
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            }
        } catch (Exception e) {
            try {
                return cell.getNumericCellValue() + "";
            } catch (Exception e1) {
                try {
                    return sdf.format(cell.getDateCellValue());
                } catch (Exception e2) {
                }
            }
        }
        return null;
    }


    /***
     * 单元格读取时间,格式返回yyyy-MM-dd HH:mm:ss
     *
     * @param cell
     * @return
     * @author 杨勇
     * @date 2019-01-07
     */
    public static Date getDate(Cell cell) {
        try {
            //按照字符串读取
            return ds2s(cell.getStringCellValue());
        } catch (Exception e) {
            try {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
            } catch (Exception e1) {
            }
        }
        return null;
    }

    /***
     * 支持格式
     * 日期+空格+时间
     * 2018/1/01 13:1:01 (20180101) (130101)
     * 2018-1-01 13:1:01
     * 2018-01-0113:01:01
     * 2018/01/0113:01:01
     * 20180101130101
     * 20180101
     * 2018/1/01
     * 2018-1-01
     * 对日期格式的字符串转换成时间格式字符串yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date ds2s(String s) {
        String dt = null;
        if (s.contains(" ")) {//时间含空格
            String[] ss = s.split(" ");
            String d = ss[0];
            String t = ss[1];
            if (d.contains("/") && !d.contains("-")) {//日期
                String[] ds = d.split("/");
                d = d.replace("/", "-");
            } else if (d.contains("-")) {
            } else if (d.length() == 8) {
                d = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6);
            } else {
                return null;
            }
            if (t.contains(":")) {
            } else if (t.length() == 6) {
                t = t.substring(0, 2) + ":" + t.substring(2, 4) + ":" + t.substring(4);
            } else {
                return null;
            }
            dt = d + " " + t;
        } else if (s.length() == 14 && s.startsWith("2")) {
            dt = s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12) + ":" + s.substring(12);
        } else if (s.length() <= 10) {
            if (s.contains("/") && !s.contains("-")) {//日期
                dt = s.replace("/", "-") + " 00:00:00";
            } else if (s.contains("-")) {
                dt = s + " 00:00:00";
            }
        } else if (s.length() == 18) {
            dt = s.substring(0, 10).replace("/", "-") + " " + s.substring(10);
        } else if (s.length() == 16) {
            if (s.contains(":") && !s.contains("/") && !s.contains("-")) {
                dt = s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8) + " " + s.substring(8);
            } else if (!s.contains(":") && s.contains("/") && !s.contains("-")) {
                s = s.replace("/", "-");
                dt = s.substring(0, 10) + " " + s.substring(10, 12) + ":" + s.substring(12, 14) + ":" + s.substring(14);
            } else if (!s.contains(":") && !s.contains("/") && s.contains("-")) {
                dt = s.substring(0, 10) + " " + s.substring(10, 12) + ":" + s.substring(12, 14) + ":" + s.substring(14);
            } else {
                return null;
            }
        } else {
            return null;
        }

        if (dt.matches(REGTIME)) {
            try {
                return sdf.parse(dt);
            } catch (Exception e) {
            }
        }
        return null;
    }


    //日期转换成固定时间格式字符串yyyy-MM-dd HH:mm:ss
    public static String d2s(Date d) {
        if (d == null) {
            return null;
        }
        return (d.getYear() + 1900) + "-" + i2s(d.getMonth() + 1) + "-" + i2s(d.getDate()) + " " + i2s(d.getHours()) + ":" + i2s(d.getMinutes()) + ":" + i2s(d.getSeconds());
    }

    //数字转换为2位
    private static String i2s(int i) {
        if (i > 10) {
            return i + "";
        }
        return "0" + i;
    }
}
