package com.example.visualmath;

public  class VM_ENUM {

    //** post
    public static final int FINISH=0;  //>>삭제 예정
    static final int BEFORE_MATCH=1; //>>삭제 예정
    static final int AFTER_MACTH=2; //>>삭제 예정

    //** live
    static final int LIVE_NONE =0;//라이브 등록 문제가 아님 //>>삭제 예정
    static final int LIVE_WAIT=1;//라이브 등록 문제(시간 대기 중) //>>삭제 예정
    static final int LIVE_FINISH=2;//라이브 등록 문제 해결 종료 //>>삭제 예정

    //** alarm type
    public static final String SOLVED="SOLVED";//풀이된 문제가 도착 (학생)
    public static final String SOLVED_ALARM_MESSAGE="문제 답변이 도착했습니다.";//풀이된 문제가 도착 (학생)
    public static final String LIVE_ALARM_MESSAGE="라이브 5분전입니다.";//풀이된 문제가 도착 (학생)

    //** requestCode
    public static final int RC_GOOGLE_LOGIN=1000;
    public static final int RC_REGIOTHER_TO_PHOTOVIEW=999;

    public static final int RC_PICK_FROM_ALBUM = 998;
    public static final int RC_PICK_FROM_CAMERA = 997;


    //** Log TAG
    public static final String TAG="다슬";

    //** intent
    public static final String SOLVE_WAY="SOLVE_WAY";
    public static final String PHOTO_URI="PHOTO_URI";
    public static final String CHANGED_PHOTO_URI="CHANGED_PHOTO_URI";
    public static final String IT_DELETE_PHOTO_INDEX="IT_DELETE_PHOTO_INDEX";
    public static final String IT_PHOTO_INDEX="IT_PHOTO_INDEX";
    public static final String IT_TAKE_PHOTO="IT_TAKE_PHOTO";
    public static final String IT_GALLERY_PHOTO="IT_GALLERY_PHOTO";

    //** 상수 String
    public static final String TEXT="TEXT";
    public static final String VIDEO="VIDEO";

    //** DB TABLE
    public static final String DB_CHATS="CHATS";
    public static final String DB_POSTS="POSTS";

    //** DB CHILD
    public  static final String DB_chatList="chatList";

    //** Preference Key
    public  static final String PRE_LOGOUT="logout";
    public  static final String PRE_ALARM="setAlarm";
    public  static final String PRE_TEST="test";
    public static final String PREF_USER_NAME = "username";
}

