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
import com.xiaobailong.bean.Examination;
import com.xiaobailong.bean.ExaminationDao;
import com.xiaobailong.response.ResponseData;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

/**
 * <p>Login Handler.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class RequestExaminationHandler implements RequestHandler {
    private static String TAG = "AndServer";

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parse(request);

        Log.i(TAG, "Params: " + params.toString());

//        String userName = URLDecoder.decode(params.get("username"), "utf-8");
//        String password = URLDecoder.decode(params.get("xuehao"), "utf-8");
//
//        System.out.println("The Username: " + userName);
//        System.out.println("The Password: " + password);

        Examination examination = BaseApplication.app.daoSession.getExaminationDao().queryBuilder()
                .where(ExaminationDao.Properties.Expired.eq(false))
                .orderDesc(ExaminationDao.Properties.Id).limit(1).unique();

        ResponseData data = new ResponseData();
        Gson gson = new Gson();
        if (examination != null && examination.getId() != null) {
            data.setError(0);
            data.setData(examination);
            data.setMsg("获取考试信息成功");
            StringEntity stringEntity = new StringEntity(gson.toJson(data), "utf-8");
            Log.i(TAG, gson.toJson(data));
            response.setEntity(stringEntity);
        } else {
            data.setError(1);
            data.setMsg("老师未出题");
            StringEntity stringEntity = new StringEntity(gson.toJson(data), "utf-8");
            response.setEntity(stringEntity);
        }
    }
}
