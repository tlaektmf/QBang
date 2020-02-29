package com.visualstudy.visualmath.data;


/**
 * 1. 사진 url 최대 3장
 * 2. 텍스트 1개
 */

public class VM_Data_Default
{
    private String title;
    private String grade;
    private String problem;

    public VM_Data_Default(){

    }

    public VM_Data_Default(String _title, String _grade, String _problem){
        title=_title;
        grade=_grade;
        problem=_problem;
    }

    public VM_Data_Default(VM_Data_Default data_default) {
        this.grade=data_default.grade;
        this.title=data_default.title;
        this.problem=data_default.problem;
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

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}
