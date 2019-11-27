package com.xiaobailong.activity.cms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong.activity.AddFileActivity;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.Devices;
import com.xiaobailong.bean.DevicesDao;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.tools.ConstValue;
import com.xiaobailong.widget.BottomMenuFragment;
import com.xiaobailong.widget.MenuItem;
import com.xiaobailong.widget.MenuItemOnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dongyuangui on 2017/6/1.
 */

/**
 * Created by dongyuangui on 2017/6/1.
 */

public class DevicesActity extends BaseActivity {
    @BindView(R.id.grid)
    GridView grid;
    YearAdapter adapter;
    @BindView(R.id.parent)
    LinearLayout parent;
    GestureDetector dec = null;
    Classes classes;
    Years years;
    DevicesDao devicesDao;

    private static String TAG = "DevicesActity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_devices);
        ButterKnife.bind(this);
        // get data
        classes = (Classes) getIntent().getSerializableExtra("classes");
        years = (Years) getIntent().getSerializableExtra("years");
        getData();
        adapter = new YearAdapter();
        grid.setAdapter(adapter);

        dec = new GestureDetector(this, listener);
        grid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dec.onTouchEvent(event);
                return false;
            }
        });

        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longlick();
                return false;
            }
        });
    }

    private void itemClick(int position) {
        Devices y = deviceses.get(position);
        if (y != null) {
            Intent intent = new Intent(this, TimeActivity.class);
            intent.putExtra("devices", y);
            startActivity(intent);
        }

    }

    private void longlick() {
        Intent intent = new Intent(this, AddFileActivity.class);
        startActivityForResult(intent, 100);
    }


    private void deleteFileBypos(int position) {
        Devices Classes = deviceses.get(position);
        devicesDao.delete(Classes);

        // delete file

        String dir = ConstValue.getDirStudent();
        String path = dir + "/" + Classes.getFilename();
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        // remove from db
        deviceses.remove(Classes);
        adapter.notifyDataSetChanged();
    }

    private int editFilePos = -1;

    private void modifyFileName(int position) {
        Devices Classes = deviceses.get(position);
        String filename = Classes.getFilename();
        editFilePos = position;

        Intent intent = new Intent(this, AddFileActivity.class);
        // send intent;
        intent.putExtra("filename", filename);
        startActivityForResult(intent, 99);
    }

    List<Devices> deviceses;

    private void getData() {

        devicesDao = BaseApplication.app.daoSession.getDevicesDao();
        deviceses = devicesDao.queryBuilder().where(DevicesDao.Properties.Parent.eq(classes.getId())).list();
        Log.i(TAG, "getData:  devices");

    }


    class YearAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return deviceses == null ? 0 : deviceses.size();
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
            Devices devices = null;
            YeadViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(DevicesActity.this).inflate(R.layout.item_grid_year, null);
                holder = new YeadViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
                convertView.setTag(holder);

            } else {
                holder = (YeadViewHolder) convertView.getTag();
            }
            devices = deviceses.get(position);
            holder.fileName.setText(devices.getFilename());
            return convertView;
        }
    }

    final class YeadViewHolder {
        TextView fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 添加文件
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    String filename = data.getStringExtra("filename");
                    if (TextUtils.isEmpty(filename)) {
                        Toast.makeText(this, "添加文件出错了", Toast.LENGTH_SHORT).show();
                    } else {
                        // 创建文件夹
                        String path = ConstValue.getDirStudent();
                        if (path == null) {
                            Toast.makeText(this, "请检查是否安装sdcard", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // add file
                        File dirs = new File(classes.getPath());
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }

                        File newFile = new File(dirs.getAbsolutePath() + "/" + filename);
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }
                        // add data base;
                        Devices devices = new Devices();
                        devices.setFilename(filename);
                        devices.setParent(classes.getId());
                        devices.setPath(newFile.getAbsolutePath());
                        devicesDao.insert(devices);
                        List<Devices> datas = devicesDao.queryBuilder().where(DevicesDao.Properties.Parent.eq(classes.getId())).list();
                        deviceses.clear();
                        adapter.notifyDataSetChanged();
                        deviceses.addAll(datas);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (requestCode == 99) {
            if (data != null) {
                String filename = data.getStringExtra("filename");
                if (TextUtils.isEmpty(filename)) {
                    Toast.makeText(this, "修改文件出错了", Toast.LENGTH_SHORT).show();
                } else {
                    // 创建文件夹
                    String path = ConstValue.getDirStudent();
                    if (path == null) {
                        Toast.makeText(this, "请检查是否安装sdcard", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File dirs = new File(path + "/" + classes.getFilename());
                    if (!dirs.exists()) {
                        dirs.mkdirs();
                    }

                    Devices devices = deviceses.get(editFilePos);
                    File editFile = new File(dirs.getAbsoluteFile() + "/" + filename);
                    File oldfile = new File(dirs.getAbsoluteFile() + "/" + devices.getFilename());
                    oldfile.renameTo(editFile);


                    // add data base;
                    devices.setFilename(filename);
                    devices.setPath(editFile.getAbsolutePath());
                    devicesDao.update(devices);
                    List<Devices> datas = devicesDao.queryBuilder().where(DevicesDao.Properties.Parent.eq(classes.getId())).list();
                    deviceses.clear();
                    adapter.notifyDataSetChanged();
                    deviceses.addAll(datas);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = grid.pointToPosition((int) e.getRawX(), (int) e.getY());
            if (position != -1) {
                itemClick(position);
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int position = grid.pointToPosition((int) e.getRawX(), (int) e.getY());
            if (position == -1) {
                longlick();
            } else {
                itemLongclick(position);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    private void itemLongclick(final int position) {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem item0 = new MenuItem();
        item0.setText("修改");
        item0.setPosition(0);
        item0.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item0) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                modifyFileName(position);

            }
        });
        MenuItem item1 = new MenuItem();
        item1.setText("删除");
        item1.setPosition(1);
        item1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                deleteFileBypos(position);
            }
        });
        menuItemList.add(item0);
        menuItemList.add(item1);

        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

}
