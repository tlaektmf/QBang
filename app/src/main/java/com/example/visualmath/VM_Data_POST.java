package com.example.visualmath;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class VM_Data_POST {
    private String p_id;//게시글 고유 id
    private int state;
    private int live_state;
    private String liveTime; //live 시간
    private String matchSet_teacher; // student-teacher match set
    private String matchSet_student; // student-teacher match set
    private String uploadDate; //업로드 날짜

    private List<VM_Data_CHAT> chatList;
//    private VM_Data_BASIC data_basic;

    private VM_Data_Default data_default;
    private VM_Data_EXTRA data_extra;

    //** data_add 부분이 Parcelable 객체로 되어 있어서 firebase에 데이터를 바로 넣을 수 없음
    ///private VM_Data_ADD data_add;
//    private String content;
//    private Uri add_picture1;
//    private Uri add_picture2;
//    private Uri add_picture3;

    //**

    public VM_Data_POST(VM_Data_Default _vm_data_default,
                        VM_Data_EXTRA _vm_data_extra,
                        String _s_id,String _key,String _uploadDate,int _live_state){
        p_id=_key;
        state=VM_ENUM.BEFORE_MATH;
        live_state=_live_state;
        uploadDate=_uploadDate;
        matchSet_student=_s_id;
        matchSet_teacher=null;

        // 필수 아닌 것
        liveTime=null;
        chatList=new ArrayList<>();
        VM_Data_CHAT chat=new VM_Data_CHAT("다슬","sdfsd",0,0);
        chatList.add(chat);

        data_default=_vm_data_default;

        if(_vm_data_extra!=null){
            data_extra=_vm_data_extra;

            ///** DB저장할때, storage uri로 변경해서 저장 필요
            ///그냥 content uri 사용 시 보안문제로 error
            if(_vm_data_extra.getAdd_picture1()!=null){
                data_extra.setAdd_picture1(matchSet_student+"/"+
                        p_id+"/"+
                        "picture1");
            }
            if(_vm_data_extra.getAdd_picture2()!=null){
                data_extra.setAdd_picture2(matchSet_student+"/"+
                        p_id+"/"+
                        "picture2");
            }
           if(_vm_data_extra.getAdd_picture3()!=null){
               data_extra.setAdd_picture3(matchSet_student+"/"+
                       p_id+"/"+
                       "picture3");
           }
            ///**
        }


        ///** DB저장할때, storage uri로 변경해서 저장 필요
        ///그냥 content uri 사용 시 보안문제로 error
        data_default.setProblem(matchSet_student+"/"+
                p_id+"/"+
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getLive_state() {
        return live_state;
    }

    public void setLive_state(int live_state) {
        this.live_state = live_state;
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



//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public Uri getAdd_picture1() {
//        return add_picture1;
//    }
//
//    public void setAdd_picture1(Uri add_picture1) {
//        this.add_picture1 = add_picture1;
//    }
//
//    public Uri getAdd_picture2() {
//        return add_picture2;
//    }
//
//    public void setAdd_picture2(Uri add_picture2) {
//        this.add_picture2 = add_picture2;
//    }
//
//    public Uri getAdd_picture3() {
//        return add_picture3;
//    }
//
//    public void setAdd_picture3(Uri add_picture3) {
//        this.add_picture3 = add_picture3;
//    }


}
