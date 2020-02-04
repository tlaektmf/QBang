package com.example.visualmath.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.activity.HomeActivity;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.example.visualmath.adapter.FilterAdapter;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFullViewFragment extends Fragment {

    private boolean mTwoPane;

    private String this_year;
    private String this_month;
    private String this_day;

    private ViewGroup root;
    private TextView datecheck;
    private CalendarView calendar;
    private RecyclerView recyclerView;


    public HomeActivity parent;
    public static String TAG=VM_ENUM.TAG;
    public int focusedYear;
    public int focusedMonth;
    public int focusedDay;


    public CalendarFullViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_full_view, container, false);
        parent=(HomeActivity)getActivity();

        recyclerView = root.findViewById(R.id.calendar_recyclerview);
        datecheck = root.findViewById(R.id.datecheck);
        calendar = root.findViewById(R.id.calendar);
        setupRecyclerView(recyclerView);

        dateInit();

        return root;
    }

    private void dateInit() {
        final long now = System.currentTimeMillis();//현재시간
        final Date date = new Date(now);//현재날짜

        final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);//현재 지역의 년도
        final SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.KOREA);//현재 몇일

        final String year = yearFormat.format(date);
        final String month = monthFormat.format(date);
        final String day = dayFormat.format(date);

        this_year = year;
        this_month = month;
        this_day = day;


        //현재 포커싱된 달력뷰(년,월,일) 초기값
        focusedYear=Integer.parseInt(this_year);
        focusedMonth=Integer.parseInt(this_month);
        focusedDay=Integer.parseInt(this_day);

        datecheck.setText(this_year + "년 " + this_month + "월 " + this_day + "일 문제 목록");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //선택한 날짜가 전돨됨


                datecheck.setText(year + "년 " + (month+1) + "월 " + dayOfMonth + "일 문제 목록");

                DashboardFragment.subs=new ArrayList<Pair<VM_Data_Default, Pair<String,String>>>();

                String selectedDate=year+"-";

                if((month+1)<10){
                    selectedDate+="0"+(month+1)+"-";
                }else{
                    selectedDate+=(month+1)+"-";
                }

                if(dayOfMonth<10){
                    selectedDate+="0"+(dayOfMonth);
                }else{
                    selectedDate+=dayOfMonth;
                }

                Log.d(TAG,"[클릭이벤트]선택한 날짜: "+selectedDate);

                if(DashboardFragment.posts!=null){
                    for(int i=0;i<DashboardFragment.posts.size();i++){
                        Log.d(TAG,"[클릭이벤트]포스트 날짜: "+DashboardFragment.posts.get(i).second.second);
                        if(DashboardFragment.posts.get(i).second.second.contains(selectedDate)){
                            DashboardFragment.subs.add(Pair.create(DashboardFragment.posts.get(i).first,
                                    Pair.create(DashboardFragment.posts.get(i).second.first,DashboardFragment.posts.get(i).second.second)));
                        }
                    }
                }

                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DashboardFragment.subs, mTwoPane,parent));

            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Pair<VM_Data_Default,Pair<String,String>>> mValues;
        private final boolean mTwoPane;
        private final HomeActivity mParentActivity;

        SimpleItemRecyclerViewAdapter(List<Pair<VM_Data_Default,Pair<String,String>>>  items, boolean twoPane,HomeActivity parent) {
            mValues = items;
            mTwoPane = twoPane;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_calendar, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mContentView.setText(mValues.get(position).first.getTitle());//내용(post_title)
            holder.itemView.setTag(mValues.get(position));

            //** Date 에서 시간만 표기 (시:분)
            String token=mValues.get(position).second.second;
            holder.mTimeView.setText(token);//시간

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mContentView;
            final TextView mTimeView;
            ViewHolder(View view) {
                super(view);

                mContentView = (TextView) view.findViewById(R.id.problem_name);
                mTimeView = (TextView) view.findViewById(R.id.endTime);


                //** 아이템 클릭 이벤트
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
                            // post_id 인자
                            Intent intent = new Intent(mParentActivity, VM_FullViewActivity.class);
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, DashboardFragment.subs.get(pos).second.first);
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,DashboardFragment.subs.get(pos).first.getTitle());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,DashboardFragment.subs.get(pos).first.getGrade());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,DashboardFragment.subs.get(pos).first.getProblem());
                            intent.putExtra(VM_ENUM.IT_ARG_BLOCK,VM_ENUM.IT_ARG_BLOCK); //** dashboard에서는 완료된 항목이 가기 때문에 모든 창을 비활성화
                            mParentActivity.startActivity(intent);
                            Toast.makeText(v.getContext(), "확인" + pos, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }



}
