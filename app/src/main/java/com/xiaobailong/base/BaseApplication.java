package com.xiaobailong.base;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.xiaobailong.bean.DaoMaster;
import com.xiaobailong.bean.DaoSession;
import com.xiaobailong.bean.Examination;
import com.xiaobailong.bluetoothfaultboardcontrol.FaultboardOption;
import com.xiaobailong.bluetoothfaultboardcontrol.Relay;
import com.xiaobailong.model.FaultDes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongyuangui on 2017/5/31.
 */

public class BaseApplication extends Application {
    public static BaseApplication app = null;

    public DaoMaster.DevOpenHelper devOpenHelper;
    public DaoSession daoSession;
    public DaoMaster daoMaster;
    // 故障点说明保存
    public FaultDes descStrFile;
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
        devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "cms", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        if(faultboardOption == null){
            List<Examination> list = BaseApplication.app.daoSession.getExaminationDao().queryBuilder().list();
            for (int i = 0; i < list.size(); i++) {
                Examination examination = list.get(i);
                if (examination != null) {
                    examination.setExpired(true);
                    BaseApplication.app.daoSession.getExaminationDao().update(examination);
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
