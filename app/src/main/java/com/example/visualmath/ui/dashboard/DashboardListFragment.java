package com.example.visualmath.ui.dashboard;


import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.visualmath.activity.HomeActivity;
import com.example.visualmath.R;
import com.example.visualmath.adapter.FilterAdapter;
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
public class DashboardListFragment extends Fragment implements TextWatcher {

    //lhj_0_start
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    calendarListAdapater recyclerViewAdapter;

    //점찍힌 날짜 배열
    private ArrayList<CalendarDay> dotted_date_list;

    //    검색창
    private Button search_btn;
    private Button search_cancel_btn;
    private ConstraintLayout search_container;
    private InputMethodManager imm;
    private EditText search_editText;
    //검색 목록
    private RecyclerView searched_list;
    private FilterAdapter filterAdapter;

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

        //검색 관련 부분 초기화
        //**검색창
        search_container = rootView.findViewById(R.id.search_container);
        //검색 버튼
        search_btn = rootView.findViewById(R.id.cal_search_btn);
        //검색 취소 버튼
        search_cancel_btn = rootView.findViewById(R.id.serach_cancel_btn);
        search_editText = rootView.findViewById(R.id.search_editText);
        search_editText.addTextChangedListener(this);
        imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //검색 목록 리사이클러뷰
        searched_list = rootView.findViewById(R.id.searched_list);
        //검색 관련 부분 초기화 끝

        //검색 관련 클릭 리스너
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_container.setVisibility(search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                //검색 어댑터 생성
//                Log.d("filter","posts 사이즈 : "+posts.size());
//                filterAdapter = new FilterAdapter(getContext(),posts);
                searched_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                searched_list.setAdapter(filterAdapter);
            }
        });

        search_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_container.setVisibility(search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                hideKeyboard();
                search_editText.setText("");
            }
        });
        //검색 관련 클릭 리스터 끝

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
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_editText.getWindowToken(),0);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //검색할 데이터가 없어서 터지기 때문에 우선 주석으로 막아둠
        //        filterAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
