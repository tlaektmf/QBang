package com.example.visualmath.data;

import com.example.visualmath.VM_ENUM;

/**
 * 학생
 * - 문제 매칭 완료
 * - chat 메세지 오는 경우
 *
 * 선생
 * - chat 메세지 오는 경우
 * - 학생이 문제 완료 누르는 경우
 */
public class AlarmItem {

    private  String id;//게시글 아이디
    private String title;//게시글 이름
    private  String type; //알람 종류 (TAG)

    private  String details; //상세 내용 [ ] <- 이부분에 들어갈 내용

    public AlarmItem(){

    }
    public AlarmItem(String id,String title, String type) {
        this.id=id;
        this.title=title;

        if(type.equals(VM_ENUM.ALARM_MATCHED) ){
            this.details="[선생님이 문제를 선택하였습니다.]";
        }
        if(type.equals(VM_ENUM.ALARM_NEW)){
            this.details="[새로운 메세지가 도착했습니다]";
        }
        if(type.equals(VM_ENUM.ALARM_DONE)){
            this.details="[학생이 문제를 완료했습니다]";
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
