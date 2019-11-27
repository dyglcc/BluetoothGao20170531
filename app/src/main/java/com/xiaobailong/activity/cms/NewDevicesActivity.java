package com.xiaobailong.activity.cms;

import android.content.Intent;
import android.os.Bundle;
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
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.LoginActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.tools.ConstValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by dongyuangui on 2017/6/1.
 * des:新的设备代码，之前有个旧的设备代码
 */

public class NewDevicesActivity extends BaseActivity {
    @BindView(R.id.grid)
    GridView grid;
    DevicesAdapter adapter;
    @BindView(R.id.parent)
    LinearLayout parent;
    GestureDetector dec = null;

    Classes classes;
    Years years;

//    DevicesDao devicesDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_devices);
        ButterKnife.bind(this);
        // get data

        classes = (Classes) getIntent().getSerializableExtra("classes");
        years = (Years) getIntent().getSerializableExtra("years");
        getData();
        adapter = new DevicesAdapter();
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewDevicesActivity.this, ScoresActivity.class);
                intent.putExtra("classes", classes);
                intent.putExtra("years", years);
                intent.putExtra("device", deviceses.get(position));
                startActivity(intent);
                Toast.makeText(NewDevicesActivity.this, "scoresActivity", Toast.LENGTH_SHORT).show();
            }
        });

//        dec = new GestureDetector(this, listener);
//        grid.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                dec.onTouchEvent(event);
//                return false;
//            }
//        });
//
//        parent.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                longlick();
//                return false;
//            }
//        });
    }

    private void itemClick(int position) {
//        Devices y = deviceses.get(position);
//        if (y != null) {
//            Intent intent = new Intent(this, TimeActivity.class);
//            intent.putExtra("devices", y);
//            startActivity(intent);
//        }

    }

    private void longlick() {
        Intent intent = new Intent(this, AddFileActivity.class);
        startActivityForResult(intent, 100);
    }


//    private void deleteFileBypos(int position) {
//        Devices Classes = deviceses.get(position);
//        devicesDao.delete(Classes);
//
//        // delete file
//
//        String dir = ConstValue.getDirStudent();
//        String path = dir + "/" + Classes.getFilename();
//        File file = new File(path);
//        if (file.exists()) {
//            file.delete();
//        }
//        // remove from db
//        deviceses.remove(Classes);
//        adapter.notifyDataSetChanged();
//    }

    private int editFilePos = -1;

//    private void modifyFileName(int position) {
//        Devices Classes = deviceses.get(position);
//        String filename = Classes.getFilename();
//        editFilePos = position;
//
//        Intent intent = new Intent(this, AddFileActivity.class);
//        // send intent;
//        intent.putExtra("filename", filename);
//        startActivityForResult(intent, 99);
//    }

    List<String> deviceses;

    private void getData() {

        deviceses = new ArrayList<>();
//        devicesDao = BaseApplication.app.daoSession.getDevicesDao();
//        deviceses = devicesDao.queryBuilder().where(DevicesDao.Properties.Parent.eq(classes.getId())).list();
        String devicesPath = ConstValue.getFailureDir();
        File filePath = new File(devicesPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        if (filePath.isFile()) {
            return;
        }
        for (File file : filePath.listFiles()) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                continue;
            }
            if (file.getName() == null) {
                continue;
            }
            if (file.getName().equals(LoginActivity.companyFileName)) {
                continue;
            }
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String[] names = file.getName().split("\\.");
                if (names.length > 0) {
                    deviceses.add(names[0]);
                }
            }
        }

    }


    class DevicesAdapter extends BaseAdapter {

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
            YeadViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(NewDevicesActivity.this).inflate(R.layout.item_grid_year, null);
                holder = new YeadViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
                convertView.setTag(holder);

            } else {
                holder = (YeadViewHolder) convertView.getTag();
            }
            holder.fileName.setText(deviceses.get(position));
            return convertView;
        }
    }

    static final  class YeadViewHolder {
        TextView fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        // 添加文件
//        if (requestCode == 100) {
//            if (resultCode == Activity.RESULT_OK) {
//
//                if (data != null) {
//                    String filename = data.getStringExtra("filename");
//                    if (TextUtils.isEmpty(filename)) {
//                        Toast.makeText(this, "添加文件出错了", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // 创建文件夹
//                        String path = ConstValue.getDirStudent();
//                        if (path == null) {
//                            Toast.makeText(this, "请检查是否安装sdcard", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        // add file
//                        File dirs = new File(classes.getPath());
//                        if (!dirs.exists()) {
//                            dirs.mkdirs();
//                        }
//
//                        File newFile = new File(dirs.getAbsolutePath() + "/" + filename);
//                        if (!newFile.exists()) {
//                            newFile.mkdir();
//                        }
//                        // add data base;
//                        Devices devices = new Devices();
//                        devices.setFilename(filename);
//                        devices.setParent(classes.getId());
//                        devices.setPath(newFile.getAbsolutePath());
//                        devicesDao.insert(devices);
//                        List<Devices> datas = devicesDao.queryBuilder().where(DevicesDao.Properties.Parent.eq(classes.getId())).list();
//                        deviceses.clear();
//                        adapter.notifyDataSetChanged();
//                        deviceses.addAll(datas);
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        } else if (requestCode == 99) {
//            if (data != null) {
//                String filename = data.getStringExtra("filename");
//                if (TextUtils.isEmpty(filename)) {
//                    Toast.makeText(this, "修改文件出错了", Toast.LENGTH_SHORT).show();
//                } else {
//                    // 创建文件夹
//                    String path = ConstValue.getDirStudent();
//                    if (path == null) {
//                        Toast.makeText(this, "请检查是否安装sdcard", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    File dirs = new File(path + "/" + classes.getFilename());
//                    if (!dirs.exists()) {
//                        dirs.mkdirs();
//                    }
//
//                    Devices devices = deviceses.get(editFilePos);
//                    File editFile = new File(dirs.getAbsoluteFile() + "/" + filename);
//                    File oldfile = new File(dirs.getAbsoluteFile() + "/" + devices.getFilename());
//                    oldfile.renameTo(editFile);
//
//
//                    // add data base;
//                    devices.setFilename(filename);
//                    devices.setPath(editFile.getAbsolutePath());
//                    devicesDao.update(devices);
//                    List<Devices> datas = devicesDao.queryBuilder().where(DevicesDao.Properties.Parent.eq(classes.getId())).list();
//                    deviceses.clear();
//                    adapter.notifyDataSetChanged();
//                    deviceses.addAll(datas);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        }
    }

//    private GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return false;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent e) {
//
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            int position = grid.pointToPosition((int) e.getRawX(), (int) e.getY());
//            if (position != -1) {
//                itemClick(position);
//            }
//            return false;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return false;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            int position = grid.pointToPosition((int) e.getRawX(), (int) e.getY());
//            if (position == -1) {
//                longlick();
//            } else {
//                itemLongclick(position);
//            }
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            return false;
//        }
//    };
//
//    private void itemLongclick(final int position) {
//        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
//        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
//        MenuItem item0 = new MenuItem();
//        item0.setText("修改");
//        item0.setPosition(0);
//        item0.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item0) {
//            @Override
//            public void onClickMenuItem(View v, MenuItem menuItem) {
//
//                modifyFileName(position);
//
//            }
//        });
//        MenuItem item1 = new MenuItem();
//        item1.setText("删除");
//        item1.setPosition(1);
//        item1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item1) {
//            @Override
//            public void onClickMenuItem(View v, MenuItem menuItem) {
//                deleteFileBypos(position);
//            }
//        });
//        menuItemList.add(item0);
//        menuItemList.add(item1);
//
//        bottomMenuFragment.setMenuItems(menuItemList);
//        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
//    }

}
