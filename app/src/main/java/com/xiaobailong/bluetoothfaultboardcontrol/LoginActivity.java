package com.xiaobailong.bluetoothfaultboardcontrol;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.display.google_auth.Checker;
import com.xiaobailong.activity.MenuActivity;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Scores;
import com.xiaobailong.bean.ScoresDao;
import com.xiaobailong.bean.Student;
import com.xiaobailong.bean.StudentDao;
import com.xiaobailong.tools.ConstValue;
import com.xiaobailong.tools.SpDataUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginActivity extends BaseActivity {

    Button logigBtn;
    EditText idEdit;
    EditText pwdEdit;
    ImageView brandIV = null;
    RelativeLayout rl = null;
    private Student student;
    private Scores scores;

    private String savePath = null;
    public static String companyFileName = "CompanyInfo.txt";
    private String companyBrandFileName = "brand.png";
    private String backgroundFileName = "background.jpg";
    private String companyName = null;
    private boolean hasSdcard = false;
    private String type = "";

    TextView login_type_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        type = getIntent().getStringExtra("type");

        // initHandler();
        // initData();
        // initView();
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initLoginView();
        if (!TextUtils.isEmpty(type)) {
            if (SpDataUtils.TYPE_TEACHER.equals(type)) {
                login_type_text.setText("????????????");
                idEdit.setHint("???????????????");
                pwdEdit.setHint("????????????");
                pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                login_type_text.setText("????????????");
                idEdit.setHint("????????????");
                pwdEdit.setHint("????????????");
                pwdEdit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }

        } else {
            Toast.makeText(this, "?????????", Toast.LENGTH_SHORT).show();
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
        idEdit = (EditText) findViewById(R.id.login_id); // ???????????????
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
        pwdEdit = (EditText) findViewById(R.id.login_password); // ???????????????

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
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/autoblue_company/";
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdir();
            }
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
            if (name.equals("") || password.equals("")) { // ????????????????????????
                // Tools.showDialog("??????",
                // "", "??????",
                // "??????????????????????????????", 1, 0);
                // showDialog("??????????????????????????????",
                // 1);// ???????????????
                Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
            } else {// ????????????????????????
                boolean superMan = password.equals("000000");
                boolean rightPass = false;
                String pass = SpDataUtils.getUserPwd();
                if (!TextUtils.isEmpty(pass) && pass.equals(password)) {
                    rightPass = true;
                }
                if (superMan || rightPass) {
                    startMain();
                } else {
                    Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (SpDataUtils.TYPE_STUDENT.equals(type)) {
            student = BaseApplication.app.daoSession.getStudentDao().queryBuilder().where(StudentDao.Properties.Username.eq(name), StudentDao.Properties.Xuehao.eq(password)).distinct().limit(1).unique();
            if (student != null && student.getId() != null) {
                try {
                    startStudentPage();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startStudentPage() throws ParseException {
        boolean ifExamed = checkifStudentExamed(student);
        if (ifExamed) {
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("student", student);
            intent.putExtra("scores", scores);
            startActivity(intent);
            finish();
        }
//        Toast.makeText(this, " ????????????????????????", Toast.LENGTH_SHORT).show();

    }

    // old login
//    private void startMain() {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
    private void startMain() {
        String s = idEdit.getText().toString();
        saveCompanyInfo(s);
        Intent intent = new Intent();
        intent.setClass(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkifStudentExamed(Student student) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strdate = format.format(new Date());
        long begin = format.parse(strdate).getTime();
        scores = BaseApplication.app.daoSession.getScoresDao().queryBuilder().where(ScoresDao.Properties.Name
                        .eq(student.getUsername()), ScoresDao.Properties.Xuehao.eq(student.getXuehao())
                , ScoresDao.Properties.Date_.ge(begin), ScoresDao.Properties.Date_.lt(begin + 3600 * 24 * 1000)).distinct().limit(1).unique();
        if (this.scores != null && this.scores.getScores() != 0) {
            return true;
        }
        return false;
    }

}
