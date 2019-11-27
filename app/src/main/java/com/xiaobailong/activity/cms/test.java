package com.xiaobailong.activity.cms;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by dongyuangui on 2018/2/3.
 */

public class test {
    public static void main(String[] args){
        long t= System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
        System.out.println(format.format(t));
    }
}
