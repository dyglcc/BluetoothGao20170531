package com.xiaobailong.tools;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.xiaobailong.bluetoothfaultboardcontrol.R;

public class FileResource {

    public int width = 0;
    public int height = 0;
    public int dialog_height = 0;
    public int resourceID_Icon = 0;
    public int resourceID_Directory = 0;
    public int resourceID_UpDirectory = 0;
    public int resourceID_File = 0;

    public FileResource(Activity act) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        width = mDisplayMetrics.widthPixels;
        height = mDisplayMetrics.heightPixels;

        if (height < 600) {
            if (height < 500) {
                dialog_height = 250;
            } else {
                dialog_height = 300;
            }
            resourceID_Icon = R.drawable.file;
            resourceID_Directory = R.drawable.file;
            resourceID_UpDirectory = R.drawable.file;
            resourceID_File = R.drawable.filedialog_xlsfile_m;
        } else {
            dialog_height = 600;
            resourceID_Icon = R.drawable.file;
            resourceID_Directory = R.drawable.file;
            resourceID_UpDirectory = R.drawable.file;
            resourceID_File = R.drawable.filedialog_xlsfile_l;
        }
    }

}
