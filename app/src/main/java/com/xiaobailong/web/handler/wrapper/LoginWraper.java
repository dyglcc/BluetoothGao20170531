package com.xiaobailong.web.handler.wrapper;

import com.xiaobailong.bean.Scores;
import com.xiaobailong.bean.Student;

/**
 * Created by dongyuangui on 2018/2/1.
 */

public class LoginWraper {
    Student student;
    Scores scores;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }
}
