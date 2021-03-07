package com.xiaobailong.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.display.google_auth.Checker;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Examination;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.LoginActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.tools.CheckPermissionUtils;
import com.xiaobailong.tools.ConstValue;
import com.xiaobailong.tools.SpDataUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by dongyuangui on 2017/6/1.
 */

public class EntryActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE = 111;
    private static final int REQUEST_CAMERA_PERM = 101;
    @BindView(R.id.btn_teacher)
    Button btnTeacher;
    @BindView(R.id.btn_student)
    Button btnStudent;
    @BindView(R.id.img_brand)
    ImageView brandIV;
    private String savePath = null;
    private boolean hasSdcard = false;
    private String companyBrandFileName = "brand.png";
    public static String backgroundFileName = "background.jpg";

    ImageView rl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_entry);
        SharedPreferences sharedPreferences = this.getSharedPreferences(ConstValue.SP_NAME, 0);
        int orentation = sharedPreferences.getInt(ConstValue.xbl_orentation, 1);
        setRequestedOrientation(orentation);
        ButterKnife.bind(this);
//        if (Build.VERSION.SDK_INT >= 23) {
//            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            ActivityCompat.requestPermissions(this, mPermissionList, 123);
//        }
        initPermission();
        getSdcardPath();
        initUI();
    }

    private void initUI() {
        rl = (ImageView) findViewById(R.id.login_Background);
        if (savePath != null && hasSdcard) {
            File file = new File(savePath + backgroundFileName);
            if (file.exists()) {
                rl.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(savePath + backgroundFileName)));
//                rl.setImageDrawable(new BitmapDrawable());
            }
        }
//        init brand image
        brandIV = (ImageView) findViewById(R.id.img_brand);
        if (savePath != null && hasSdcard) {
            File file = new File(savePath + companyBrandFileName);
            if (file.exists()) {
                brandIV.setBackgroundDrawable(new BitmapDrawable(savePath + companyBrandFileName));
            }
        }
    }

    @OnClick(R.id.btn_student)
    public void studentClick() {
        studentLogin(EntryActivity.this);
    }

    public static void studentLogin(Context context) {

        boolean check = check();
        if (!check) {
            showAuth(context, new GoOn() {
                @Override
                public void goOn() {
                    login_student(context);
                }
            });
        } else {
            login_student(context);
        }


    }

    public static void login_student(Context context) {
        SpDataUtils.saveLoginType(SpDataUtils.TYPE_STUDENT);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("type", SpDataUtils.TYPE_STUDENT);
        context.startActivity(intent);
    }

    @OnClick(R.id.btn_teacher)
    public void teacherClick() {
        boolean check = check();
        if (!check) {
            showAuth(this, new GoOn() {
                @Override
                public void goOn() {
                    loginTeacher();
                }
            });
        } else {
            loginTeacher();
        }

    }

    public void loginTeacher() {
        SpDataUtils.saveLoginType(SpDataUtils.TYPE_TEACHER);
        Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
        intent.putExtra("type", SpDataUtils.TYPE_TEACHER);
        startActivity(intent);
    }

    public void getSdcardPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            hasSdcard = true;
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/autoblue/";
//			Toast.makeText(this, sdcardPath, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(EntryActivity.this).setTitle("确定退出？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
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
        List<Examination> list = BaseApplication.app.daoSession.getExaminationDao().queryBuilder().list();
        for (int i = 0; i < list.size(); i++) {
            Examination examination = list.get(i);
            if (examination != null) {
                examination.setExpired(true);
                BaseApplication.app.daoSession.getExaminationDao().update(examination);
            }
        }
    }

    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            //是否登录
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(101)
    private void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            initUI();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "请开启文件读写权限，用于读取图片",
                    REQUEST_CAMERA_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i("SelectServerActivity", "执行onPermissionsGranted");
        boolean re = check();
        if (!re) {
            showAuth(EntryActivity.this, new GoOn() {
                @Override
                public void goOn() {

                }
            });
        }

//    Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show()
    }

    private static File getCheckFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.auth_bluetooth");
    }

    public static boolean check() {

        return getCheckFile().exists();
    }

    interface GoOn {
        void goOn();
    }

    public static void showAuth(Context context, GoOn goon) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        View viewParent = LayoutInflater.from(context).inflate(R.layout.auth, null);
        dialog.setContentView(viewParent);


        viewParent.findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checker test = new Checker();
                EditText editText = viewParent.findViewById(R.id.code);
                boolean result = test.verifyCode(editText.getText().toString().trim(), context, ConstValue.secret_key);
                if (result) {
                    try {
                        getCheckFile().createNewFile();
                    } catch (IOException e) {
                        Toast.makeText(context, "出错了！请先授权app读写权限", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                    goon.goOn();

                } else {
                    TextView tip = viewParent.findViewById(R.id.tip);
                    tip.setVisibility(View.VISIBLE);
                    editText.setText("");
                    tip.setText("验证码错误,请重新输入");
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.i("SelectServerActivity", "执行onPermissionsGranted");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(EntryActivity.this)
                    .setRationale("当前App需要申请sdcard的读写权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消")
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }
}
