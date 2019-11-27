package com.xiaobailong.activity.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.Scores;
import com.xiaobailong.bean.ScoresDao;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bean.YearsDao;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.tools.ExcelUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by dongyuangui on 2017/6/2.
 */

public class ScoresContentActivity extends BaseActivity {
    //implements FileSelectFragment.FileSelectCallbacks

    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.btn_export)
    Button btnExport;
    @BindView(R.id.years)
    TextView years;
    @BindView(R.id.classes)
    TextView classes_text;
    @BindView(R.id.devices)
    TextView devices;
    @BindView(R.id.datestr)
    TextView datestr;
    @BindView(R.id.xuehao)
    TextView xuehao;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.tv_scores)
    TextView tvScores;
    @BindView(R.id.tv_consume)
    TextView tvConsume;
    private Classes classes_;
    private Years years_;
    private List<Scores> datalist;
    //    private List<Student> importList;
    private ScoresDao dao;

    private LayoutInflater inflater = null;

    private ScoresAdapter adapter = null;
    private String dateStr, deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_scores_content);
        ButterKnife.bind(this);
        classes_ = (Classes) getIntent().getSerializableExtra("classes");
        dateStr = getIntent().getStringExtra("dateStr");
        deviceName = getIntent().getStringExtra("devices");
        if (classes_ == null) {
            return;
        }
        dao = BaseApplication.app.daoSession.getScoresDao();
        inflater = LayoutInflater.from(this);
        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        years_ = BaseApplication.app.daoSession.getYearsDao().queryBuilder()
                .where(YearsDao.Properties.Id.eq(classes_.getParent())).uniqueOrThrow();

        years.setText(years_.getFilename());
        devices.setText(deviceName);
        classes_text.setText(classes_.getFilename());
        datestr.setText(dateStr);

    }

    private void getData() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date base = format.parse(dateStr);

        long timeStamp1 = base.getTime();
        datalist = dao.queryBuilder()
                .where(ScoresDao.Properties.Class_.eq(classes_.getId()),
                        ScoresDao.Properties.Date_.ge(timeStamp1), ScoresDao.Properties.Date_.lt(timeStamp1 + 3600 * 24 * 1000), ScoresDao.Properties.Devices.eq(deviceName))
                .list();
        adapter = new ScoresAdapter();
        list.setAdapter(adapter);
    }


    @OnClick(R.id.btn_export)
    public void export() {
        String title = years_.getFilename() + "_" + classes_.getFilename() + "_" + deviceName + "_" + dateStr;
        String fileName = classes_.getPath() + "/" + years_.getFilename() + "_" + classes_.getFilename() + "_" + deviceName + "_" + dateStr + ".xls";
        try {
            List<List<Object>> strs = new ArrayList<>();
            for (int i = 0; i < datalist.size(); i++) {
                Scores scores = datalist.get(i);
                ArrayList<Object> list = new ArrayList<>();
//            学号、姓名、
                String minutes = scores.getConsume_time();
                if (minutes == null) {
                    minutes = "暂无时间";
                } else {
                    minutes += "分钟";
                }
                String scores_ = scores.getScores() == null ? "暂无成绩" : scores.getScores() + "";
                list.add(scores.getXuehao());
                list.add(scores.getName());
                list.add(scores_);
                list.add(minutes);
                strs.add(list);
            }
            String[] column = new String[4];
            column[0] = "学号";
            column[1] = "名字";
            column[2] = "成绩";
            column[3] = "考试用时";
            ExcelUtil.writeExcelWithTitleColumnAndMergeTitleCell(fileName, strs, column, title, "成绩表");
            Toast.makeText(this, "导出excel完成，保存路径是：" + fileName.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ScoresAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datalist == null ? 0 : datalist.size();
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
                convertView = inflater.inflate(R.layout.item_scores, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Scores scores = datalist.get(position);

            holder.xuehao.setText(scores.getXuehao() + "");
            holder.username.setText(scores.getName());
            holder.tvScores.setText(scores.getScores() + "");
            holder.tvConsume.setText(scores.getConsume_time());
            return convertView;
        }


    }

    static class ViewHolder {
        @BindView(R.id.xuehao)
        TextView xuehao;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.tv_scores)
        TextView tvScores;
        @BindView(R.id.tv_consume)
        TextView tvConsume;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
