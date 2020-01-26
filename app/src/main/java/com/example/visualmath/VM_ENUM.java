package com.example.visualmath;

public  class VM_ENUM {

    //** post

    public static final String PROJECT_EMAIL ="visualmath" ;

    public static final String GRADE_HIGH ="고등" ;
    public static final String GRADE_MID = "중등";
    public static final String GRADE_ELEMENT = "초등";



    //** alarm type
    public static final String ALARM_DONE="DONE";//풀이된 문제가 도착 (학생)
    public static final String ALARM_MATCHED="MATCHED";//풀이된 문제가 도착 (학생)

    //** requestCode
    public static final int RC_GOOGLE_LOGIN=1000;
    public static final int RC_REGIOTHER_TO_PHOTOVIEW=999;

    public static final int RC_PICK_FROM_ALBUM = 998;
    public static final int RC_PICK_FROM_CAMERA = 997;
    public static final int RC_READ_REQUEST_CODE = 995;
    public static final int RC_PROBLEM_SOLVE =990 ;
    public static final int PICK_FROM_ALBUM = 1; //onActivityResult 에서 requestCode 로 반환되는 값
    public static final int PICK_FROM_CAMERA = 2;
    public static final int OTHER_DATA_LOAD = 3;

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
    public static final String IT_ARG_BLOCK="IT_ARG_BLOCK";
    public static final String IT_FROM_UNMATCHED ="IT_FROM_UNMATCHED" ;
    public static final String IT_MATCH_SUCCESS = "IT_MATCH_SUCCESS";
    public static final String IT_ALARM_MESSAGE ="IT_ALARM_MESSAGE" ;
    //** 상수 String
    public static final String TEXT="TEXT";
    public static final String VIDEO="VIDEO";
    public static final String TEACHER="TEACHER";
    public static final String STUDENT="STUDENT";
    public static final int GALLERY = 1;
    public static final int CAMERA = 2;
    public static final int NOTHING = -1;
    public static final String ALL = "all";




    //** DB TABLE
    public static final String DB_CHATS="CHATS";
    public static final String DB_POSTS="POSTS";
    public static final String DB_USERS="USERS";
    public static final String DB_STUDENTS="STUDENTS";
    public static final String DB_TEACHERS="TEACHERS";
    public static final String DB_UNMATCHED="UNMATCHED";



    //** DB CHILD
    public static final String DB_ALARMS = "alarms";
    public  static final String DB_chatList="chatList";
    public static final String DB_S_ID="s_id";
    public static final String DB_USER_TYPE="type";
    public static final String DB_INFO="info";
    public static final String DB_STU_UNMATCHED="unmatched";
    public static final String DB_STU_DONE="done";
    public static final String DB_STU_UNSOLVED="unsolved";
    public static final String DB_GRADE="grade";
    public static final String DB_P_ID="p_id";
    public static final String DB_T_ID ="t_id" ;
    public static final String DB_TITLE="title";
    public static final String DB_PROBLEM="problem";
    public static final String DB_UPLOAD_DATE="uploadDate";
    public static final String DB_STU_POSTS="posts";
    public static final String DB_DATA_DEFAULT="data_default";
    public static final String DB_TEA_POSTS ="posts" ;
    public static final String DB_TEA_UNSOLVED = "unsolved";
    public static final String DB_MATCH_STUDENT = "matchSet_student";
    public static final String DB_MATCH_TEACHER = "matchSet_teacher";
    public static final String DB_SOLVE_WAY ="solveWay" ;
    public static final String DB_TEA_DONE = "done";
    public static final String DB_SOLVE_PROBLEM ="solve_problem" ;
    public static final String DB_STU_ALARM ="alarms" ;
    public static final String DB_TEA_ALARM ="alarms" ;

    //** Preference Key
    public  static final String PRE_LOGOUT="logout";
    public  static final String PRE_ALARM="setAlarm";
    public  static final String PRE_TEST="test";
    public static final String PREF_USER_NAME = "username";


}

