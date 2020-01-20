package com.example.visualmath.data;

import java.util.ArrayList;
import java.util.List;

public class VM_Data_POST {
    private String p_id;//게시글 고유 id

    private String liveTime; //live 시간
    private String matchSet_teacher; // student-teacher match set
    private String matchSet_student; // student-teacher match set
    private String uploadDate; //업로드 날짜
    private String solveWay; //풀이 방식
    private List<VM_Data_CHAT> chatList;


    private VM_Data_Default data_default;
    private VM_Data_EXTRA data_extra;

    //** data_add 부분이 Parcelable 객체로 되어 있어서 firebase에 데이터를 바로 넣을 수 없음

    //private int state; //>>삭제 예정
    //private int live_state;  ////>>삭제 예정

    public VM_Data_POST(VM_Data_Default _vm_data_default,
                        VM_Data_EXTRA _vm_data_extra,
                        String _s_id,String _key,String _uploadDate,String _solveWay){

        this.p_id=_key;
        this.uploadDate=_uploadDate;
        this.solveWay=_solveWay;
        this.matchSet_student=_s_id;
        this.matchSet_teacher=null;


        //** 아직 입력이 안된 기본값들 초기 세팅
        this.liveTime=null;
        this.chatList=new ArrayList<>();
        ///VM_Data_CHAT chat=new VM_Data_CHAT("다슬","sdfsd",0,0);
        ///chatList.add(chat);

        this.data_default=_vm_data_default;

        if(_vm_data_extra!=null){
            this.data_extra=_vm_data_extra;

            ///** DB저장할때, storage uri로 변경해서 저장 필요
            ///그냥 content uri 사용 시 보안문제로 error
            if(_vm_data_extra.getAdd_picture1()!=null){
                this.data_extra.setAdd_picture1(this.matchSet_student+"/"+
                        this.p_id+"/"+
                        "picture1");
            }
            if(_vm_data_extra.getAdd_picture2()!=null){
                this.data_extra.setAdd_picture2(this.matchSet_student+"/"+
                        this.p_id+"/"+
                        "picture2");
            }
           if(_vm_data_extra.getAdd_picture3()!=null){
               this.data_extra.setAdd_picture3(this.matchSet_student+"/"+
                       this.p_id+"/"+
                       "picture3");
           }
            ///**
        }


        ///** DB저장할때, storage uri로 변경해서 저장 필요
        ///그냥 content uri 사용 시 보안문제로 error
        ///null일수 없으므로 별도의 검사는 하지 않겠음
        this.data_default.setProblem(this.matchSet_student+"/"+
                this.p_id+"/"+
                "problem"
        );
        ///**

    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }


    public VM_Data_EXTRA getData_extra() {
        return data_extra;
    }

    public void setData_extra(VM_Data_EXTRA data_extra) {
        this.data_extra = data_extra;
    }

    public VM_Data_Default getData_default() {
        return data_default;
    }

    public void setData_default(VM_Data_Default data_default) {
        this.data_default = data_default;
    }


    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }


    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<VM_Data_CHAT> getChatList() {
        return chatList;
    }

    public void setChatList(List<VM_Data_CHAT> chatList) {
        this.chatList = chatList;
    }

    public String getMatchSet_teacher() {
        return matchSet_teacher;
    }

    public void setMatchSet_teacher(String matchSet_teacher) {
        this.matchSet_teacher = matchSet_teacher;
    }

    public String getMatchSet_student() {
        return matchSet_student;
    }

    public void setMatchSet_student(String matchSet_student) {
        this.matchSet_student = matchSet_student;
    }

    public String getSolveWay() {
        return solveWay;
    }

    public void setSolveWay(String solveWay) {
        this.solveWay = solveWay;
    }

//
//    public int getState() {
//        return state;
//    }
//
//    public void setState(int state) {
//        this.state = state;
//    }
//
//    public int getLive_state() {
//        return live_state;
//    }
//
//    public void setLive_state(int live_state) {
//        this.live_state = live_state;
//    }

}
