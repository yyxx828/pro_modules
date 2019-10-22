package com.xajiusuo.busi.template.service.impl;

import com.xajiusuo.busi.template.dao.BusFieldDao;
import com.xajiusuo.busi.template.dao.IntoTempDao;
import com.xajiusuo.busi.template.entity.BusField;
import com.xajiusuo.busi.template.entity.Business;
import com.xajiusuo.busi.template.entity.IntoRecord;
import com.xajiusuo.busi.template.entity.IntoTemp;
import com.xajiusuo.busi.template.service.IntoRecordService;
import com.xajiusuo.busi.template.service.IntoTempService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseEntity;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.ExcelUtils;
import com.xajiusuo.utils.MD5FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;


@Service
public class IntoTempServiceImpl extends BaseServiceImpl<IntoTemp, Integer> implements IntoTempService {

    @Autowired
    private IntoTempDao entityRepository;

    @Autowired
    private BusFieldDao busFieldRepository;

    @Autowired
    private IntoRecordService intoRecordRepository;

    @Override
    public BaseDao<IntoTemp, Integer> getBaseDao() {
        return entityRepository;
    }

    @Override
    public Page<IntoTemp> findPageByEntity(Pageable pageable, IntoTemp entity) {
        String sql = BeanUtils.beanToSql(entity);
        sql += " order by " + pageable.getSort().toString().replaceAll(":", "");
        return entityRepository.executeQuerySqlByPage(pageable, sql);
    }

    @Override
    public void deleteTemplate(Integer id) {
        List<IntoRecord> list = intoRecordRepository.findBy("intoTemp", id);
        for (IntoRecord r : list) {
            r.setIntoTemp(null);
            intoRecordRepository.save(r);
        }
        delete(id);
    }

    @Override
    public void importFile(IntoTemp entity, File file, String encoding, Integer startLine, IntoRecord ir, UserInfoVo user) {
        //模板对应数据容器
        Map<String, List<Object>> data = new HashMap<String, List<Object>>();
        //对各列进行初始列
        String[] fields = entity.getFileds().split(",");
        for (String f : fields) {
            if (StringUtils.isNotBlank(f)) {
                data.put(f, new ArrayList<Object>());
            }
        }

        //所有列定义
        Map<String, BusField> map = new HashMap<String, BusField>();
        for (BusField bf : entity.getUseList()) {
            map.put(bf.getfCol(), bf);
        }

        //去重处理
        //读取最新5万条记录进行滤重
        Set<String> multSet = null;
        Business bs = entity.getBusiness();
        if (StringUtils.isNotBlank(bs.getTname()) && StringUtils.isNotBlank(bs.getMultiCol())) {//读取需要去重列
            String cols = entity.getBusiness().getMultiCol();
            multSet = new HashSet<String>();
            getBaseDao().queryRangeAll();
            String sql = "select " + cols.replace(",", "||' + '||") + " from " + bs.getTname() + " order by id desc";
            List<Object[]> list = querySqlArr(sql,null,50000);
            for(Object[] os : list){
                multSet.add((String)os[0]);
            }
        }

        InputStream in = MD5FileUtil.getIn(file);

        Workbook book = getHExcel(in);
        if (book == null) {
            MD5FileUtil.close(in);
            in = MD5FileUtil.getIn(file);
            book = getXExcel(in);
        }

        if (book != null) {//Excel
            importExcelFile(map, data, entity, book, startLine, ir, user, multSet);
        } else {//txt导入
            MD5FileUtil.close(in);
            in = MD5FileUtil.getIn(file);
            importTxtFile(map, data, entity, in, startLine, ir, user, multSet, encoding);
        }

        if (multSet != null) {
            multSet.clear();
        }

        MD5FileUtil.close(in);
    }

    @Override
    public IntoTemp getIntoTemp(Integer id) {
        IntoTemp entity = getOne(id);

        if (entity.getBusiness() != null) {
            entity.getConverts();

            List<BusField> busList = busFieldRepository.findBy("business", entity.getBusiness());
            Map<String, BusField> busMap = new HashMap<String, BusField>();
            for (BusField f : busList) {
                busMap.put(f.getfCol(), f);
            }

            entity.setNoUseList(busList);
            if (StringUtils.isNotBlank(entity.getFileds())) {
                String[] fs = entity.getFileds().split(",");
                entity.setUseList(new ArrayList<>(fs.length));

                BusField temp = new BusField();
                temp.setfCol("");
                temp.setFproper("<列忽略>");
                for (String s : fs) {
                    BusField bus = busMap.get(s);
                    if (bus == null) {
                        bus = temp;
                    }
                    entity.getUseList().add(bus);
                    entity.list(bus);
                    busList.remove(bus);
                }
            } else {
                entity.setUseList(new ArrayList<>(0));
            }
        }
        return entity;
    }

    //Excel文件获取,兼容新旧版本
    private Workbook getHExcel(InputStream in) {
        Workbook book = null;
        try {
            book = new HSSFWorkbook(in);
        } catch (Exception e) {
            book = null;
        }
        return book;
    }

    //Excel文件获取,兼容新旧版本
    private Workbook getXExcel(InputStream in) {
        Workbook book = null;
        try {
            book = new XSSFWorkbook(in);
        } catch (Exception e1) {
            book = null;
        }
        return book;
    }


    //EXCEL导入
    private void importExcelFile(Map<String, BusField> map, Map<String, List<Object>> data, IntoTemp entity, Workbook book, Integer startLine, IntoRecord ir, UserInfoVo user, Set<String> multSet) {

        if (book.getNumberOfSheets() < 1) {
            ir.setStatus(-1);
            return;
        }

        Sheet sheet = book.getSheetAt(0);
        String[] fields = entity.getFileds().split(",");
        ir.setAllNum(sheet.getLastRowNum());
        ir.setStartNum(startLine);
        intoRecordRepository.save(ir);

        int rowLine = -1;

        for (int i = startLine; i <= sheet.getLastRowNum(); i++) {

            rowLine++;
            addRow(data, rowLine);

            Row row = sheet.getRow(i);
            if (row == null) continue;
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (j >= fields.length) {
                    continue;
                }
                String key = fields[j];
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                BusField bf = map.get(key);
                if (bf == null) {
                    continue;
                }
                Object val = null;
                if (bf.getfType() == null) {
                    val = ExcelUtils.getString(cell);
                } else if (bf.getfType().equals("TIMESTAMP")) {//时间
                    val = ExcelUtils.getDate(cell);
                } else if (bf.getfType().equals("VARCHAR2")) {//字符串
                    val = ExcelUtils.getString(cell);
                    if (val != null) val = ((String) val).trim();
                } else if (bf.getfType().equals("NUMBER")) {//数值
                    val = ExcelUtils.getNumber(cell);
                }

                if (val != null && bf.getCvMap() != null && bf.getCvMap().size() > 0) {
                    String v = bf.getCvMap().get(val);
                    if (v != null) {
                        val = v;
                    } else if (!bf.isNoToVal()) {
                        val = null;
                    }
                }

                data.get(key).set(rowLine, val);
            }
            if (rowLine >= 999) {
                int rows = dataOut(entity, rowLine, data, ir, user, multSet);
                intoRecordRepository.save(ir);
                if (rows < rowLine) {//错误跳出
                    ir.setEndNum(i + rowLine + 1 - rows);
                    ir.setStatus(2);
                    return;
                }
                rowLine = -1;
            }
        }
        int rows = dataOut(entity, rowLine, data, ir, user, multSet);
        if (rowLine != -1) {
            intoRecordRepository.save(ir);
            dataOut(entity, -1, data, ir, user, multSet);
        }
        ir.setEndNum(sheet.getLastRowNum());
        ir.setStatus(1);
    }

    //TXT导入
    private void importTxtFile(Map<String, BusField> map, Map<String, List<Object>> data, IntoTemp entity, InputStream is, Integer startLine, IntoRecord ir, UserInfoVo user, Set<String> multSet, String encoding) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(is, encoding));
        } catch (Exception e) {
            in = new BufferedReader(new InputStreamReader(is));
        }
        String[] fields = entity.getFileds().split(",");
        String split = null;
        String line = null;
        int rowNum = -1;
        int allLine = 0;
        int rowLine = -1;
        boolean err = false;

        try {
            while ((line = in.readLine()) != null) {
                if (split == null) {
                    rowNum = line.split("\t").length;
                    if (rowNum > 5) {
                        split = "\t";
                    } else if ((rowNum = line.split(",").length) > 5) {
                        split = ",";
                    } else {
                        ir.setStatus(-1);
                        return;
                    }
                }
                if (allLine++ < startLine) {
                    continue;
                }
                if (err) {
                    continue;
                }

                rowLine++;
                addRow(data, rowLine);

                String[] ss = line.split(split);

                for (int i = 0; i < ss.length; i++) {
                    if (i >= fields.length) {
                        continue;
                    }
                    String key = fields[i];
                    if (StringUtils.isBlank(key)) {
                        continue;
                    }
                    BusField bf = map.get(key);
                    if (bf == null) {
                        continue;
                    }
                    Object val = null;
                    if (bf.getfType().equals("TIMESTAMP")) {//时间
                        val = ExcelUtils.ds2s(ss[i]);
                    } else if (bf.getfType().equals("VARCHAR2")) {//字符串
                        val = ss[i];
                        if (val != null) val = ((String) val).trim();
                    } else if (bf.getfType().equals("NUMBER")) {//数值
                        if (ss[i] != null) {
                            try {
                                if (ss[i].contains(".")) {
                                    val = Double.parseDouble(ss[i]);
                                } else {
                                    val = Integer.parseInt(ss[i]);
                                }
                            } catch (Exception e) {
                                val = null;
                            }
                        }
                    }

                    if (bf.getCvMap() != null && bf.getCvMap().size() > 0 && val != null) {
                        String v = bf.getCvMap().get(val);
                        if (v != null) {
                            val = v;
                        } else if (!bf.isNoToVal()) {
                            val = null;
                        }
                    }

                    data.get(key).set(rowLine, val);
                }

                if (rowLine >= 999) {
                    int row = dataOut(entity, rowLine, data, ir, user, multSet);
                    intoRecordRepository.save(ir);
                    if (row <= rowLine) {//错误跳出
                        ir.setEndNum(allLine + rowLine + 1 - row);
                        ir.setStatus(2);
                        rowLine = -1;
                        err = true;
                        continue;
                    }
                    rowLine = -1;
                }
            }
            ir.setAllNum(allLine - 1);
        } catch (Exception e) {
        }

        if (!err) {
            ir.setStatus(1);
        }

        int row = dataOut(entity, rowLine, data, ir, user, multSet);
        if (rowLine != -1) {
            intoRecordRepository.save(ir);
            dataOut(entity, -1, data, ir, user, multSet);
        }
    }

    //数据输出
    private int dataOut(IntoTemp entity, int rowLine, Map<String, List<Object>> data, IntoRecord ir, UserInfoVo user, Set<String> multSet) {
        int num = 0;
        if (entity.getBusiness().getUseType() == null || entity.getBusiness().getUseType() || StringUtils.isNotBlank(entity.getBusiness().getTname())) {
            num = outToDatabase(entity, rowLine, data, ir, user, multSet);
        } else {
            num = outToSerName(entity, rowLine, data, ir);
            ir.setSuccessNum(ir.getSuccessNum() + num + 1);
        }
        clearDate(data);
        return num;
    }

    //数据入库
    private int outToDatabase(IntoTemp entity, int rowLine, Map<String, List<Object>> data, IntoRecord ir, UserInfoVo user, Set<String> multSet) {
        if (rowLine == -1) {
            return 0;
        }

        String sf2 = "insert into {0}({1}) values({2})";//todo 插入语句

        //判断ID 是字符串还是整型
        int type = -1;//自定义ID 1 int , 0 string
        Class clas = BeanUtils.getEntity(entity.getBusiness().getTcode());

        Class parent = clas;
        if (parent == null) {
            return 0;
        }
        while ((parent = parent.getSuperclass()) != null) {
            if (parent.equals(BaseIntEntity.class)) {//1 int
                type = 1;
                break;
            } else if (parent.equals(BaseEntity.class)) {//0 string
                type = 0;
                break;
            }
        }

        StringBuilder pros = new StringBuilder();//字段拼接
        StringBuilder vals = new StringBuilder();//字段对应?拼接

        List<String> list = new ArrayList<String>(data.keySet());

        List<Object> val = new ArrayList<Object>(data.size());//字段对应值集合

        StringBuilder sb = new StringBuilder();//进行去重列字段整合
        String mp = StringUtils.isNotBlank(entity.getBusiness().getMultiCol()) ? "{" + entity.getBusiness().getMultiCol().replace(",", "}+{") + "}" : null;

        for (int i = 0; i <= rowLine; i++) {
            pros.delete(0, pros.length());
            vals.delete(0, vals.length());
            sb.delete(0, sb.length());
            val.clear();
            String mp1 = mp;

            for (String s : list) {
                if (data.get(s).get(i) != null) {
                    pros.append(s + ",");
                    vals.append("?" + ",");
                    val.add(data.get(s).get(i));
                }
                if (mp1 != null) {//去重拼接
                    if (data.get(s).get(i) != null) {
                        mp1.replace("{" + s + "}", data.get(s).get(i).toString());
                    } else {
                        mp1.replace("{" + s + "}", "");
                    }
                }
            }
            if (type != -1) {//继承 TableEntity
                pros.append("id");
                vals.append("?");
                val.add(getId(type));
            }
            try {
                if (multSet == null || !multSet.contains(mp1)) {//没有去重设置,或者没有重复执行
                    getBaseDao().executeUpdateSql(MessageFormat.format(sf2, entity.getBusiness().getTname(), pros.toString(), vals.toString()), val.toArray());
                    ir.setSuccessNum(ir.getSuccessNum() + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //错误记录
                if (entity.getBusiness().getErrorSkip() == null || entity.getBusiness().getErrorSkip()) {//
                    ir.setErrorNum(ir.getErrorNum() + 1);
                    continue;
                }
            }
        }
        return rowLine + 1;
    }

    private static ThreadLocal<Long> bs = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    };

    //数据接口
    private int outToSerName(IntoTemp entity, int rowLine, Map<String, List<Object>> data, IntoRecord ir) {
//        return expressRepository.imp(data, bs.get(), entity.getBusiness().getTcode(), ir, rowLine);
        return 0;
    }

    //对数据新增行
    private void addRow(Map<String, List<Object>> data, int row) {
        for (List<Object> list : data.values()) {
            if (list.size() < row + 1) {
                list.add(null);
            }
        }
    }


    private void clearDate(Map<String, List<Object>> data) {
        for (List<Object> list : data.values()) {
            list.clear();
        }
    }

    private Object getId(int type) {
        if (type == 1) {
            Object o = queryOneSql("select " + SqlUtils.getIntSeqName() + ".nextval from dual");
            if(o instanceof Number){
                return ((Number) o).intValue();
            }
        } else {
            return UUID.randomUUID();
        }
        throw new RuntimeException("类型错误");
    }

}
