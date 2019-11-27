package com.xiaobailong_student.base;

import android.app.Application;

import com.xiaobailong_student.bluetoothfaultboardcontrol.FaultboardOption;
import com.xiaobailong_student.bluetoothfaultboardcontrol.Relay;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dongyuangui on 2017/5/31.
 */

public class BaseApplication extends Application {
    public static BaseApplication app = null;

    // 故障点说明保存
    public File descStrFile;
    /**
     * 基本操作功能类
     */
    public  FaultboardOption faultboardOption = null;

    /**
     * 短路1-120继电器状态数据
     */
    public ArrayList<Relay> shortList = new ArrayList<Relay>();

    /**
     * 虚接1-120继电器状态数据
     */
    public ArrayList<Relay> falseList = new ArrayList<Relay>();

    /**
     * 断路1-120继电器状态数据
     */
    public ArrayList<Relay> breakfaultList = new ArrayList<Relay>();


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        if(faultboardOption == null){
//            List<Examination> list = BaseApplication.app.daoSession.getExaminationDao().queryBuilder().list();
//            for (int i = 0; i < list.size(); i++) {
//                Examination examination = list.get(i);
//                if (examination != null) {
//                    examination.setExpired(true);
//                    BaseApplication.app.daoSession.getExaminationDao().update(examination);
//                }
//            }
//        }
    }

}
