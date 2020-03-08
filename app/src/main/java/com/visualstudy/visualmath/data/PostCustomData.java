package com.visualstudy.visualmath.data;

import androidx.annotation.Nullable;

import com.visualstudy.visualmath.VM_ENUM;

public class PostCustomData {
    private String p_id;
    private String title;
    private String solveWay;
    private String upLoadDate;

    private String matchSet_student;
    private String matchSet_teacher;

    private String time;
    private String problem;
    private String grade;

    public PostCustomData(){

    }

    public PostCustomData(String p_id,String p_title,String upLoadDate){
        this.p_id=p_id;
        this.title=p_title;
        this.upLoadDate=upLoadDate;
    }

//    public PostCustomData(String p_id,String p_title,String solveWay ,String upLoadDate){
//        this.p_id=p_id;
//        this.title=p_title;
//        this.solveWay=solveWay;
//        this.upLoadDate=upLoadDate;
//    }


    public PostCustomData(String p_id,String p_title,String solveWay ,String upLoadDate,String grade, String problem,String matchSet){
        this.p_id=p_id;
        this.title=p_title;
        this.grade=grade;
        this.problem=problem;
        this.solveWay=solveWay;
        this.upLoadDate=upLoadDate;

        if(matchSet!=null){
            String mailDomain = matchSet.split("_")[1];
            if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
                this.matchSet_teacher=matchSet;
            }else{
                this.matchSet_student=matchSet;
            }

        }else{

        }

    }

    public PostCustomData(String p_id,String p_title,String solveWay ,String upLoadDate,String student,String teacher){
        this.p_id=p_id;
        this.title=p_title;
        this.solveWay=solveWay;
        this.upLoadDate=upLoadDate;
        this.matchSet_student=student;
        this.matchSet_teacher=teacher;
    }

    //** post "done"
    public PostCustomData(String p_id,String p_title,String grade,String problem,String time){
        this.p_id=p_id;
        this.title=p_title;
        this.grade=grade;
        this.problem=problem;
        this.time=time;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        //return super.equals(obj);

        //** 메소드 오버라이드 : post_id 가 같은 경우는 같다고 함
        PostCustomData other= (PostCustomData) obj;
        if(this.p_id.equals(other.getP_id())){
            return true;
        }
        return false;
    }
}
