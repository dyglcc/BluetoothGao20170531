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
import com.xiaobailong.response.ResponseData;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * <p>Login Handler.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class RequestSaveScoresHandler implements RequestHandler {
    private static String TAG = "AndServer";

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parse(request);

        Log.i(TAG, "Params: " + params.toString());

        String userName = URLDecoder.decode(params.get("username"), "utf-8");
        String xuehao = URLDecoder.decode(params.get("xuehao"), "utf-8");
//        String id = URLDecoder.decode(params.get("id"), "utf-8");
        String results = URLDecoder.decode(params.get("results"), "utf-8");
        String classes = URLDecoder.decode(params.get("classes"), "utf-8");
        String years_ = URLDecoder.decode(params.get("years"), "utf-8");
//        String sex = URLDecoder.decode(params.get("sex"), "utf-8");
        String cousumetime = URLDecoder.decode(params.get("cousumetime"), "utf-8");
        String devices = URLDecoder.decode(params.get("devices"), "utf-8");


        System.out.println("The Username: " + userName);
        System.out.println("The Password: " + xuehao);

//        Student student = BaseApplication.app.daoSession.getStudentDao().queryBuilder().where(StudentDao.Properties.Username.eq(userName), StudentDao.Properties.Xuehao.eq(password)).distinct().limit(1).unique();
//        if ("123".equals(userName) && "123".equals(password)) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Scores scores = new Scores();
        scores.setName(userName);
        scores.setXuehao(xuehao);
//        student.setId(Long.parseLong(id));
        scores.setScores(Integer.parseInt(results));
        scores.setClass_(Long.parseLong(classes));
        scores.setYear_(Long.parseLong(years_));
        scores.setConsume_time(cousumetime);
        scores.setDevices(devices);
        scores.setDate_(System.currentTimeMillis());
//        scores.setYear_(Long.parseLong(years_));
        // 学号
        ResponseData data = new ResponseData();
        try {
            BaseApplication.app.daoSession.getScoresDao().save(scores);
            data.setError(0);
            data.setMsg("保存学生成绩正常");
        } catch (Exception e) {
            data.setError(1);
            String msg = e == null ? "未知错误" : e.getMessage() + "";
            data.setMsg(msg);
        }
        Gson gson = new Gson();
        StringEntity stringEntity = new StringEntity(gson.toJson(data), "utf-8");
        Log.i(TAG, gson.toJson(data));
        response.setEntity(stringEntity);
    }
}
