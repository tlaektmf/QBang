package com.example.visualmath;

import android.net.Uri;
import java.util.ArrayList;

public class VM_Data_POST {
    private String p_id;//게시글 고유 id
    private String content;// detail content
    private Uri[] add_pictures;
    private Uri main_problem;
    private int state;
    private int live_state;
    private String liveTime; //live 시간
    private String[] matchSet; // student-teacher match set
    private String uploadDate; //업로드 날짜
    private ArrayList<VM_Data_CHAT> chatList;

    VM_Data_POST(String _s_id,Uri _main_problem,String _key,String _uploadDate,int _live_state){
        p_id=_key;
        state=VM_ENUM.BEFORE_MATH;
        live_state=_live_state;
        main_problem=_main_problem;
        uploadDate=_uploadDate;
        matchSet=new String[2];
        matchSet[0]=_s_id;

        // 필수 아닌 것
        content=null;
        add_pictures=new Uri[3];
        liveTime=null;
        chatList=new ArrayList<>();

    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Uri[] getAdd_pictures() {
        return add_pictures;
    }

    public void setAdd_pictures(Uri[] add_pictures) {
        this.add_pictures = add_pictures;
    }

    public Uri getMain_problem() {
        return main_problem;
    }

    public void setMain_problem(Uri main_problem) {
        this.main_problem = main_problem;
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

    public String[] getMatchSet() {
        return matchSet;
    }

    public void setMatchSet(String[] matchSet) {
        this.matchSet = matchSet;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public ArrayList<VM_Data_CHAT> getChatList() {
        return chatList;
    }

    public void setChatList(ArrayList<VM_Data_CHAT> chatList) {
        this.chatList = chatList;
    }
}
