package com.example.visualmath.ui.dashboard;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.visualmath.R;
import com.example.visualmath.activity.TeacherHomeActivity;
import com.example.visualmath.calendarListAdapater;
import com.example.visualmath.date_data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherDashboardListFragment extends Fragment {

    //lhj_0_start
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    calendarListAdapater recyclerViewAdapter;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM",Locale.KOREA);
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd",Locale.KOREA);

    //2. 무조건 1월부터 시작하는 방법
    Integer year = Integer.parseInt(yearFormat.format(date));
    Integer month = 0;
    Integer day = 1;

    //lhj_0_end

    ViewGroup rootView;

    public TeacherDashboardListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_teacher_dashboard_list, container, false);


        //캘린더 모드 변경
        Button btn = rootView.findViewById(R.id.teacher_button);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FragmentManager fm = ((TeacherHomeActivity)getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기
                tran.replace(R.id.teacher_nav_host_fragment,new TeacherDashboardFragment()).commit();

            }
        });

        //lhj_1_start
        recyclerView = rootView.findViewById(R.id.teacher_continuous_cal_rview);
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

//        recyclerViewAdapter = new calendarListAdapater(this,date_list);
        recyclerView.setAdapter(recyclerViewAdapter);

        //lhj_1_end

        return rootView;

    }

}
