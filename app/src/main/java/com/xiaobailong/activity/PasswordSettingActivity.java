package com.xiaobailong.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.tools.SpDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dongyuangui on 2017/6/1.
 */

public class PasswordSettingActivity extends BaseActivity {

    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.save)
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pass_setting);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.save)
    public void save() {
        String str = title.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            SpDataUtils.saveUserPwd(str);
            finish();
        } else {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
        }
    }
}
