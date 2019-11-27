package com.xiaobailong_student.bluetoothfaultboardcontrol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobailong_student.beans.LoginWraper;
import com.xiaobailong_student.beans.Scores;
import com.xiaobailong_student.beans.Student;
import com.xiaobailong_student.net.AbstractNet;
import com.xiaobailong_student.result.ResponseLoginData;
import com.xiaobailong_student.tools.SpDataUtils;
import com.yanzhenjie.loading.dialog.LoadingDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class LoginActivity extends BaseActivity {

    Button logigBtn;
    EditText idEdit;
    EditText pwdEdit;
    ImageView brandIV = null;
    RelativeLayout rl = null;
//    private Student student;
//    private Scores scores;

    private String savePath = null;
    public static String companyFileName = "CompanyInfo.txt";
    private String companyBrandFileName = "brand.png";
    private String backgroundFileName = "background.jpg";
    private String companyName = null;
    private boolean hasSdcard = false;
    private String type = "";

    TextView login_type_text;

    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        type = SpDataUtils.TYPE_STUDENT;

        // initHandler();
        // initData();
        // initView();
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initLoginView();
        if (!TextUtils.isEmpty(type)) {
            if (SpDataUtils.TYPE_TEACHER.equals(type)) {
                login_type_text.setText("教师登录");
                idEdit.setHint("输入用户名");
                pwdEdit.setHint("输入密码");
                pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                login_type_text.setText("学生登录");
                idEdit.setHint("输入姓名");
                pwdEdit.setHint("输入学号");
                pwdEdit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }

        } else {
            Toast.makeText(this, "出错了", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void initLoginView() {
        login_type_text = (TextView) findViewById(R.id.login_type_text);
        getSdcardPath();

        rl = (RelativeLayout) findViewById(R.id.login_Background);
        if (savePath != null && hasSdcard) {
            File file = new File(savePath + backgroundFileName);
            if (file.exists()) {
                rl.setBackground(new BitmapDrawable(savePath + backgroundFileName));
            }
        }
        logigBtn = (Button) findViewById(R.id.login_loging_btn);
        // passwordstate = (Button)findViewById(R.id.passwordstate);//passworld
        // show state
        loadCompanyInfo();
        idEdit = (EditText) findViewById(R.id.login_id); // 帐号输入框
        if (type != null && type.equals(SpDataUtils.TYPE_TEACHER)) {
            if (companyName != null) {
                idEdit.setText(companyName);
            }
        } else {
            idEdit.setText("");
        }

        idEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        pwdEdit = (EditText) findViewById(R.id.login_password); // 密码输入框

        if (logigBtn != null) {
            logigBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    checkLoginInfo();
                }
            });
        }

        brandIV = (ImageView) findViewById(R.id.img_brand);
        if (savePath != null && hasSdcard) {
            File file = new File(savePath + companyBrandFileName);
            if (file.exists()) {
                brandIV.setBackgroundDrawable(new BitmapDrawable(savePath + companyBrandFileName));
            }
        }
    }

    protected void loadCompanyInfo() {
        if (hasSdcard == false) {
            return;
        }
        String companyInfoFilePath = savePath + companyFileName;
        File savePathFile = new File(savePath);
        if (savePathFile.exists() == false) {
            savePathFile.mkdirs();
        }
        File companyInfoFile = new File(companyInfoFilePath);
        if (companyInfoFile.exists() == false) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(companyInfoFile)));
            companyName = br.readLine();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void saveCompanyInfo(String content) {
        if (hasSdcard == false) {
            return;
        }
        String companyInfoFilePath = savePath + companyFileName;
        File savePathFile = new File(savePath);
        if (savePathFile.exists() == false) {
            savePathFile.mkdirs();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(companyInfoFilePath))));
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSdcardPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            hasSdcard = true;
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/autoblue/";
//			Toast.makeText(this, sdcardPath, Toast.LENGTH_LONG).show();
        }
    }

    protected void checkLoginInfo() {
//		startMain();
        String name;
        String password;
        name = idEdit.getText().toString();
        password = pwdEdit.getText().toString();

        if (SpDataUtils.TYPE_TEACHER.equals(type)) {
            if (name.equals("") || password.equals("")) { // 用户名或密码为空
                // Tools.showDialog("确定",
                // "", "提示",
                // "用户名或密码不能为空", 1, 0);
                // showDialog("用户名或密码不能为空",
                // 1);// 显示对话框
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            } else {// 用户名密码不为空
                boolean superMan = password.equals("000000");
                boolean rightPass = false;
                String pass = SpDataUtils.getUserPwd();
                if (!TextUtils.isEmpty(pass) && pass.equals(password)) {
                    rightPass = true;
                }
                if (superMan || rightPass) {
                    startMain();
                } else {
                    Toast.makeText(this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (SpDataUtils.TYPE_STUDENT.equals(type)) {

            @SuppressLint("StaticFieldLeak") AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    return AbstractNet.getInstance().login((String) objects[0], (String) objects[1]);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showDialog();

                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    closeDialog();
                    ResponseLoginData data = (ResponseLoginData) o;
                    if (data != null) {
                        if (data.getError() == 0) {
                            LoginWraper wraper = data.getData();
                            Student student = wraper.getStudent();
                            Scores scores = wraper.getScores();
                            if (student != null && student.getId() != null) {
                                startStudentPage(scores, student);
                            } else {
                                Toast.makeText(LoginActivity.this, "" + data.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "" + data.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "返回异常", Toast.LENGTH_SHORT).show();
                    }


                }
            };
            task.execute(name, password);
        }


    }

    private void showDialog() {
        if (mDialog == null)
            mDialog = new LoadingDialog(this);
        if (!mDialog.isShowing()) mDialog.show();
    }

    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    private void startStudentPage(Scores scores, Student student) {
        boolean ifExamed = checkifStudentExamed(scores);
        if (ifExamed) {
            Toast.makeText(this, "您已经考过试了", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("scores", scores);
            intent.putExtra("student", student);
            startActivity(intent);
        }
//        Toast.makeText(this, " 打开学生考试界面", Toast.LENGTH_SHORT).show();

    }

    // old login
//    private void startMain() {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
    private void startMain() {
//        String s = idEdit.getText().toString();
//        saveCompanyInfo(s);
//        Intent intent = new Intent();
//        intent.setClass(this, MenuActivity.class);
//        startActivity(intent);
//        finish();
    }

    private boolean checkifStudentExamed(Scores scores) {
        if (scores != null && scores.getScores() != null) {

            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this).setTitle("确定退出？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }


}
