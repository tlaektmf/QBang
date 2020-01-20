package com.example.visualmath.data;

public class PostCustomData {
    private String p_id;
    private String p_title;
    private String solveWay;
    private String upLoadDate;

    private String matchSet_student;
    private String matchSet_teacher;

    public PostCustomData(){

    }

    public PostCustomData(String p_id,String p_title,String upLoadDate){
        this.p_id=p_id;
        this.p_title=p_title;
        this.upLoadDate=upLoadDate;
    }

    public PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate){
        this.p_id=p_id;
        this.p_title=p_title;
        this.solveWay=solveWaym;
        this.upLoadDate=upLoadDate;
    }
    public PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate,String student,String teacher){
        this.p_id=p_id;
        this.p_title=p_title;
        this.solveWay=solveWaym;
        this.upLoadDate=upLoadDate;
        this.matchSet_student=student;
        this.matchSet_teacher=teacher;
    }
    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_title() {
        return p_title;
    }

    public void setP_title(String p_title) {
        this.p_title = p_title;
    }

    public String getSolveWay() {
        return solveWay;
    }

    public void setSolveWay(String solveWay) {
        this.solveWay = solveWay;
    }

    public String getUpLoadDate() {
        return upLoadDate;
    }

    public void setUpLoadDate(String upLoadDate) {
        this.upLoadDate = upLoadDate;
    }

    public String getMatchSet_student() {
        return matchSet_student;
    }

    public void setMatchSet_student(String matchSet_student) {
        this.matchSet_student = matchSet_student;
    }

    public String getMatchSet_teacher() {
        return matchSet_teacher;
    }

    public void setMatchSet_teacher(String matchSet_teacher) {
        this.matchSet_teacher = matchSet_teacher;
    }
}
