package com.xiaobailong_student.result;

/**
 * Created by dongyuangui on 2017/12/30.
 */

public class ResponseSaveScoreData {
    private int error;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

}
