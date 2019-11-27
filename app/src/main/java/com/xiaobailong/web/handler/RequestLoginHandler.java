/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaobailong.web.handler;

import android.util.Log;

import com.google.gson.Gson;
import com.xiaobailong.base.BaseApplication;
import com.xiaobailong.bean.Scores;
import com.xiaobailong.bean.ScoresDao;
import com.xiaobailong.bean.Student;
import com.xiaobailong.bean.StudentDao;
import com.xiaobailong.response.ResponseData;
import com.xiaobailong.web.handler.wrapper.LoginWraper;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * <p>Login Handler.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class RequestLoginHandler implements RequestHandler {

    private static String TAG = "AndServer";
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parse(request);

        Log.i(TAG, "Params: " + params.toString());

        String userName = URLDecoder.decode(params.get("username"), "utf-8");
        String password = URLDecoder.decode(params.get("xuehao"), "utf-8");

        System.out.println("The Username: " + userName);
        System.out.println("The Password: " + password);

        Student student = BaseApplication.app.daoSession.getStudentDao().queryBuilder().where(StudentDao.Properties.Username.eq(userName), StudentDao.Properties.Xuehao.eq(password)).distinct().limit(1).unique();

        ResponseData data = new ResponseData();
        Gson gson = new Gson();
        if (student != null && student.getId() != null) {
            data.setError(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String strdate = format.format(new Date());
            long begin = 0;
            try {
                begin = format.parse(strdate).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Scores scores = BaseApplication.app.daoSession.getScoresDao().queryBuilder().where(ScoresDao.Properties.Name
                            .eq(student.getUsername()), ScoresDao.Properties.Xuehao.eq(student.getXuehao())
                    , ScoresDao.Properties.Date_.ge(begin), ScoresDao.Properties.Date_.lt(begin + 3600 * 24 * 1000)).distinct().limit(1).unique();
            LoginWraper wraper = new LoginWraper();
            wraper.setScores(scores);
            wraper.setStudent(student);
            data.setData(wraper);
            data.setMsg("login success");
            Log.i(TAG, gson.toJson(data));
            StringEntity stringEntity = new StringEntity(gson.toJson(data), "utf-8");
            response.setEntity(stringEntity);
        } else {
            data.setError(1);
            data.setMsg("姓名或者学号不正确，请重新输入");
            StringEntity stringEntity = new StringEntity(gson.toJson(data), "utf-8");
            response.setEntity(stringEntity);
        }
    }
}
