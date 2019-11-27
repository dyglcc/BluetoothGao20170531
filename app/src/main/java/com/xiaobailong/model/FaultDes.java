package com.xiaobailong.model;

import java.io.File;
import java.util.List;

/**
 * Created by dongyuangui on 2018/1/6.
 */

public class FaultDes {
    private List<FaultBean> datas;
    private String seriaData = null;
    private File srcfile = null;

    public List<FaultBean> getDatas() {
        return datas;
    }

    public void setDatas(List<FaultBean> datas) {
        this.datas = datas;
    }

    public String getSeriaData() {
        return seriaData;
    }

    public void setSeriaData(String seriaData) {
        this.seriaData = seriaData;
    }

    public File getSrcfile() {
        return srcfile;
    }

    public void setSrcfile(File srcfile) {
        this.srcfile = srcfile;
    }
}
