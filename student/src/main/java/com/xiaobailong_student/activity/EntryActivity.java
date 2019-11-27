package com.xiaobailong_student.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;

import com.xiaobailong_student.base.BaseApplication;
import com.xiaobailong_student.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong_student.bluetoothfaultboardcontrol.LoginActivity;
import com.xiaobailong_student.bluetoothfaultboardcontrol.R;
import com.xiaobailong_student.tools.SpDataUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by dongyuangui on 2017/6/1.
 */

public class EntryActivity extends BaseActivity {

    @BindView(R.id.btn_teacher)
    Button btnTeacher;
    @BindView(R.id.btn_student)
    Button btnStudent;
    @BindView(R.id.img_brand)
    ImageView brandIV;
    private String savePath = null;
    private boolean hasSdcard = false;
    private String companyBrandFileName = "brand.png";
    public static  String backgroundFileName = "background.jpg";

    ImageView rl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_entry);
        ButterKnife.bind(this);
//        if (Build.VERSION.SDK_INT >= 23) {
//            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            ActivityCompat.requestPermissions(this, mPermissionList, 123);
//        }

        getSdcardPath();
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

        SpDataUtils.saveLoginType(SpDataUtils.TYPE_STUDENT);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("type", SpDataUtils.TYPE_STUDENT);
        context.startActivity(intent);
    }

    @OnClick(R.id.btn_teacher)
    public void teacherClick() {
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

    }

}
