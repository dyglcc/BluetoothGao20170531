package com.xiaobailong.activity.cms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong.activity.AddFileActivity;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.Scores;
import com.xiaobailong.bean.ScoresDao;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.widget.BottomMenuFragment;
import com.xiaobailong.widget.MenuItem;
import com.xiaobailong.widget.MenuItemOnClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dongyuangui on 2017/6/1.
 */

/**
 * Created by dongyuangui on 2017/6/1.
 */

public class ScoresActivity extends BaseActivity {
    @BindView(R.id.grid)
    GridView grid;
    ScoresAdapter adapter;
    @BindView(R.id.parent)
    LinearLayout parent;
    GestureDetector dec = null;

    Classes classes;
    Years years;
    ScoresDao dao;
    private String deviceName;

    private static final String TAG = "ScoresActivity";
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_scores);
        ButterKnife.bind(this);
        // get data

        classes = (Classes) getIntent().getSerializableExtra("classes");
        years = (Years) getIntent().getSerializableExtra("years");
        deviceName = getIntent().getStringExtra("device");

        getData();
        adapter = new ScoresAdapter();
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dateStr = groups.get(position);
                Intent intent = new Intent(ScoresActivity.this, ScoresContentActivity.class);
                intent.putExtra("classes", classes);
                intent.putExtra("dateStr", dateStr);
                intent.putExtra("devices", deviceName);
                intent.putExtra("years", years);
                startActivity(intent);
            }
        });
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(ScoresActivity.this, "长按" + position, Toast.LENGTH_SHORT).show();

                BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
                List<MenuItem> menuItemList = new ArrayList<MenuItem>();
                MenuItem item0 = new MenuItem();
                item0.setText("删除");
                item0.setPosition(0);
                item0.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item0) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {

                        // 学生学期信息删除
                        String dateStr = groups.get(position);
                        long baseTime = 0;
                        try {
                            baseTime = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (baseTime == 0) {
                            Toast.makeText(ScoresActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < times.size(); i++) {
                            Scores scores = times.get(i);
                            if (scores == null) {
                                continue;
                            }

                            long strTime = scores.getDate_();
                            if (strTime == 0) {
                                continue;
                            }

                            if (strTime >= baseTime && baseTime < (baseTime + 3600 * 24 * 1000)) {
                                dao.delete(scores);
                            }

                        }
                        // 初始化数据
                        getData();
                        adapter.notifyDataSetChanged();

                    }
                });
                menuItemList.add(item0);

                bottomMenuFragment.setMenuItems(menuItemList);
                bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");


                return true;
            }
        });

    }

    private void longlick() {
        Intent intent = new Intent(this, AddFileActivity.class);
        startActivityForResult(intent, 100);
    }


    private int editFilePos = -1;

    // 设备下面不同日期的考试
    List<Scores> times;
    List<String> groups;

    private void getData() {
        times = new ArrayList<>();
        groups = new ArrayList<>();
        dao = BaseApplication.app.daoSession.getScoresDao();


        times = dao.queryBuilder().where(ScoresDao.Properties.Devices.eq(deviceName), ScoresDao.Properties.Class_.eq(classes.getId()))
                .orderDesc(ScoresDao.Properties.Date_).build().list();

        Log.i(TAG, "getData: 日期的 sizes " + (times == null ? 0 : times.size()));

        HashMap<String, Scores> map = new HashMap<>();
        for (int i = 0; i < times.size(); i++) {
            Scores stu = times.get(i);
            if (stu.getDate_() == null || stu.getDate_() == 0) {
                continue;
            }
            Long value = stu.getDate_();
            String dateStr = format.format(value);

            if (!map.containsKey(dateStr)) {
                map.put(dateStr, stu);
            }
        }
        for (Map.Entry<String, Scores> entry : map.entrySet()) {
            String key = entry.getKey();
            groups.add(key);
        }
    }


    class ScoresAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return groups == null ? 0 : groups.size();
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
            YeadViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(ScoresActivity.this).inflate(R.layout.item_grid_scores, null);
                holder = new YeadViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
                convertView.setTag(holder);

            } else {
                holder = (YeadViewHolder) convertView.getTag();
            }

            holder.fileName.setText(groups.get(position));
            return convertView;
        }
    }

    final class YeadViewHolder {
        TextView fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
