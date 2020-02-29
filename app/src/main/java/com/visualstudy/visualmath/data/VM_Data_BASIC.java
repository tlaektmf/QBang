package com.visualstudy.visualmath.data;

import android.net.Uri;

public class VM_Data_BASIC {

    private String title;
    private String grade;
    private Uri problem;

    public VM_Data_BASIC(){
     this.title=null;
     this.grade=null;
     this.problem=null;

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Uri getProblem() {
        return problem;
    }

    public void setProblem(Uri problem) {
        this.problem = problem;
    }
}
