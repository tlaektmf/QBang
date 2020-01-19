package com.example.visualmath.dummy;

public class PostCustomData {
    private String p_id;
    private String p_title;
    private String solveWay;
    private String upLoadDate;

    public PostCustomData(){

    }
    public PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate){
        this.p_id=p_id;
        this.p_title=p_title;
        this.solveWay=solveWaym;
        this.upLoadDate=upLoadDate;
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
}
