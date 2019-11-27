package com.xiaobailong.activity.cms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.xiaobailong.activity.StudentManageActivity;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.ClassesDao;
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

public class ClassActivity extends BaseActivity {
    @BindView(R.id.classes)
    GridView classes;
    YearAdapter adapter;
    @BindView(R.id.parent)
    LinearLayout parent;
    GestureDetector dec = null;

    Years years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_class);
        ButterKnife.bind(this);
        // get data

        years = (Years) getIntent().getSerializableExtra("year");

        getData();
        adapter = new YearAdapter();
        classes.setAdapter(adapter);

        dec = new GestureDetector(this, listener);
        classes.setOnTouchListener(new View.OnTouchListener() {
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
//        Classes y = datas.get(position);
//        if (y != null) {
//            Intent intent = new Intent(this, DevicesActity.class);
//            intent.putExtra("classes", y);
//            startActivity(intent);
//        }
        Classes y = datas.get(position);
        if (y != null) {
            Intent intent = new Intent(this, StudentManageActivity.class);
            intent.putExtra("classes", y);
            intent.putExtra("years", years);
            startActivity(intent);
        }


    }

    private void longlick() {
        Intent intent = new Intent(this, AddFileActivity.class);
        startActivityForResult(intent, 100);
    }


    private void deleteFileBypos(int position) {
        Classes years = datas.get(position);
        dao.delete(years);

        // delete file

        String dir = ConstValue.getDirStudent();
        String path = dir + "/" + years.getFilename();
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        // remove from db
        datas.remove(years);
        adapter.notifyDataSetChanged();
    }

    private int editFilePos = -1;

    private void modifyFileName(int position) {
        Classes years = datas.get(position);
        String filename = years.getFilename();
        editFilePos = position;

        Intent intent = new Intent(this, AddFileActivity.class);
        // send intent;
        intent.putExtra("filename", filename);
        startActivityForResult(intent, 99);
    }

    List<Classes> datas;
    private ClassesDao dao;

    private void getData() {

        dao = BaseApplication.app.daoSession.getClassesDao();
        datas = dao.queryBuilder().where(ClassesDao.Properties.Parent.eq(years.getId())).list();

    }


    class YearAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
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
            Classes years = null;
            YeadViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(ClassActivity.this).inflate(R.layout.item_grid_year, null);
                holder = new YeadViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
                convertView.setTag(holder);

            } else {
                holder = (YeadViewHolder) convertView.getTag();
            }
            years = datas.get(position);
            holder.fileName.setText(years.getFilename());
            return convertView;
        }
    }

    final class YeadViewHolder {
        TextView fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // add file
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
                        File dirs = new File(path + "/" + years.getFilename());
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }

                        File newFile = new File(dirs.getAbsolutePath() + "/" + filename);
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }
                        // add data base;
                        Classes class_ = new Classes();
                        class_.setFilename(filename);
                        class_.setParent(years.getId());
                        class_.setPath(newFile.getAbsolutePath());
                        dao.insert(class_);
                        List<Classes> datas = dao.queryBuilder().where(ClassesDao.Properties.Parent.eq(years.getId())).list();
                        this.datas.clear();
                        adapter.notifyDataSetChanged();
                        this.datas.addAll(datas);
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

                    File dirs = new File(path + "/" + years.getFilename());
                    if (!dirs.exists()) {
                        dirs.mkdirs();
                    }

                    Classes classes1 = datas.get(editFilePos);
                    File editFile = new File(dirs.getAbsoluteFile() + "/" + filename);
                    File oldfile = new File(dirs.getAbsoluteFile() + "/" + classes1.getFilename());
                    oldfile.renameTo(editFile);


                    // add data base;
                    classes1.setFilename(filename);
                    classes1.setPath(editFile.getAbsolutePath());
                    dao.update(classes1);
                    List<Classes> datas = dao.queryBuilder().where(ClassesDao.Properties.Parent.eq(years.getId())).list();
                    this.datas.clear();
                    adapter.notifyDataSetChanged();
                    this.datas.addAll(datas);
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
            int position = classes.pointToPosition((int) e.getRawX(), (int) e.getY());
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

            int position = classes.pointToPosition((int) e.getRawX(), (int) e.getY());
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

