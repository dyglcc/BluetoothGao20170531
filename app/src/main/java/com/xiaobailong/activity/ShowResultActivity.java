package com.xiaobailong.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Scores;
import com.xiaobailong.bean.ScoresDao;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dongyuangui on 2017/6/6.
 */

public class ShowResultActivity extends BaseActivity {

//    Scores scores = null;
    Button buttonSave;
    String str = "";
    String xuehao = "";
    String name = "";
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_value)
    TextView tvNameValue;
    @BindView(R.id.tv_xuehao)
    TextView tvXuehao;
    @BindView(R.id.tv_xuehao_value)
    TextView tvXuehaoValue;
    @BindView(R.id.list)
    ListView list;
    private List<Scores> data;
    DataAdatper adatper = null;

    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_show_result);
        ButterKnife.bind(this);
        Intent intent = getIntent();
//        scores = (Scores) intent.getSerializableExtra("scores");
        str = intent.getStringExtra("view");
        xuehao = intent.getStringExtra("xuehao");
        name = intent.getStringExtra("name");

        inflater = LayoutInflater.from(this);
//        if (str != null) {
//            buttonSave.setVisibility(View.GONE);
//        }
        // 这里显示一个列表。。


        initData();
        tvNameValue.setText(name);
        tvXuehaoValue.setText(xuehao);
//        buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    private void initData() {
        //查看学号下面所有考试
        if (str != null) {
            data = BaseApplication.app.daoSession.getScoresDao()
                    .queryBuilder().where(ScoresDao.Properties.Xuehao.eq(xuehao)
                            , ScoresDao.Properties.Name.eq(name)).list();
        } else {// 查看当次考试
            data = BaseApplication.app.daoSession.getScoresDao()
                    .queryBuilder().where(ScoresDao.Properties.Xuehao.eq(xuehao)
                            , ScoresDao.Properties.Name.eq(name)).limit(1).list();
        }
        adatper = new DataAdatper(data,ShowResultActivity.this);
        list.setAdapter(adatper);

    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(str)) {
            setResult(Activity.RESULT_OK, new Intent());
        }
        super.onBackPressed();
    }

    private static class DataAdatper extends BaseAdapter {

        List<Scores> data = null;
        LayoutInflater inflater = null;

        public DataAdatper(List<Scores> data,Context context) {
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_item_result, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Scores scores = data.get(position);
            String minutes = scores.getConsume_time();
            if (minutes == null) {
                minutes = "暂无时间";
            } else {
                minutes += "分钟";
            }
            holder.tvConsumeValue.setText(minutes);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            holder.tvDatestr.setText("考试日期：" + format.format(scores.getDate_()));
            holder.tvDeviceValue.setText(scores.getDevices());
            String result = scores.getScores() == null ? "暂无成绩" : scores.getScores() + "";
            holder.tvScoresValue.setText(result);
            return convertView;
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_datestr)
        TextView tvDatestr;
        @BindView(R.id.tv_device)
        TextView tvDevice;
        @BindView(R.id.tv_device_value)
        TextView tvDeviceValue;
        @BindView(R.id.tv_consume)
        TextView tvConsume;
        @BindView(R.id.tv_consume_value)
        TextView tvConsumeValue;
        @BindView(R.id.tv_scores)
        TextView tvScores;
        @BindView(R.id.tv_scores_value)
        TextView tvScoresValue;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
