package com.xiaobailong_student.tools;

import android.os.Environment;

public class ConstValue {
    // 没有状态
    public static final int type_none = -1;
    // $ 文件中开头符号 断路
    public static final int type_break = 0;
    // # 文件中开头符号 虚接
    public static final int type_false = 1;
    // @ 文件中开头符号 短路
    public static final int type_shortFault = 2;
    // $ 文件中开头符号 断路
    public static final String type_break_str = "0";
    // # 文件中开头符号 虚接
    public static final String type_false_str = "1";
    // @ 文件中开头符号 短路
    public static final String type_shortFault_str = "2";


    public static String BREAK_DELIMTER = "$";
    public static String FALSE_DELIMTER = "#";
    public static String SHORT_DELIMTER = "@";
    public static String SERIAL_DELIMTER = "&&&&";
    /**
     * 取sdcard的路径
     *
     * @return 如果Sdcard没有，或者不可写，返回null
     */

    public static String titleName = "标题名称.txt";
    public static String dir = "simulation";
    public static String DIR_STUDENT = "dir_student";
    public static String dir_autoblue = "autoblue";
    public static String dir_scores = "scores";

    public static String getSdcardPath() {
        if (haveSdcard()) {
            return Environment.getExternalStorageDirectory()
                    .getPath();
        } else {
            return null;
        }
    }

    /**
     * 是否有SDCARD
     *
     * @return 有SDCARD, 返回true, 否则返回false
     */
    public static boolean haveSdcard() {
        return (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED));
    }

    public static String get_DIR() {
//        return getSdcardPath() ;
        return getSdcardPath() + "/" + dir + "/";
    }

    public static String get_title_File() {
        return getSdcardPath() + "/" + dir + "/" + titleName;
    }

    public static String getDirStudent() {
        return getSdcardPath() + "/"  + DIR_STUDENT;
    }

    public static String getFailureDir(){
        return getSdcardPath()+"/" + dir_autoblue;
    }
    public static String getScoresDir(){
        return getSdcardPath()+"/" + dir_scores;
    }

}
