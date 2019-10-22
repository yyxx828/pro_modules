package com.xajiusuo.utils;

import java.io.File;

/**
 * Created by fanhua on 2019-4-28.
 */
public class DocumentUtil {
    public DocumentUtil() {
    }

    public static void main(String[] args){
        DocumentUtil documentUtil = new DocumentUtil();
        String paths = "C:\\Users\\Administrator\\Desktop\\测试\\2007.docx";
        documentUtil.topdfAndSwf(paths);
    }
    public File topdfAndSwf(String path) {
        File file = new File(path);
        if (null != file) {
            DocConverterUtil d = new DocConverterUtil(path);
            File filePdf = d.conver();
            return filePdf;
        }
        return null;
    }
}
