package com.xiaobailong_student.beans;

/**
 * Entity mapped to table "SCORES".
 */
public class Scores implements java.io.Serializable {

    private Long id;
    private Integer scores;
    private Long date_;
    private String devices;
    private String xuehao;
    private String name;
    private Long class_;
    private Long year_;
    private String consume_time;

    public Scores() {
    }

    public Scores(Long id) {
        this.id = id;
    }

    public Scores(Long id, Integer scores, Long date_, String devices, String xuehao, String name, Long class_, Long year_, String consume_time) {
        this.id = id;
        this.scores = scores;
        this.date_ = date_;
        this.devices = devices;
        this.xuehao = xuehao;
        this.name = name;
        this.class_ = class_;
        this.year_ = year_;
        this.consume_time = consume_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScores() {
        return scores;
    }

    public void setScores(Integer scores) {
        this.scores = scores;
    }

    public Long getDate_() {
        return date_;
    }

    public void setDate_(Long date_) {
        this.date_ = date_;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public String getXuehao() {
        return xuehao;
    }

    public void setXuehao(String xuehao) {
        this.xuehao = xuehao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getClass_() {
        return class_;
    }

    public void setClass_(Long class_) {
        this.class_ = class_;
    }

    public Long getYear_() {
        return year_;
    }

    public void setYear_(Long year_) {
        this.year_ = year_;
    }

    public String getConsume_time() {
        return consume_time;
    }

    public void setConsume_time(String consume_time) {
        this.consume_time = consume_time;
    }

}
