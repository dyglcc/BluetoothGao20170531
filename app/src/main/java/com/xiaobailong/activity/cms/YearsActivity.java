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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong.activity.AddFileActivity;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bean.YearsDao;
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

public class YearsActivity extends BaseActivity {
    @BindView(R.id.grid)
    GridView grid;
    YearAdapter adapter;
    @BindView(R.id.parent)
    LinearLayout parent;
    GestureDetector dec = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_years);
        ButterKnife.bind(this);
        // get data

        getData();
        adapter = new YearAdapter();
        grid.setAdapter(adapter);
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                return false;
            }
        });
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
        Years y = yearses.get(position);
        if(y!=null){
            Intent intent  =new Intent(this,ClassActivity.class);
            intent.putExtra("year",y);
            startActivity(intent);
        }

    }

    private void longlick() {
        Intent intent = new Intent(YearsActivity.this, AddFileActivity.class);
        startActivityForResult(intent, 100);
    }


    private void deleteFileBypos(int position) {
        Years years = yearses.get(position);
        yd.delete(years);

        // delete file

        String dir = ConstValue.getDirStudent();
        String path = dir + "/" + years.getFilename();
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        // remove from db
        yearses.remove(years);
        adapter.notifyDataSetChanged();
    }

    private int editFilePos = -1;

    private void modifyFileName(int position) {
        Years years = yearses.get(position);
        String filename = years.getFilename();
        editFilePos = position;

        Intent intent = new Intent(this, AddFileActivity.class);
        // send intent;
        intent.putExtra("filename", filename);
        startActivityForResult(intent, 99);
    }

    List<Years> yearses;
    private YearsDao yd;

    private void getData() {

        yd = BaseApplication.app.daoSession.getYearsDao();
        yearses = yd.loadAll();

    }


    class YearAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return yearses == null ? 0 : yearses.size();
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
            Years years = null;
            YeadViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(YearsActivity.this).inflate(R.layout.item_grid_year, null);
                holder = new YeadViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
                convertView.setTag(holder);

            } else {
                holder = (YeadViewHolder) convertView.getTag();
            }
            years = yearses.get(position);
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
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    String filename = data.getStringExtra("filename");
                    if (TextUtils.isEmpty(filename)) {
                        Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        // ???????????????
                        String path = ConstValue.getDirStudent();
                        if (path == null) {
                            Toast.makeText(this, "?????????????????????sdcard", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // add file
                        File dirs = new File(path);
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }

                        File newFile = new File(dirs.getAbsolutePath() + "/" + filename);
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }
                        // add data base;
                        Years years = new Years();
                        years.setFilename(filename);
                        years.setPath(newFile.getAbsolutePath());
                        yd.insert(years);
                        List<Years> datas = yd.loadAll();
                        yearses.clear();
                        adapter.notifyDataSetChanged();
                        yearses.addAll(datas);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (requestCode == 99) {
            if (data != null) {
                String filename = data.getStringExtra("filename");
                if (TextUtils.isEmpty(filename)) {
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    // ???????????????
                    String path = ConstValue.getDirStudent();
                    if (path == null) {
                        Toast.makeText(this, "?????????????????????sdcard", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File dirs = new File(path);
                    if (!dirs.exists()) {
                        dirs.mkdirs();
                    }

                    Years years = yearses.get(editFilePos);
                    File editFile = new File(dirs.getAbsoluteFile() + "/" + filename);
                    File oldfile = new File(dirs.getAbsoluteFile() + "/" + years.getFilename());
                    oldfile.renameTo(editFile);


                    // add data base;
                    years.setFilename(filename);
                    years.setPath(editFile.getAbsolutePath());
                    yd.update(years);
                    List<Years> datas = yd.loadAll();
                    yearses.clear();
                    adapter.notifyDataSetChanged();
                    yearses.addAll(datas);
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
            int position = grid.pointToPosition((int)e.getRawX(),(int)e.getY());
            if(position!=-1){
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

            int position = grid.pointToPosition((int)e.getRawX(),(int)e.getY());
            if(position == -1){
                longlick();
            }else{
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
        item0.setText("??????");
        item0.setPosition(0);
        item0.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item0) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                modifyFileName(position);

            }
        });
        MenuItem item1 = new MenuItem();
        item1.setText("??????");
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
