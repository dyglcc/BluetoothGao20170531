package com.xiaobailong_student.bluetoothfaultboardcontrol;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong_student.activity.ShowResultActivity;
import com.xiaobailong_student.base.BaseApplication;
import com.xiaobailong_student.beans.Examination;
import com.xiaobailong_student.beans.Scores;
import com.xiaobailong_student.beans.Student;
import com.xiaobailong_student.bluetooth.MediaFileListDialog;
import com.xiaobailong_student.bluetooth.MediaFileListDialogMainpage;
import com.xiaobailong_student.model.FaultBean;
import com.xiaobailong_student.net.AbstractNet;
import com.xiaobailong_student.result.ResponseLoadExamData;
import com.xiaobailong_student.result.ResponseSaveScoreData;
import com.xiaobailong_student.titile.WriteTitleActivity;
import com.xiaobailong_student.tools.ConstValue;
import com.xiaobailong_student.tools.SpDataUtils;
import com.xiaobailong_student.widget.ListScrollView;
import com.yanzhenjie.loading.dialog.LoadingDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import me.grantland.widget.AutofitTextView;

import static com.xiaobailong_student.tools.ConstValue.type_break;
import static com.xiaobailong_student.tools.ConstValue.type_false;
import static com.xiaobailong_student.tools.ConstValue.type_none;
import static com.xiaobailong_student.tools.ConstValue.type_shortFault;

public class MainActivity extends BaseActivity implements OnClickListener,
        OnTouchListener, OnScrollListener, ReadStateListenter {

    public static final int ShortTable = 0;
    public static final int FalseTable = 1;
    public static final int BreakTable = 2;
    public static int TableState = ShortTable;

    private ListScrollView listscroll;

//    public ExaminationDao dao;

    /**
     * 1-120继电器操作监听
     */
    private Handler theFailurePointSetGVHandler = null;
    /**
     * 基本功能操作监听
     */
    private Handler faultboardOptionHandler = null;

    private TheFailurePointSetAdapter theFailurePointSetAdapter = null;

    private Button ignitionButton = null;
    private Button startButton = null;
    private Button shutDownButton = null;
    private boolean hasStarted = false;

    private TabHost tabHost = null;
    private TabWidget tabWidget = null;

    private GridView theFailurePointSetGV = null;
    private AutofitTextView tvFileName = null;
    private ViewGroup layout_teacher, layout_student;

    private Button btnSend = null;
    private EditText etTime;

    // 学生面板
    private TextView tv_exam_tip_break, tv_lasttime;
    private TextView tv_exam_tip_false;
    private TextView tv_exam_tip_short;
    private Button button_start_exam, button_submit;
    private Examination examzation;
    private Student student;
    private Scores scores, scoresNew;
    private LinearLayout content;
    private int count;
    private int resultCount;
    private String loginType;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        dao = BaseApplication.app.daoSession.getExaminationDao();
        loginType = SpDataUtils.getLoginType();
        initSDcard();
        initData(0);
        initHandler();
        initCountHandler();
        initView();
        initShowResultHandler();

    }

    private void initShowResultHandler() {
        showResultHandler = new ShowResultHandler(this);
    }

    private static Handler countHandler;
    private int cousumeSeconds = 0;

    private ShowResultHandler showResultHandler;

    private static class ShowResultHandler extends Handler {

        private WeakReference<MainActivity> mainActivityWeakReference = null;

        public ShowResultHandler(MainActivity reference) {
            mainActivityWeakReference = new WeakReference<MainActivity>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

                MainActivity activity = mainActivityWeakReference.get();
                if (activity != null) {
                    activity.afterSaveScores();
                }
            }
        }
    }

    private void initCountHandler() {
        countHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Bundle data = msg.getData();
                    if (data != null) {
                        int second = data.getInt("time");
                        int min = second / 60;
                        int sss = second % 60;
                        tv_lasttime.setText("剩余时间：" + min + "分 " + sss + " 秒");
                        cousumeSeconds = second;
                        if (second == 0) {
                            begin = false;
                            tv_lasttime.setText("剩余时间：" + 0 + "分 " + 0 + " 秒");
                            tv_lasttime.setTextColor(ContextCompat.getColor(MainActivity.this, com.xiaobailong_student.bluetoothfaultboardcontrol.R.color.red));
                            float scroes = getScores();
                            int scores_ = ((int) scroes);
                            scoresNew = new Scores();
                            scoresNew.setScores(scores_);
                            scoresNew.setClass_(student.getClasses());
                            scoresNew.setYear_(student.getYear_());
                            scoresNew.setConsume_time(student.getConsume_time());
                            scoresNew.setDate_(System.currentTimeMillis());
                            scoresNew.setDevices(examzation.getDevices());
                            scoresNew.setName(student.getUsername());
                            scoresNew.setXuehao(student.getXuehao());
//                           保存学生成绩
                            saveScoresInAsynTask(scoresNew);

//                            BaseApplication.app.daoSession.getStudentDao().save(student);

                        }
                    }
                }
            }
        };

    }

    private void afterSaveScores() {
        new AlertDialog.Builder(MainActivity.this).setMessage("考试结束")
                .setPositiveButton("查看成绩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showResultActivity();
                    }
                }).show();
        // 判断答案
//        Toast.makeText(MainActivity.this, "考试时间结束", Toast.LENGTH_SHORT).show();
    }

    private void saveScoresInAsynTask(Scores scores) {

        AsyncTask task = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                closeDialog();
                ResponseSaveScoreData data = (ResponseSaveScoreData) o;
                if (data != null) {
                    if (data.getError() == 0) {
                        showResultHandler.sendEmptyMessage(0);
                    } else {
                        Toast.makeText(MainActivity.this, "成功保存失败 " + data.getMsg(), Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "保存成绩出错", Toast.LENGTH_LONG);
                }
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                Scores scores1 = (Scores) objects[0];
                return AbstractNet.getInstance().saveScores(scores1);
            }
        };
        task.execute(scores);
    }

    private void showResultActivity() {
        Intent intent = new Intent(MainActivity.this, ShowResultActivity.class);
        intent.putExtra("scores", scoresNew);
        startActivityForResult(intent, 123);
    }

    private void initView() {
        //创建中部标签浏览界面
        createTabHost();
        // 滑动区域冲突
        listscroll = (ListScrollView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.listscroll);
        listscroll.setListView(theFailurePointSetGV);
        content = (LinearLayout) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.layout_content);
        // 点火
        ignitionButton = (Button) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Ignition);
        ignitionButton.setOnClickListener(this);
        // 启动
        startButton = (Button) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Start);
        startButton.setOnClickListener(this);
        startButton.setOnTouchListener(this);
        // 熄火
        shutDownButton = (Button) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_ShutDown);
        shutDownButton.setOnClickListener(this);
        // 故障点说明
        findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_PointOfFailureThat).setOnClickListener(this);
        // 状态读取
        findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_StateIsRead).setOnClickListener(this);
        // 全部设置
        findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_SetAll).setOnClickListener(this);
        // 全部清除
        findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_ClearAll).setOnClickListener(this);
        // 全部清除
        findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Mode_Teach02).setOnClickListener(this);
        tvFileName = (AutofitTextView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tv_name);
        btnSend = (Button) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.button_send);
        btnSend.setOnClickListener(this);
        etTime = (EditText) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.et_text);
        layout_student = (ViewGroup) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.layout_student);
        layout_teacher = (ViewGroup) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.layout_teacher);

        loadExamnination();

    }

    private void initStudentConsole() {
        // 读取故障点说明文件名称
//        if (BaseApplication.app.descStrFile != null) {
//            setFileName(BaseApplication.app.descStrFile);
//        }

        layout_student.setVisibility(View.VISIBLE);
        layout_teacher.setVisibility(View.GONE);
        button_start_exam = (Button) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.button_start_exam);
        button_submit = (Button) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.button_submit);

        button_start_exam.setOnClickListener(this);
        button_submit.setOnClickListener(this);
        tv_exam_tip_break = (TextView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tv_exam_tip_break);
        tv_exam_tip_false = (TextView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tv_exam_tip_false);
        tv_exam_tip_short = (TextView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tv_exam_tip_short);
        tv_lasttime = (TextView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tv_lasttime);
//            获取考试题
        if (examzation == null) {
            Toast.makeText(this, "教师还未出题", Toast.LENGTH_SHORT).show();
            tv_exam_tip_break.setVisibility(View.GONE);
            tv_exam_tip_false.setVisibility(View.GONE);
            tv_exam_tip_short.setVisibility(View.GONE);
        } else {
            // 显示考试时间 只是展示时间
            tv_lasttime.setText("本次考试时间：" + examzation.getMinutes() + "分钟");
            tv_exam_tip_break.setVisibility(View.VISIBLE);
            tv_exam_tip_false.setVisibility(View.VISIBLE);
            tv_exam_tip_short.setVisibility(View.VISIBLE);
//                etTime.setText(examzation.getMinutes()+"");
//                Toast.makeText(this, "time is " + examzation.getMinutes(), Toast.LENGTH_SHORT).show();
            calcCount();
            // 设置题目数量
            String strbreak = getString(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.already_start3, breakCount + "");
            tv_exam_tip_break.setText(strbreak);
            String strFalse = getString(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.already_start4, falsecount + "");
            tv_exam_tip_false.setText(strFalse);
            String strShort = getString(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.already_start5, shotCount + "");
            tv_exam_tip_short.setText(strShort);
            // 设置设备名称
            setFileName(examzation.getDevices());
        }
    }

    private void initExamnination() {
        int countShort = BaseApplication.app.shortList.size();
        for (int i = 0; i < countShort; i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            relay.setExamination(false);
        }
        int countBreak = BaseApplication.app.breakfaultList.size();
        for (int i = 0; i < countBreak; i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            relay.setExamination(false);
        }
        int countFalse = BaseApplication.app.falseList.size();
        for (int i = 0; i < countFalse; i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            relay.setExamination(false);
        }
    }

    // 恢复颜色
    private void setInitGreenColor() {
        int countShort = BaseApplication.app.shortList.size();
        for (int i = 0; i < countShort; i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            relay.setState(Relay.Green);
            relay.setStdentClick(false);
        }
        int countBreak = BaseApplication.app.breakfaultList.size();
        for (int i = 0; i < countBreak; i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            relay.setState(Relay.Green);
            relay.setStdentClick(false);
        }
        int countFalse = BaseApplication.app.falseList.size();
        for (int i = 0; i < countFalse; i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            relay.setState(Relay.Green);
            relay.setStdentClick(false);
        }
    }

    // 恢复颜色
    private void setInitGreenColorAndInitExamation() {
        if (BaseApplication.app.shortList == null) {
            BaseApplication.app.shortList = new ArrayList<>();
        }
        if (BaseApplication.app.breakfaultList == null) {
            BaseApplication.app.breakfaultList = new ArrayList<>();
        }
        if (BaseApplication.app.falseList == null) {
            BaseApplication.app.falseList = new ArrayList<>();
        }
        setInitGreenColor();
        initExamnination();
    }

    private void loadExamnination2DataList() {
        String strBreak = examzation.getBreak_();
        if (!TextUtils.isEmpty(strBreak)) {
            String[] breaks = strBreak.split(",");
            for (int i = 0; i < breaks.length; i++) {
                if (!TextUtils.isEmpty(breaks[i])) {
                    try {
                        int position = Integer.parseInt(breaks[i]);
                        System.out.println("use init data ...." + BaseApplication.app.breakfaultList);
                        Relay relay = BaseApplication.app.breakfaultList.get(position);
                        relay.setExamination(true);
                    } catch (Exception e) {
                        Log.d("MainActivity", "解析题库出错，" + breaks[i]);
                    }
                }
            }
        }
        String strFalse_ = examzation.getFalse_();
        if (!TextUtils.isEmpty(strFalse_)) {
            String[] falses = strFalse_.split(",");
            for (int i = 0; i < falses.length; i++) {
                if (!TextUtils.isEmpty(falses[i])) {
                    try {
                        int position = Integer.parseInt(falses[i]);
                        Relay relay = BaseApplication.app.falseList.get(position);
                        relay.setExamination(true);
                    } catch (Exception e) {
                        Log.d("MainActivity", "解析题库出错，" + falses[i]);
                    }
                }
            }
        }
        String strShort = examzation.getShort_();
        if (!TextUtils.isEmpty(strShort)) {
            String[] shorts = strShort.split(",");
            for (int i = 0; i < shorts.length; i++) {
                if (!TextUtils.isEmpty(shorts[i])) {
                    try {
                        int position = Integer.parseInt(shorts[i]);
                        Relay relay = BaseApplication.app.shortList.get(position);
                        relay.setExamination(true);
                    } catch (Exception e) {
                        Log.d("MainActivity", "解析题库出错，" + shorts[i]);
                    }
                }
            }
        }
        // 设置题目总数
        count = getSourceCount();
        Log.d("MainActivity", "题库加载完毕");
    }


    private int getCurentTab() {
        return tabHost.getCurrentTab();
    }

    /**
     * 初始化继电器状态数据
     */
    private void initData(int pos) {

        Intent intent = getIntent();
        student = (Student) intent.getSerializableExtra("student");
        scores = (Scores) intent.getSerializableExtra("scores");
        if (BaseApplication.app.shortList == null) {
            BaseApplication.app.shortList = new ArrayList<>();
        }
        if (BaseApplication.app.breakfaultList == null) {
            BaseApplication.app.breakfaultList = new ArrayList<>();
        }
        if (BaseApplication.app.falseList == null) {
            BaseApplication.app.falseList = new ArrayList<>();
        }

//        if(student!=null){
//            sdfasdf
//        }
        if (BaseApplication.app.shortList.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                BaseApplication.app.shortList.add(new Relay(i + 1, i + 1, Relay.Green, ConstValue.type_shortFault));
            }

            for (int i = 0; i < 100; i++) {
                BaseApplication.app.breakfaultList.add(new Relay(i + 1, i + 1, Relay.Green, ConstValue.type_break));
            }

            for (int i = 0; i < 20; i++) {
                BaseApplication.app.falseList.add(new Relay(i + 1, i + 1, Relay.Green, ConstValue.type_false));
            }
        }

        System.out.println("init data ....");
        // 不再初始化
        if (BaseApplication.app.faultboardOption == null) {
            switch (pos) {
                case 0:
                    BaseApplication.app.faultboardOption = new FaultboardOption(this, faultboardOptionHandler,
                            BaseApplication.app.breakfaultList, this);
                    break;
                case 1:
                    BaseApplication.app.faultboardOption = new FaultboardOption(this, faultboardOptionHandler,
                            BaseApplication.app.falseList, this);
                    break;
                case 2:
                    BaseApplication.app.faultboardOption = new FaultboardOption(this, faultboardOptionHandler,
                            BaseApplication.app.shortList, this);
                    break;
            }
        }
        // 初始化颜色为绿色 并且清除上一个学生答题的结果
        setInitGreenColor();
        //

    }

    private void initHandler() {
        theFailurePointSetGVHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!begin) {
                    Toast.makeText(MainActivity.this, "还没开始考试,请点击开始考试", Toast.LENGTH_SHORT).show();
                    return;
                }
                Relay relay = (Relay) msg.obj;
                // add by dyg
                // 在adapter中判断红色是examnation
                if (goOnCount) {
                    if (relay.isStdentClick()) {
                        relay.setStdentClick(false);
                        relay.setState(Relay.Green);
                    } else {
                        relay.setStdentClick(true);
                        relay.setState(Relay.Yellow);
                    }
                    int count = getSourceCount(relay.getCategory());
                    int clickCount = getClickCount(relay.getCategory());
                    if (clickCount > count) {
//                        Toast.makeText(MainActivity.this, "已答" + clickCount + "道题,本次点击答题无效", Toast.LENGTH_SHORT).show();
                        relay.setStdentClick(false);
                        relay.setState(Relay.Green);
                    }
                }
                theFailurePointSetAdapter.notifyDataSetChanged();
            }
        };

        faultboardOptionHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                theFailurePointSetAdapter.notifyDataSetChanged();
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initSDcard() {
        if (ConstValue.haveSdcard()) {
            File file = new File(ConstValue.get_DIR());
            if (!file.exists()) {
                file.mkdirs();
            }
            File title = new File(ConstValue.get_title_File());
            if (!title.exists()) {
                try {
                    title.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "创建文件失败了！！", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            nosdcard();
        }
    }


    public static String readTitleStr(Context context, File file) {
        String str = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((str = reader.readLine()) != null) {
                return str;
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "读取文件出错！", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "读取文件出错！", Toast.LENGTH_LONG).show();
        }
        return str;
    }

    private LoadingDialog mDialog;

    private void showDialog() {
        if (mDialog == null)
            mDialog = new LoadingDialog(this);
        if (!mDialog.isShowing()) mDialog.show();
    }

    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    private void loadExamnination() {
        @SuppressLint("StaticFieldLeak") AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return AbstractNet.getInstance().loadExamination();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();

            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                closeDialog();
                ResponseLoadExamData data = (ResponseLoadExamData) o;
                if (data != null) {
                    if (data.getError() == 0) {
                        examzation = data.getData();
                        // 加载考试题数据
                        if (examzation != null) {
                            loadExamnination2DataList();
                            //设置面板，并且设置设备名称
                            initStudentConsole();
                            // 设置设备格子数据
                            setDatas(examzation.getDeviceFileDatas());
                        } else {
                            initExamnination();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "" + data.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "返回异常", Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();
    }

    private void setDatas(String deviceFileDatas) {
        if (TextUtils.isEmpty(deviceFileDatas)) {
            System.out.println("set file data is null");
            return;
        }

        String[] strs = deviceFileDatas.split(ConstValue.SERIAL_DELIMTER);
        if (strs.length == 0) {
            System.out.println("set file data is null");
            return;
        }
        List<FaultBean> faultBeans = new ArrayList<>();
        for (int i = 0; i < strs.length; i++) {

            String line = strs[i];
            FaultBean fb = null;
            // 断路
            if (line.startsWith(ConstValue.type_break_str)) {

                fb = new FaultBean();
                fb.setType(ConstValue.type_break);
                fb.setValue(line.substring(1));

                // 虚接
            } else if (line.startsWith(ConstValue.type_false_str)) {
                fb = new FaultBean();
                fb.setType(ConstValue.type_false);
                fb.setValue(line.substring(1));
                // 短路
            } else if (line.startsWith(ConstValue.type_shortFault_str)) {
                fb = new FaultBean();
                fb.setType(ConstValue.type_shortFault);
                fb.setValue(line.substring(1));
            }
            if (fb != null) {
                faultBeans.add(fb);
            }
            // 前面代码组装数据
        }
        setValues(faultBeans);


    }


    private boolean begin = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 开始考试
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.button_start_exam:
                // 显示倒计时，
                // 显示红色提示view，
                // 时间结束显示对话框，
                // 考试结束，自动提交考试结果。
                begin = true;

                boolean canStartExam = checkDbHaveExamination();
                boolean haveExamed = checkifStudentExamed();
                if (canStartExam && !haveExamed) {
                    tv_exam_tip_break.setVisibility(View.VISIBLE);
                    tv_exam_tip_false.setVisibility(View.VISIBLE);
                    tv_exam_tip_short.setVisibility(View.VISIBLE);
                    startCountNumber();
                    button_start_exam.setVisibility(View.GONE);
                }

                break;
            // 提交答案
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.button_submit:
                if (!begin) {
                    Toast.makeText(this, "请先点击开始考试按钮", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (examzation == null) {
                    Toast.makeText(this, "教师还未出题，不能计算成绩", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 停止计时
                stopCount();

                float scores = getScores();
                // 判断分数写入数据库，跳转到显示分数界面
                scoresNew = new Scores();
                scoresNew.setXuehao(student.getXuehao());
                scoresNew.setName(student.getUsername());
                scoresNew.setClass_(student.getClasses());
                scoresNew.setYear_(student.getYear_());
                scoresNew.setScores((int) scores);
                scoresNew.setDevices(examzation.getDevices());
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                scoresNew.setDate_(System.currentTimeMillis());
                int minutes = examzation.getMinutes();
//                消耗时间计算
                scoresNew.setConsume_time(minutes - (cousumeSeconds / 60) + "");
//                BaseApplication.app.daoSession.getStudentDao().save(student);

                saveScoresInAsynTask(scoresNew);
//                examzation.setExpired(true);
                // 保存
//                BaseApplication.app.daoSession.getExaminationDao().update(examzation);
//                showResultActivity();

                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Ignition:// 点火
                if (hasStarted) {
                    Toast.makeText(this, "The machine had be started !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    hasStarted = true;
                    Toast.makeText(this, "The machine be started !",
                            Toast.LENGTH_SHORT).show();
                    BaseApplication.app.faultboardOption.ignition(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Ignition);
                }
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_ShutDown:// 熄火
                if (hasStarted) {
                    hasStarted = false;
                    Toast.makeText(this, "The machine be shut down !",
                            Toast.LENGTH_SHORT).show();
                    BaseApplication.app.faultboardOption.fireDown((byte) 0x65, com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_ShutDown);
                } else {
                    Toast.makeText(this, "The machine had not be started !",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_PointOfFailureThat:// 故障点说明
//                PointOfFailureThatDialog pointOfFailureThatDialog = new PointOfFailureThatDialog(
//                        this);
//                pointOfFailureThatDialog.show();
                MediaFileListDialogMainpage dialog = new MediaFileListDialogMainpage(this);
                dialog.show();
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_StateIsRead:// 状态读取
                BaseApplication.app.faultboardOption.stateIsRead(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_StateIsRead);
                faultboardOptionHandler.sendEmptyMessageDelayed(0, 500);
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_SetAll:// 全部设置
                BaseApplication.app.faultboardOption.setAll(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_SetAll);
                faultboardOptionHandler.sendEmptyMessageDelayed(0, 500);
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_ClearAll:// 全部清除
                BaseApplication.app.faultboardOption.clearAll(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_ClearAll);
                faultboardOptionHandler.sendEmptyMessageDelayed(0, 500);
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Mode_Teach02:// 教学模式
                if (ConstValue.haveSdcard()) {
                    File file = new File(ConstValue.get_DIR());
                    if (!file.exists()) {
                        Toast.makeText(MainActivity.this, "没有文件可以显示！文件路径 " + ConstValue.get_DIR(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    MediaFileListDialog bluetoothDevicesListDialog = new MediaFileListDialog(
                            MainActivity.this);
                    bluetoothDevicesListDialog.show();
                } else {
                    nosdcard();
                }

                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.button_send:
                // 检查是否可以出题，设备名称，考试时间，考题内容
                boolean isCanstart = checkCanMakeExamination();
                if (!isCanstart) {
                    return;
                }
                String str = etTime.getText().toString();
                String devicesName = tvFileName.getText().toString();
                if (examzation != null) {
                    examzation.setMinutes(Integer.parseInt(str));
                    examzation.setDevices(devicesName);
//                    dao.update(examzation);
                } else {
                    if (isCanstart) {
                        StringBuilder fasleString = new StringBuilder();
                        StringBuilder breakString = new StringBuilder();
                        StringBuilder shortString = new StringBuilder();
                        for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
                            Relay relay = BaseApplication.app.falseList.get(i);
                            if (relay.isExamination()) {
                                fasleString.append(i + ",");
                            }
                        }
                        for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
                            Relay relay = BaseApplication.app.breakfaultList.get(i);
                            if (relay.isExamination()) {
                                breakString.append(i + ",");
                            }
                        }
                        for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
                            Relay relay = BaseApplication.app.shortList.get(i);
                            if (relay.isExamination()) {
                                shortString.append(i + ",");
                            }
                        }
                        Examination examination = new Examination();
                        examination.setBreak_(breakString.toString());
                        examination.setFalse_(fasleString.toString());
                        examination.setShort_(shortString.toString());
                        examination.setMinutes(Integer.parseInt(str));
                        examination.setExpired(false);
                        examination.setDevices(devicesName);
//                        BaseApplication.app.daoSession.getExaminationDao().save(examination);
                    }
                    Toast.makeText(this, "考题发送完毕，学生可以考试", Toast.LENGTH_SHORT).show();
                    findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tv_tip2).setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    private int getClickCount(int type) {
        int resultCount = 0;
        switch (type) {
            case type_break:
                for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
                    Relay relay = BaseApplication.app.breakfaultList.get(i);
                    if (!relay.isStdentClick()) {
                        continue;
                    } else {
                        resultCount++;
                    }
                }
                break;
            case type_false:
                for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
                    Relay relay = BaseApplication.app.falseList.get(i);
                    if (!relay.isStdentClick()) {
                        continue;
                    } else {
                        resultCount++;
                    }
                }
                break;
            case type_shortFault:
                for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
                    Relay relay = BaseApplication.app.shortList.get(i);
                    if (!relay.isStdentClick()) {
                        continue;
                    } else {
                        resultCount++;
                    }
                }
                break;
        }


        return resultCount;
    }

    private void readState() {
        if (BaseApplication.app.faultboardOption != null && BaseApplication.app.faultboardOption.isConneted()) {
            BaseApplication.app.faultboardOption.stateIsRead(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_StateIsRead);
            faultboardOptionHandler.sendEmptyMessageDelayed(0, 500);
        }
    }

    private int breakCount = 0;
    private int falsecount = 0;
    private int shotCount = 0;

    private void calcCount() {
        for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                breakCount++;
            }
        }
        for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                falsecount++;
            }
        }
        for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                shotCount++;
            }
        }
    }

    private int getSourceCount() {
        int sourceCount = 0;
        for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                sourceCount++;
            }
        }
        for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                sourceCount++;
            }
        }
        for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                sourceCount++;
            }
        }
        return sourceCount;
    }

    private int getSourceCount(int category) {
        int resultCount = 0;
        switch (category) {
            case type_break:
                for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
                    Relay relay = BaseApplication.app.breakfaultList.get(i);
                    if (!relay.isExamination()) {
                        continue;
                    } else {
                        resultCount++;
                    }
                }
                break;
            case type_false:
                for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
                    Relay relay = BaseApplication.app.falseList.get(i);
                    if (!relay.isExamination()) {
                        continue;
                    } else {
                        resultCount++;
                    }
                }
                break;
            case type_shortFault:
                for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
                    Relay relay = BaseApplication.app.shortList.get(i);
                    if (!relay.isExamination()) {
                        continue;
                    } else {
                        resultCount++;
                    }
                }
                break;

        }

        return resultCount;
    }

    private float getScores() {
        int sourceCount = 0;
        int rightCount = 0;
        for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                sourceCount++;
                if (relay.isStdentClick()) {
                    rightCount++;
                }
            }
        }
        for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                sourceCount++;
                if (relay.isStdentClick()) {
                    rightCount++;
                }
            }
        }
        for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            if (!relay.isExamination()) {
                continue;
            } else {
                sourceCount++;
                if (relay.isStdentClick()) {
                    rightCount++;
                }
            }
        }
        if (sourceCount == 0) {
            Toast.makeText(this, "教师没有出题，无法计算分数", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return (100f / sourceCount) * rightCount;
    }

    private boolean goOnCount = false;

    private void stopCount() {
        goOnCount = false;
    }

    private void startCountNumber() {

        int minutes = examzation.getMinutes();
        int seconds = 0;
        if (minutes > 0) {
            seconds = minutes * 60;
        } else {
            Toast.makeText(this, "考试时间异常", Toast.LENGTH_SHORT).show();
            return;
        }
        goOnCount = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int seconds = examzation.getMinutes() * 60;
                while (goOnCount && seconds >= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    seconds--;
                    Message msg = countHandler.obtainMessage();
                    msg.what = 1;
                    Bundle bun = new Bundle();
                    bun.putInt("time", seconds);
                    msg.setData(bun);
                    countHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private boolean checkDbHaveExamination() {
//        Examination examination = dao.queryBuilder()
//                .where(ExaminationDao.Properties.Expired
//                        .eq(new Boolean(false))).orderDesc(ExaminationDao.Properties.Id).limit(1).unique();
//        if (examination != null && !examination.getExpired()) {
//            return true;
//        } else {
//            Toast.makeText(this, "老师未出题，不能参加考试", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    private boolean checkIfHaveExamnation() {
//        Examination examination = dao.queryBuilder()
//                .where(ExaminationDao.Properties.Expired
//                        .eq(new Boolean(false))).orderDesc(ExaminationDao.Properties.Id).limit(1).unique();
//        if (examination != null && !examination.getExpired()) {
//            return true;
//        } else {
//            return false;
//        }
        return true;
    }

    private boolean checkifStudentExamed() {
        if (scores != null && scores.getScores() != null) {
            Toast.makeText(this, "您已经考过试了", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean checkCanMakeExamination() {

//        Examination examination = dao.queryBuilder()
//                .where(ExaminationDao.Properties.Expired
//                        .eq(new Boolean(false))).orderDesc(ExaminationDao.Properties.Id).unique();
//        if (examination != null && !examination.getExpired()) {
//            Toast.makeText(this, "考题已经发送完毕!", Toast.LENGTH_SHORT).show();
//        }
        String devicesName = tvFileName.getText().toString();

        if (TextUtils.isEmpty(devicesName)) {
            Toast.makeText(this, "请点击\"故障点说明\"选择对应设备文件", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((devicesName + ".txt").equals(LoginActivity.companyFileName)) {
            Toast.makeText(this, "请选择正确的故障点说明文件！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String str = etTime.getText().toString();
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(this, "请设置考试时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 已经发送完了考题
        int count = getSourceCount();

        if (count == 0) {
            Toast.makeText(this, "还没有选择考题", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (count > 20) {
            Toast.makeText(this, "最多出20道题，当前已出题" + count + "道", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void nosdcard() {
        Toast.makeText(MainActivity.this, "没有sdcard,无法显示教学文件", Toast.LENGTH_LONG).show();
    }

    public static ArrayList<String> getDirFilesDir(File file) {
        ArrayList<String> list = new ArrayList<String>();
        if (file == null) {
            return list;
        }
        for (File f : file.listFiles()) {
            list.add(f.getAbsolutePath());
        }
        return list;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (SpDataUtils.TYPE_TEACHER.equals(loginType)) {
            getMenuInflater().inflate(com.xiaobailong_student.bluetoothfaultboardcontrol.R.menu.main, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.action_settings:
                if (!BaseApplication.app.faultboardOption.isConneted()) {
                    BaseApplication.app.faultboardOption.bluetoothConnect(MainActivity.this);
                } else {
                    Toast.makeText(this, "还在连接当中", Toast.LENGTH_SHORT).show();
                }
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.action_exit:
                exit();
                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.action_close:
                BaseApplication.app.faultboardOption.closeBluetoothSocket();
                Toast.makeText(MainActivity.this, "连接已断开",
                        Toast.LENGTH_SHORT).show();
//                initData(getCurentTab());
                setInitGreenColorAndInitExamation();
                // updateDatabase
                if (examzation != null) {
//                    BaseApplication.app.daoSession.getExaminationDao().update(examzation);
                }
                if (theFailurePointSetAdapter != null) {
                    theFailurePointSetAdapter.notifyDataSetChanged();
                }

                break;
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.action_edit_title:
                editTitle();
                break;
            default:
                break;
        }
        return true;
    }

    private void editTitle() {
        startActivityForResult(new Intent(MainActivity.this, WriteTitleActivity.class), 100);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                File file = new File(ConstValue.get_title_File());
                String title = readTitleStr(MainActivity.this, file);
                if (title == null || title.equals("")) {
                    title = getString(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.app_name);
                }
                getActionBar().setTitle(title);
            }
        } else if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
//                Toast.makeText(this, "考试结束跳转页面", Toast.LENGTH_SHORT).show();
//                EntryActivity.studentLogin(MainActivity.this);
                finish();
            }
        }
    }

    @Override
    public void finish() {
//        BaseApplication.app.faultboardOption.closeBluetoothSocket();
        super.finish();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            Toast.makeText(this, getString(R.string.Button_Back_BeCanceled),
//                    Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Start:// 启动
                if (hasStarted) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Toast.makeText(this, "StartDown", Toast.LENGTH_SHORT).show();
                        BaseApplication.app.faultboardOption.startDown((byte) 0x66, com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Start);
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Toast.makeText(this, "StartUp", Toast.LENGTH_SHORT).show();
                        BaseApplication.app.faultboardOption.startUp((byte) 0x66, com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.Button_Start);
                    }
                } else {
                    Toast.makeText(this, "The machine had not be started !",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    private void createTabHost() {
        {
            tabHost = (TabHost) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.tabhost);
            tabWidget = (TabWidget) findViewById(android.R.id.tabs);
            tabHost.setup();
            tabHost.bringToFront();

            ArrayList<TabHost.TabSpec> hostlist = new ArrayList<TabHost.TabSpec>(
                    2);

            TabHost.TabSpec tabspec1 = tabHost.newTabSpec("0");
            tabspec1.setContent(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.GridView_TheFailurePointSet);
            TextView indicatorV = new TextView(this);
            indicatorV.setGravity(Gravity.CENTER);
            indicatorV.setBackgroundResource(com.xiaobailong_student.bluetoothfaultboardcontrol.R.drawable.channelsbg);
            indicatorV.setTextSize(16);
            indicatorV.setText(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.breakfault);
            tabspec1.setIndicator(indicatorV);
            hostlist.add(tabspec1);


            tabspec1 = tabHost.newTabSpec("2");
            tabspec1.setContent(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.GridView_TheFailurePointSet);
            indicatorV = new TextView(this);
            indicatorV.setGravity(Gravity.CENTER);
            indicatorV.setBackgroundResource(com.xiaobailong_student.bluetoothfaultboardcontrol.R.drawable.channelsbg);
            indicatorV.setTextSize(16);
            indicatorV.setText(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.falsefault);
            tabspec1.setIndicator(indicatorV);
            hostlist.add(tabspec1);

            tabspec1 = tabHost.newTabSpec("1");
            tabspec1.setContent(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.GridView_TheFailurePointSet);
            indicatorV = new TextView(this);
            indicatorV.setGravity(Gravity.CENTER);
            indicatorV.setBackgroundResource(com.xiaobailong_student.bluetoothfaultboardcontrol.R.drawable.channelsbg);
            indicatorV.setTextSize(16);
            indicatorV.setText(com.xiaobailong_student.bluetoothfaultboardcontrol.R.string.shortfault);
            tabspec1.setIndicator(indicatorV);
            hostlist.add(tabspec1);


            int j = hostlist.size();
            for (int i = 0; i < j; i++) {
                tabHost.addTab(hostlist.get(i));
            }

            tabHost.setCurrentTab(0);
            View view = tabWidget.getChildAt(0);
            view.setBackgroundDrawable(getResources().getDrawable(
                    com.xiaobailong_student.bluetoothfaultboardcontrol.R.drawable.presschannelbg));

            theFailurePointSetAdapter = new TheFailurePointSetAdapter(this,
                    BaseApplication.app.shortList, theFailurePointSetGVHandler);

            theFailurePointSetGV = (GridView) findViewById(com.xiaobailong_student.bluetoothfaultboardcontrol.R.id.GridView_TheFailurePointSet);
            theFailurePointSetGV.setAdapter(theFailurePointSetAdapter);
            theFailurePointSetGV.setOnScrollListener(this);
            theFailurePointSetGV.setVisibility(View.VISIBLE);
            theFailurePointSetGV.setNumColumns(3);

            tabHost.setOnTabChangedListener(new OnTabChangeListener() {
                public void onTabChanged(String tabId) {

                    changeListData(tabId);
                    for (int i = 0; i < tabWidget.getChildCount(); i++) {
                        View view = tabWidget.getChildAt(i);
                        if (tabHost.getCurrentTab() == i) {
                            view.setBackgroundDrawable(getResources()
                                    .getDrawable(com.xiaobailong_student.bluetoothfaultboardcontrol.R.drawable.presschannelbg));
                        } else {
                            view.setBackgroundDrawable(getResources()
                                    .getDrawable(com.xiaobailong_student.bluetoothfaultboardcontrol.R.drawable.channelsbg));
                        }
                    }
                    // 读取状态
                    if (SpDataUtils.getLoginType().equals(SpDataUtils.TYPE_TEACHER)) {
                        readState();
                    }
                }
            });

            changeListData("0");
            if (SpDataUtils.getLoginType().equals(SpDataUtils.TYPE_TEACHER)) {
                readState();
            }
        }

    }

    private void changeListData(String tabId) {
        // 短路故障
        if (tabId.equals("1")) {
            TableState = ShortTable;
            theFailurePointSetAdapter = new TheFailurePointSetAdapter(this,
                    BaseApplication.app.shortList, theFailurePointSetGVHandler);
            theFailurePointSetGV.setAdapter(theFailurePointSetAdapter);
            BaseApplication.app.faultboardOption.setArray(BaseApplication.app.shortList);
        } else if (tabId.equals("2")) { // 虚接故障
            TableState = FalseTable;
            theFailurePointSetAdapter = new TheFailurePointSetAdapter(this,
                    BaseApplication.app.falseList, theFailurePointSetGVHandler);
            theFailurePointSetGV.setAdapter(theFailurePointSetAdapter);
            BaseApplication.app.faultboardOption.setArray(BaseApplication.app.falseList);
        } else {// 断路故障
            TableState = BreakTable;
            theFailurePointSetAdapter = new TheFailurePointSetAdapter(this,
                    BaseApplication.app.breakfaultList, theFailurePointSetGVHandler);
            theFailurePointSetGV.setAdapter(theFailurePointSetAdapter);
            BaseApplication.app.faultboardOption.setArray(BaseApplication.app.breakfaultList);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void setFileName(String deviceName) {
        if (!TextUtils.isEmpty(deviceName) && !this.isFinishing()) {
            tvFileName.setVisibility(View.VISIBLE);
            final String[] names = deviceName.split("\\.");
            if (names.length > 0) {

                tvFileName.setText(names[0]);

//                if (names.length > 2) {
//                    String lastword = names[0].substring(names[0].length() - 2, names[0].length() - 1);
//                    countHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            afterSet(names[0]);
//                        }
//                    }, 2000);
//                }

                // 修复bug:设置之后textview的高度不会减小
                afterSet(names[0]);

            }
        } else {
            tvFileName.setVisibility(View.GONE);
        }
    }

    private void afterSet(final String str) {
        tvFileName.setText(str + "..");
        countHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                tvFileName.setText(str);
            }
        }, 200);
    }

    public void setValues(List<FaultBean> datas) {
        if (datas == null) {
            return;
        }
        if (datas.size() == 0) {
            resetValues();
        }

        for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            for (int j = 0; j < datas.size(); j++) {
                FaultBean faultBean = datas.get(j);
                if (faultBean.getType() == ConstValue.type_break) {
                    relay.setValue(faultBean.getValue());
                    relay.setType(faultBean.getType());
                    datas.remove(faultBean);
                    break;
                }

            }
        }
        for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            for (int j = 0; j < datas.size(); j++) {
                FaultBean faultBean = datas.get(j);
                if (faultBean.getType() == ConstValue.type_false) {
                    relay.setValue(faultBean.getValue());
                    relay.setType(faultBean.getType());
                    datas.remove(faultBean);
                    break;
                }

            }
        }
        for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            for (int j = 0; j < datas.size(); j++) {
                FaultBean faultBean = datas.get(j);
                if (faultBean.getType() == ConstValue.type_shortFault) {
                    relay.setValue(faultBean.getValue());
                    relay.setType(faultBean.getType());
                    datas.remove(faultBean);
                    break;
                }

            }
        }
        theFailurePointSetAdapter.notifyDataSetChanged();
//        Toast.makeText(this, "替换完毕", Toast.LENGTH_SHORT).show();

    }

    private void resetValues() {
        for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
            Relay relay = BaseApplication.app.breakfaultList.get(i);
            relay.setValue((i + 1) + "");
            relay.setType(type_none);
        }
        for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
            Relay relay = BaseApplication.app.falseList.get(i);
            relay.setValue((i + 1) + "");
            relay.setType(type_none);

        }
        for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
            Relay relay = BaseApplication.app.shortList.get(i);
            relay.setValue((i + 1) + "");
            relay.setType(type_none);

        }
        theFailurePointSetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (examzation != null) {

            // 已经发送完了考题
            int countCheck = getSourceCount();
            if (countCheck == 0) {
                examzation.setExpired(true);
//                BaseApplication.app.daoSession.getExaminationDao().delete(examzation);
                finish();
                return;
            }

            if (!checkCanMakeExamination()) {
            } else {
                // 不管学生还是老师登陆都要保存一次
                int count = 0;
                StringBuilder fasleString = new StringBuilder();
                StringBuilder breakString = new StringBuilder();
                StringBuilder shortString = new StringBuilder();
                for (int i = 0; i < BaseApplication.app.falseList.size(); i++) {
                    Relay relay = BaseApplication.app.falseList.get(i);
                    if (relay.isExamination()) {
                        fasleString.append(i + ",");
                        count++;
                    }
                }
                for (int i = 0; i < BaseApplication.app.breakfaultList.size(); i++) {
                    Relay relay = BaseApplication.app.breakfaultList.get(i);
                    if (relay.isExamination()) {
                        breakString.append(i + ",");
                        count++;
                    }
                }
                for (int i = 0; i < BaseApplication.app.shortList.size(); i++) {
                    Relay relay = BaseApplication.app.shortList.get(i);
                    if (relay.isExamination()) {
                        shortString.append(i + ",");
                        count++;
                    }
                }
                examzation.setBreak_(breakString.toString());
                examzation.setFalse_(fasleString.toString());
                examzation.setShort_(shortString.toString());
                if (count == 0) {
                    examzation.setExpired(true);
                }
//                BaseApplication.app.daoSession.getExaminationDao().update(examzation);
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onConnected() {
        if (SpDataUtils.getLoginType().equals(SpDataUtils.TYPE_TEACHER)) {
            readState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭第一个页面清除数据
        clearData();
    }

    private void clearData() {
        if (BaseApplication.app.faultboardOption != null) {
            BaseApplication.app.faultboardOption.closeBluetoothSocket();
            BaseApplication.app.faultboardOption = null;
        }
        BaseApplication.app.descStrFile = null;
        BaseApplication.app.shortList = null;
        BaseApplication.app.falseList = null;
        BaseApplication.app.breakfaultList = null;

    }
}
