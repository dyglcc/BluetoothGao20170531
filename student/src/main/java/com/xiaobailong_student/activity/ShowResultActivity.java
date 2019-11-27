package com.xiaobailong_student.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaobailong_student.beans.Scores;
import com.xiaobailong_student.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong_student.bluetoothfaultboardcontrol.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dongyuangui on 2017/6/6.
 */

public class ShowResultActivity extends BaseActivity {

    Scores student = null;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_value)
    TextView tvNameValue;
    @BindView(R.id.tv_xuehao)
    TextView tvXuehao;
    @BindView(R.id.tv_xuehao_value)
    TextView tvXuehaoValue;
    @BindView(R.id.tv_devices)
    TextView tvDevices;
    @BindView(R.id.tv_devices_value)
    TextView tvDevicesValue;
    @BindView(R.id.tv_scores)
    TextView tvScores;
    @BindView(R.id.tv_scores_value)
    TextView tvScoresValue;
    @BindView(R.id.button_save)
    Button buttonSave;
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_show_result);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        student = (Scores) intent.getSerializableExtra("scores");
        str = intent.getStringExtra("view");
//        if (str != null) {
//            buttonSave.setVisibility(View.GONE);
//        }
        String minutes = student.getConsume_time();
        if (minutes == null) {
            minutes = "暂无时间";
        } else {
            minutes += "分钟";
        }
        tvDevicesValue.setText(minutes);
        tvNameValue.setText(student.getName());
        tvXuehaoValue.setText(student.getXuehao() + "");
        String result = student.getScores() == null ? "暂无成绩" : student.getScores() + "";
        tvScoresValue.setText(result);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(str)) {
            setResult(Activity.RESULT_OK, new Intent());
        }
        super.onBackPressed();
    }
}
