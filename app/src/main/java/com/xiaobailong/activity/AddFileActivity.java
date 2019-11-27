package com.xiaobailong.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dongyuangui on 2017/6/1.
 */

public class AddFileActivity extends BaseActivity {
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.save)
    Button save;
    String strOld = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_add_file);
        ButterKnife.bind(this);
        strOld = getIntent().getStringExtra("filename");
        if (!TextUtils.isEmpty(strOld)) {
            title.setText(strOld);
        }
    }


    @OnClick(R.id.save)
    public void save() {
        String str = title.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            retunBack(str);
        } else {
            Toast.makeText(this, "请输入名称", Toast.LENGTH_SHORT).show();
        }
    }

    private void retunBack(String str) {
        Intent intent = getIntent();
        intent.putExtra("filename", str);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
