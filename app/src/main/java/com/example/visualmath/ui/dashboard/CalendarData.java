package com.example.visualmath.ui.dashboard;

import android.util.Pair;

import com.example.visualmath.data.VM_Data_Default;

import java.util.List;

public class CalendarData {
    public static List<Pair<VM_Data_Default, Pair<String,String>>> posts; //포스트 데이터 전체 리스트 post/id/date
    public static List <String> dates;
    public static  List<Pair<VM_Data_Default,Pair<String,String>>> subs; //포스트 데이터 일부 리스트 post/id/date
}
