package com.example.visualmath;

public class VM_ENUM {

    //** post
    static final int FINISH=0;
    static final int BEFORE_MATH=1;
    static final int AFTER_MATH=2;

    //** live
    static final int LIVE_NONE =0;//라이브 등록 문제가 아님
    static final int LIVE_WAIT=1;//라이브 등록 문제(시간 대기 중)
    static final int LIVE_FINISH=2;//라이브 등록 문제 해결 종료

    //** alarm type
    static final String SOLVED="SOLVED";//풀이된 문제가 도착 (학생)
    static final String SOLVED_ALARM_MESSAGE="문제 답변이 도착했습니다.";//풀이된 문제가 도착 (학생)
    static final String LIVE_ALARM_MESSAGE="라이브 5분전입니다.";//풀이된 문제가 도착 (학생)

}

