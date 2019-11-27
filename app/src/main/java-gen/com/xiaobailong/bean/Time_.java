package com.xiaobailong.bean;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TIME_".
 */
@Entity
public class Time_ implements java.io.Serializable {

    @Id(autoincrement = true)
    private Long id;
    private Long parent;
    private String path;

    @NotNull
    private String filename;

    @Generated
    public Time_() {
    }

    public Time_(Long id) {
        this.id = id;
    }

    @Generated
    public Time_(Long id, Long parent, String path, String filename) {
        this.id = id;
        this.parent = parent;
        this.path = path;
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NotNull
    public String getFilename() {
        return filename;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFilename(@NotNull String filename) {
        this.filename = filename;
    }

}
