package com.xiaobailong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.xiaobailong.activity.cms.YearsActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.MainActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.web.ServerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dongyuangui on 2017/6/1.
 */

public class MenuActivity extends BaseActivity {
    @BindView(R.id.btn_examination_setting)
    Button btnExaminationSetting;
    @BindView(R.id.btn_student_cms)
    Button btnStudentCms;
    @BindView(R.id.btn_pass_setting)
    Button btnPassSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_examination_setting)
    public void examination() {

        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.btn_server)
    public void server() {
        startActivity(new Intent(this, ServerActivity.class));
    }

    @OnClick(R.id.btn_student_cms)
    public void cms() {

        startActivity(new Intent(this, YearsActivity.class));
    }

    @OnClick(R.id.btn_pass_setting)
    public void passSetting() {

        startActivity(new Intent(this, PasswordSettingActivity.class));
    }
}
