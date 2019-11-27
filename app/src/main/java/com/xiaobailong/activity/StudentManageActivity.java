package com.xiaobailong.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong.activity.cms.NewDevicesActivity;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Classes;
import com.xiaobailong.bean.Student;
import com.xiaobailong.bean.StudentDao;
import com.xiaobailong.bean.Years;
import com.xiaobailong.bluetoothfaultboardcontrol.BaseActivity;
import com.xiaobailong.bluetoothfaultboardcontrol.R;
import com.xiaobailong.tools.ConstValue;
import com.xiaobailong.tools.ExcelUtil;
import com.xiaobailong.tools.FileSelectFragment;
import com.xiaobailong.widget.BottomMenuFragment;
import com.xiaobailong.widget.MenuItem;
import com.xiaobailong.widget.MenuItemOnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by dongyuangui on 2017/6/2.
 */

public class StudentManageActivity extends BaseActivity implements FileSelectFragment.FileSelectCallbacks {
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_view_result)
    Button btnViewResult;
    @BindView(R.id.ser_id)
    TextView serId;
    @BindView(R.id.xuehao)
    TextView xuehao;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.ids)
    TextView ids;
    @BindView(R.id.btn_export)
    Button btnExport;
    @BindView(R.id.btn_import)
    Button btnImport;

    private Classes classes;
    private Years years;
    private List<Student> datalist;
    private List<Student> importList;
    private StudentDao dao;

    private LayoutInflater inflater = null;

    private StudentAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_student_manage);
        ButterKnife.bind(this);
        classes = (Classes) getIntent().getSerializableExtra("classes");
        years = (Years) getIntent().getSerializableExtra("years");
        if (classes == null) {
            return;
        }
        dao = BaseApplication.app.daoSession.getStudentDao();
        inflater = LayoutInflater.from(this);
        getData();

    }

    private void getData() {
        datalist = dao.queryBuilder().where(StudentDao.Properties.Classes.eq(classes.getId())).list();
        adapter = new StudentAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Student student = datalist.get(position);
                BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
                List<MenuItem> menuItemList = new ArrayList<MenuItem>();
                MenuItem item0 = new MenuItem();
                item0.setText("删除学生");
                item0.setPosition(0);
                item0.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item0) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {

                        delStudent(student);

                    }
                });
                MenuItem item1 = new MenuItem();
                item1.setText("删除学生信息");
                item1.setPosition(1);
                item1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item1) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {
                        delStudent(student);
                    }
                });
                MenuItem item3 = new MenuItem();
                item3.setText("查看学生成绩");
                item3.setPosition(3);
                item3.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item1) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {

                        Intent intent = new Intent(StudentManageActivity.this, ShowResultActivity.class);
                        intent.putExtra("name", student.getUsername());
                        intent.putExtra("xuehao", student.getXuehao());
                        intent.putExtra("view", "view");
                        startActivity(intent);
                    }
                });
                menuItemList.add(item0);
                menuItemList.add(item1);
                menuItemList.add(item3);

                bottomMenuFragment.setMenuItems(menuItemList);
                bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
            }
        });
    }

    private void delStudent(Student student) {
        dao.delete(student);
        datalist = dao.queryBuilder().where(StudentDao.Properties.Classes.eq(classes.getId())).list();
        adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.btn_add)
    public void add() {
        Intent intent = new Intent(this, AddStudentActivity.class);
        intent.putExtra("classes", classes);
        intent.putExtra("years", years);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.btn_view_result)
    public void viewResult() {
        Intent intent = new Intent(this, NewDevicesActivity.class);
        intent.putExtra("classes", classes);
        intent.putExtra("years", years);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.btn_import)
    public void importStudent() {
        FileSelectFragment.show(this, new String[]{"xls", "xlsx"}, null, new File(ConstValue.getDirStudent()));
    }

    @OnClick(R.id.btn_export)
    public void export() {
        String fileName = classes.getPath() + "/" + classes.getFilename() + ".xls";

        try {
            List<Student> students = dao.queryBuilder()
                    .where(StudentDao.Properties.Classes
                            .eq(classes.getId())).orderAsc(StudentDao.Properties.Id).list();
            List<List<Object>> strs = new ArrayList<>();
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                ArrayList<Object> list = new ArrayList<>();
//            顺序，学号、姓名、性别、联系电话、身份证 成绩
                list.add(student.getXuehao());
                list.add(student.getUsername());
                list.add(student.getSex());
                list.add(student.getMobile());
                list.add(student.getIds());
//                list.add(student.getResults() == null ? "" : student.getResults());
                strs.add(list);
            }
            String[] titles = new String[5];
            titles[0] = "学号";
            titles[1] = "姓名";
            titles[2] = "性别";
            titles[3] = "联系电话";
            titles[4] = "身份证";
            ExcelUtil.writeExcelWithTitleColumn(fileName, strs, titles, "学生信息表");
            Toast.makeText(this, "导出excel完成，保存路径是：" + fileName.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    boolean succ = data.getBooleanExtra("added", false);
                    if (succ) {
                        datalist.clear();
                        adapter.notifyDataSetChanged();
                        datalist = dao.queryBuilder().where(StudentDao.Properties.Classes.eq(classes.getId())).list();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        try {
            String fullPath = String.format("%s/%s", absolutePath, fileName);
            List<List<Object>> data_list = ExcelUtil.read(fullPath);
            importList = new ArrayList<>();
            for (int i = 0; i < data_list.size(); i++) {
                Student item = new Student();
                List<Object> data_item = data_list.get(i);
                for (int j = 0; j < data_item.size(); j++) {
                    Object obj = data_item.get(j);
                    Log.d(TAG, "i=" + i + ",j=" + j + ",value=" + (String) obj);
                    if (j == 0) {
                        item.setXuehao((String) obj);
                    } else if (j == 1) {
                        String str = ((String) obj).trim();
                        item.setUsername(str);
                    } else if (j == 2) {
                        item.setSex(((String) obj).trim());
                    } else if (j == 3) {
                        item.setMobile(((String) obj).trim());
                    } else if (j == 4) {
                        item.setIds(((String) obj).trim());
                    } else if (j == 5) {
//                        String str = (String) obj;
//                        if ((String) obj != null && !((String) obj).equals("null")) {
//                            item.setResults(Integer.parseInt(str));
//                        }
                    }
                    item.setClasses(classes.getId());
                    item.setYear_(years.getId());
                }
                importList.add(item);
            }
        } catch (Exception e) {
            Toast.makeText(this, "导入excel出错", Toast.LENGTH_SHORT).show();
        }

        try {
            dao.saveInTx(importList);
        } catch (Exception e) {
            Toast.makeText(this, "保存数据库失败", Toast.LENGTH_SHORT).show();
        }

        // 重新加载数据
        datalist = dao.queryBuilder().where(StudentDao.Properties.Classes.eq(classes.getId())).list();
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "已导入excel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    private class StudentAdapter extends BaseAdapter {

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
                convertView = inflater.inflate(R.layout.item_student, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Student student = datalist.get(position);
            holder.serId.setText(position + 1 + "");
            holder.xuehao.setText(student.getXuehao() + "");
            holder.username.setText(student.getUsername());
            holder.sex.setText(student.getSex());
            holder.ids.setText(student.getIds() + "");
            holder.mobile.setText(student.getMobile());
            return convertView;
        }

    }

    class ViewHolder {
        @BindView(R.id.ser_id)
        TextView serId;
        @BindView(R.id.xuehao)
        TextView xuehao;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.sex)
        TextView sex;
        @BindView(R.id.ids)
        TextView ids;
        @BindView(R.id.mobile)
        TextView mobile;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
