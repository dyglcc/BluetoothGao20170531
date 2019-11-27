package com.xiaobailong.tools;

import android.content.Context;

import java.io.IOException;

/**
 * Created by dongyuangui on 2017/6/2.
 */

public class Utils {
    public static boolean isRightIdNum(String idString) {

        if (idString == null || idString.length() < 15) {
            return false;
        }
        if (idString.length() == 15) {
            String s;
            try {
                s = IdNumberUtil.Convert(idString);
                if (s == null) {
                    return false;
                }
                return IdNumberUtil.getInfof(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (idString.length() == 18) {
            return IdNumberUtil.getInfoe(idString);
        } else if (idString.length() != 15 && idString.length() != 18) {
            return false;
        }
        return true;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
