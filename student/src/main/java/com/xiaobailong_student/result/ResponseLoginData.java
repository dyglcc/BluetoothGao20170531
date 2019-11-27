package com.xiaobailong_student.result;

import com.xiaobailong_student.beans.LoginWraper;

/**
 * Created by dongyuangui on 2017/12/30.
 */

public class ResponseLoginData {
    private int error ;

    private LoginWraper data;

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public LoginWraper getData() {
        return data;
    }

    public void setData(LoginWraper data) {
        this.data = data;
    }
}
