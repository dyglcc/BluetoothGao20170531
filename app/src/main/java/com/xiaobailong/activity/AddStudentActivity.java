package com.xiaobailong.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.Student;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dongyuangui on 2017/6/2.
 */

public class AddStudentActivity extends BaseActivity {
    @BindView(R.id.xuehao)
    EditText xuehao;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.btnMan)
    RadioButton btnMan;
    @BindView(R.id.btnWoman)
    RadioButton btnWoman;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.ids)
    EditText ids;
    @BindView(R.id.android_btn)
    Button androidBtn;
    @BindView(R.id.scroll)
    ScrollView scroll;

    private Classes classes;
    private Years years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_add_student);
        ButterKnife.bind(this);
        classes = (Classes) getIntent().getSerializableExtra("classes");
        years = (Years) getIntent().getSerializableExtra("years");
        if (classes == null) {
            Toast.makeText(this, "没有班级信息", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @OnClick(R.id.android_btn)
    public void save() {
        String xuehaoStr = xuehao.getText().toString().trim();
        String usernamestr = username.getText().toString().trim();
        String mobileStr = mobile.getText().toString().trim();
        String idsStr = ids.getText().toString().trim();


        if (TextUtils.isEmpty(xuehaoStr)) {
            Toast.makeText(this, "请输入学号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usernamestr)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        // save database
        Student student = new Student();
        student.setXuehao(xuehaoStr);
        student.setUsername(usernamestr);
        student.setIds(idsStr);
        student.setMobile(mobileStr);
        student.setClasses(classes.getId());
        student.setYear_(years.getId());

        if (btnMan.isChecked()) {
            student.setSex("男");
        } else {
            student.setSex("女");
        }
        BaseApplication.app.daoSession.getStudentDao().save(student);

        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, StudentManageActivity.class);
        intent.putExtra("added", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
