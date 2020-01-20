package com.example.visualmath.data;

public class AlarmItem {

    private  String id;//게시글 아이디
    private String title;//게시글 이름
    private  String type; //알람 종류

    private  String details; //상세 내용 [ ] <- 이부분에 들어갈 내용

    public AlarmItem(String id,String title, String type) {
        this.id=id;
        this.title=title;

        if(type== "SOLVED"){
            this.details="[답변이 도착했습니다]";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
