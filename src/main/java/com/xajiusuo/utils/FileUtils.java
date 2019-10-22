package com.xajiusuo.utils;

import javax.validation.constraints.NotNull;

public class FileUtils {
    private static final String TXT = "^.+\\.(?i)(txt)$";
    private static final String CSV = "^.+\\.(?i)(csv)$";
    private static final String TSV = "^.+\\.(?i)(tsv)$";

    public static boolean isTxt(@NotNull String fileName){
        return fileName.matches(TXT);
    }

    public static boolean isNotTxt(@NotNull String fileName){
        return !isTxt(fileName);
    }

    public static boolean isCsv(@NotNull String fileName){
        return fileName.matches(CSV);
    }

    public static boolean isNotCsv(@NotNull String fileName){
        return !isCsv(fileName);
    }

    public static boolean isTsv(@NotNull String fileName){return fileName.matches(TSV); }

    public static boolean isNotTsv(@NotNull String fileName){
        return !isTsv(fileName);
    }

    public static boolean validFile(String fileName){return fileName != null && (isTxt(fileName) || isCsv(fileName)|| isTsv(fileName));}

    public static String validSeparator(String s){
        if(s.lastIndexOf(",")>0){
            return ",";
        }else if(s.lastIndexOf(";")>0){
            return ";";
        }else if(s.lastIndexOf("\t")>0){
            return "\t";
        }
        return null;
    }
}
