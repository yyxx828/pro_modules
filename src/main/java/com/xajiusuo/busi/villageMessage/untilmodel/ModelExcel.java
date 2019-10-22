package com.xajiusuo.busi.villageMessage.untilmodel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GaoYong
 * @Date 19-6-19 下午2:21
 * @Description
 */
public class ModelExcel {
    /**
     * @Title: createExcelTemplate
     * @Description: 生成Excel导入模板
     * @param @param filePath  Excel文件路径
     * @param @param handers   Excel列标题(数组)
     * @param @param downData  下拉框数据(数组)
     * @param @param downRows  下拉列的序号(数组,序号从0开始)
     * @return void
     * @throws
     */
    public  void createExcelTemplate(String filePath, String[] handers,
                                            List<String[]> downData, String[] downRows){

        HSSFWorkbook wb = new HSSFWorkbook();//创建工作薄

        //表头样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        //字体样式
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontName("微软雅黑");
        fontStyle.setFontHeightInPoints((short)12);
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(fontStyle);

        //新建sheet
        HSSFSheet sheet1 = wb.createSheet("Sheet1");
        HSSFSheet sheet2 = wb.createSheet("Sheet2");
        HSSFSheet sheet3 = wb.createSheet("Sheet3");

        //生成sheet1内容
        HSSFRow rowFirst = sheet1.createRow(0);//第一个sheet的第一行为标题
        //写标题
        for(int i=0;i<handers.length;i++){
            HSSFCell cell = rowFirst.createCell(i); //获取第一行的每个单元格
            sheet1.setColumnWidth(i, 4000); //设置每列的列宽
            cell.setCellStyle(style); //加样式
            cell.setCellValue(handers[i]); //往单元格里写数据
        }

        //设置下拉框数据
        String[] arr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        int index = 0;
        HSSFRow row = null;
        for(int r=0;r<downRows.length;r++){
            String[] dlData = downData.get(r);//获取下拉对象
            int rownum = Integer.parseInt(downRows[r]);

            if(dlData.length<5){ //255以内的下拉
                //255以内的下拉,参数分别是：作用的sheet、下拉内容数组、起始行、终止行、起始列、终止列
                sheet1.addValidationData(setDataValidation(sheet1, dlData, 1, 50000, rownum ,rownum)); //超过255个报错
            } else { //255以上的下拉，即下拉列表元素很多的情况

                //1、设置有效性
                //String strFormula = "Sheet2!$A$1:$A$5000" ; //Sheet2第A1到A5000作为下拉列表来源数据
                String strFormula = "Sheet2!$"+arr[index]+"$1:$"+arr[index]+"$5000"; //Sheet2第A1到A5000作为下拉列表来源数据
                sheet2.setColumnWidth(r, 4000); //设置每列的列宽
                //设置数据有效性加载在哪个单元格上,参数分别是：从sheet2获取A1到A5000作为一个下拉的数据、起始行、终止行、起始列、终止列
                sheet1.addValidationData(SetDataValidation(strFormula, 1, 50000, rownum, rownum)); //下拉列表元素很多的情况

                //2、生成sheet2内容
                for(int j=0;j<dlData.length;j++){
                    if(index==0){ //第1个下拉选项，直接创建行、列
                        row = sheet2.createRow(j); //创建数据行
                        sheet2.setColumnWidth(j, 4000); //设置每列的列宽
                        row.createCell(0).setCellValue(dlData[j]); //设置对应单元格的值

                    } else { //非第1个下拉选项

                        int rowCount = sheet2.getLastRowNum();
                        //System.out.println("========== LastRowNum =========" + rowCount);
                        if(j<=rowCount){ //前面创建过的行，直接获取行，创建列
                            //获取行，创建列
                            sheet2.getRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值

                        } else { //未创建过的行，直接创建行、创建列
                            sheet2.setColumnWidth(j, 4000); //设置每列的列宽
                            //创建行、创建列
                            sheet2.createRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值
                        }
                    }
                }
                index++;
            }
        }

        try {

            File f = new File(filePath); //写文件

            //不存在则新增
            if(!new File(f.getAbsoluteFile().toString().replace("\\"+f.getName().toString(),"")).exists()){
                new File(f.getAbsoluteFile().toString().replace("\\"+f.getName().toString(),"")).mkdirs();
            }
            if(!f.exists()){
                f.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(f);
            out.flush();
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     *
     * @Title: SetDataValidation
     * @Description: 下拉列表元素很多的情况 (255以上的下拉)
     * @param @param strFormula
     * @param @param firstRow   起始行
     * @param @param endRow     终止行
     * @param @param firstCol   起始列
     * @param @param endCol     终止列
     * @param @return
     * @return HSSFDataValidation
     * @throws
     */
    private  HSSFDataValidation SetDataValidation(String strFormula,
                                                        int firstRow, int endRow, int firstCol, int endCol) {

        // 设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(regions,constraint);

        dataValidation.createErrorBox("Error", "Error");
        dataValidation.createPromptBox("", null);

        return dataValidation;
    }


    /**
     *
     * @Title: setDataValidation
     * @Description: 下拉列表元素不多的情况(255以内的下拉)
     * @param @param sheet
     * @param @param textList
     * @param @param firstRow
     * @param @param endRow
     * @param @param firstCol
     * @param @param endCol
     * @param @return
     * @return DataValidation
     * @throws
     */
    private  DataValidation setDataValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {

        DataValidationHelper helper = sheet.getDataValidationHelper();
        //加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        //DVConstraint constraint = new DVConstraint();
        constraint.setExplicitListValues(textList);

        //设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList((short) firstRow, (short) endRow, (short) firstCol, (short) endCol);

        //数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        //DataValidation data_validation = new DataValidation(regions, constraint);

        return data_validation;
    }


    public Map<Integer, Map<Integer, Object>> readExcelContent(InputStream in) throws Exception {
        Workbook wb =null;
        Sheet  sheet = null;
        Row row = null;
        Map<Integer, Map<Integer, Object>> content = new HashMap<Integer, Map<Integer, Object>>();
        try {
            wb = new HSSFWorkbook(in);
            if (wb == null) {
                throw new Exception("Workbook对象为空!");
            }
            sheet = wb.getSheetAt(0);
            //获取总行数
            int rowNum = sheet.getLastRowNum();
            row = sheet.getRow(0);
            int colNum = row.getPhysicalNumberOfCells();
            //正文内容从第二行开始，第一行为表头
            for (int i = 1; i <= rowNum; i++) {
                row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                int j = 0;
                Map<Integer, Object> cellValue = new HashMap<Integer, Object>();
                while (j < colNum) {

                    Object obj = getValue(row.getCell(j));

                    cellValue.put(j, obj);
                    j++;
                }
                if (cellValue.size() != 0) {
                    content.put(i, cellValue);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 根据Cell类型设置数据
     *
     * @param cell
     * @return
     */
    private String getValue(Cell cell) {
        if (cell == null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue = "";
        int cellType = cell.getCellType();


        if (1 == cellType) {//字符串
            cellValue = cell.getStringCellValue().trim();
            cellValue = StringUtils.isEmpty(cellValue) ? "" : cellValue;
        } else if (4 == cellType) {
            cellValue = String.valueOf(cell.getBooleanCellValue());
        } else if (0 == cellType) {
            if (DateUtil.isCellDateFormatted(cell)) {
                cellValue = new SimpleDateFormat(cell.getCellStyle().getDataFormatString().toString().replaceAll("\\\\","")).format(cell.getDateCellValue());
            } else {
                cellValue = new DecimalFormat("#").format(cell.getNumericCellValue());
            }
        } else {
            cellValue = "";
        }
        return cellValue.trim();
    }
    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null || cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }


    public Map<String,PictureData> getPic(String type,InputStream in){
        Map<String,PictureData> map = new HashMap<>();
        try {
            Workbook wb = new HSSFWorkbook(in);
            Sheet sheet = wb.getSheetAt(0);
            if("xls".equals(type)){
                map = getPictures03((HSSFSheet) sheet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @Author shirenjing
     * @Description 读取03版Excel中的图片
     * @Date 18:00 2019/7/1
     * @Param [sheet]
     * @return
     **/
    public Map<String,PictureData> getPictures03(HSSFSheet sheet){
        Map<String,PictureData> map = new HashMap<>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for(HSSFShape shape : list){
            if(shape instanceof HSSFPicture){
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor cAnchor = (HSSFClientAnchor) picture.getAnchor();
                PictureData pdata = picture.getPictureData();
                String key = cAnchor.getRow1() +"-"+cAnchor.getCol1();
                map.put(key,pdata);
            }
        }
        return map;
    }









    public static void main(String[] args) {
        ModelExcel modelExcel = new ModelExcel();
        String fileName = "/home/hadoop/桌面/EXCEL下拉模版有效性/员工信息表.xls"; //模板名称
        String[] handers = {"姓名","性别","证件类型","证件号码","服务结束时间","参保地","民族"}; //列标题

        //下拉框数据
        List<String[]> downData = new ArrayList();
        String[] str1 = {"1-男","2-女","0-未知"};
        String[] str2 = {"北京","上海","广州","深圳","武汉","长沙","湘潭"};
        String[] str3 = {"01-汉族","02-蒙古族","03-回族","04-藏族","05-维吾尔族","06-苗族","07-彝族","08-壮族","09-布依族",
                "10-朝鲜族","11-满族","12-侗族","13-瑶族","14-白族","15-土家族","16-哈尼族","17-哈萨克族","18-傣族","19-黎族","20-傈僳族",
                "21-佤族","22-畲族","23-高山族","24-拉祜族","25-水族","26-东乡族","27-纳西族","28-景颇族","29-柯尔克孜族","30-土族",
                "31-达斡尔族","32-仫佬族","33-羌族","34-布朗族","35-撒拉族","36-毛难族","37-仡佬族","38-锡伯族","39-阿昌族","40-普米族",
                "41-塔吉克族","42-怒族","43-乌孜别克族","44-俄罗斯族","45-鄂温克族","46-德昂族","47-保安族","48-裕固族","49-京族","50-塔塔尔族",
                "51-独龙族","52-鄂伦春族","53-赫哲族","54-门巴族","55-珞巴族","56-基诺族","98-外国血统","99-其他"};
        downData.add(str1);
        downData.add(str2);
        downData.add(str3);
        String [] downRows = {"1","5","6"}; //下拉的列序号数组(序号从0开始)

        try {

            modelExcel.createExcelTemplate(fileName, handers, downData, downRows);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
