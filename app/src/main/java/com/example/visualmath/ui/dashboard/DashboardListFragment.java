package com.example.visualmath.ui.dashboard;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.visualmath.activity.HomeActivity;
import com.example.visualmath.R;
import com.example.visualmath.calendarListAdapater;
import com.example.visualmath.date_data;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardListFragment extends Fragment {

    //lhj_0_start
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    calendarListAdapater recyclerViewAdapter;

    //점찍힌 날짜 배열
    private ArrayList<CalendarDay> dotted_date_list;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM",Locale.KOREA);
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd",Locale.KOREA);

    //1. 현재 시간으로 가져오는 방법
//    Integer year = Integer.parseInt(yearFormat.format(date));
//    Integer month = Integer.parseInt(monthFormat.format(date));
//    Integer day = Integer.parseInt(dayFormat.format(date));

    //2. 무조건 1월부터 시작하는 방법
    Integer year = Integer.parseInt(yearFormat.format(date));
    Integer month = 0;
    Integer day = 1;

    //lhj_0_end

    ViewGroup rootView;
    public DashboardListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        //배열 초기화
        dotted_date_list = new ArrayList<CalendarDay>();

        //캘린더 모드 변경
        Button btn = rootView.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //datecheck.setVisibility(datecheck.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                //recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                FragmentManager fm = ((HomeActivity)getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기
                tran.replace(R.id.nav_host_fragment,new DashboardFragment()).commit();

            }
        });

        //lhj_1_start
        recyclerView = rootView.findViewById(R.id.continuous_cal_rview);
        linearLayoutManager = new LinearLayoutManager(rootView.getContext());

        recyclerView.setLayoutManager(linearLayoutManager);

//        달력에 데이터 넣는 부분
        List<date_data> date_list = new ArrayList<>();
//        한 개만 테스트로 넣어본 줄(97)
//        date_list.add(new date_data(year,month,day));

//        우선 그냥 for문으로 넣어둠
        for(int i=0;i<12;i++){
            date_list.add(new date_data(year,month+i,day));
        }

        recyclerViewAdapter = new calendarListAdapater(this,date_list,dotted_date_list);
        recyclerView.setAdapter(recyclerViewAdapter);

        //lhj_1_end

        return rootView;

    }
}
