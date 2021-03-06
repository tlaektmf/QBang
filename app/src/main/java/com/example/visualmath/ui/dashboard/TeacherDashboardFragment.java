package com.example.visualmath.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.HomeActivity;
import com.example.visualmath.ItemDetailFragment;
import com.example.visualmath.ItemListActivity;
import com.example.visualmath.R;
import com.example.visualmath.TeacherHomeActivity;
import com.example.visualmath.VM_Data_Default;
import com.example.visualmath.VM_FullViewActivity;
import com.example.visualmath.dummy.DummyContent;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherDashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    private boolean mTwoPane;

    private String this_year;
    private String this_month;
    private String this_day;

    private TextView datecheck;
    private CalendarView calendar;
    private RecyclerView recyclerView;

    //    모드 변경 버튼
    private Button cal_mode_btn;

    //    검색창
    private Button search_btn;
    private Button search_cancel_btn;
    private ConstraintLayout search_input_lay;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    public static List<Pair<VM_Data_Default, Pair<String, String>>> subs; //포스트 데이터 일부 리스트 post/id/date
    public static List<Pair<VM_Data_Default, Pair<String, String>>> posts; //포스트 데이터 전체 리스트 post/id/date

    public TeacherHomeActivity parent;
    public static String TAG = "TeacherDashboardFrag";
    public int focusedYear;
    public int focusedMonth;
    public int focusedDay;


    public TeacherDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (TeacherHomeActivity) getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_teacher_dashboard, container, false);
        recyclerView = root.findViewById(R.id.teacher_calendar_recyclerview);
        datecheck = root.findViewById(R.id.teacher_datecheck);
        calendar = root.findViewById(R.id.teacher_calendar);


        //검색창
        search_input_lay = root.findViewById(R.id.teacher_search_input_lay);
        //캘린더 모드 변경
        cal_mode_btn = root.findViewById(R.id.teacher_cal_mode_change);
        //검색 버튼
        search_btn = root.findViewById(R.id.teacher_cal_search_btn);
        //검색 취소 버튼
        search_cancel_btn = root.findViewById(R.id.teacher_search_cancel_btn);

        dateInit();
        readDataBase();
        setupRecyclerView(recyclerView);


        cal_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = ((TeacherHomeActivity) getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();//트랜잭션 가져오기

                //대시보드리스트 프레그먼트로 replace
                tran.replace(R.id.teacher_nav_host_fragment, new TeacherDashboardListFragment());
                tran.commit();
            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_input_lay.setVisibility(search_input_lay.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });


        search_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_input_lay.setVisibility(search_input_lay.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        return root;
    }

    private void dateInit() {
        final long now = System.currentTimeMillis();//현재시간
        final Date date = new Date(now);//현재날짜

        final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());//현재 지역의 년도
        final SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());//현재 몇일

        final String year = yearFormat.format(date);
        final String month = monthFormat.format(date);
        final String day = dayFormat.format(date);

        this_year = year;
        this_month = month;
        this_day = day;

        //현재 포커싱된 달력뷰(년,월,일) 초기값
        focusedYear = Integer.parseInt(this_year);
        focusedMonth = Integer.parseInt(this_month);
        focusedDay = Integer.parseInt(this_day);


        datecheck.setText(this_year + "년 " + this_month + "월 " + this_day + "일 문제 목록");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //선택한 날짜가 전돨됨
                //현재 포커싱된 달력뷰(년,월,일) 정보 저장
                focusedYear = year;
                focusedMonth = month + 1;
                focusedDay = dayOfMonth;
                Log.d(TAG, "선택 -> 포커싱 변경: " + focusedYear + "-" + focusedMonth + "-" + focusedDay);

                datecheck.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일 문제 목록");

                subs = new ArrayList<Pair<VM_Data_Default, Pair<String, String>>>();

                String selectedDate = year + "-";

                if ((month + 1) < 10) {
                    selectedDate += "0" + (month + 1) + "-";
                } else {
                    selectedDate += (month + 1) + "-";
                }

                if (dayOfMonth < 10) {
                    selectedDate += "0" + (dayOfMonth);
                } else {
                    selectedDate += dayOfMonth;
                }

                Log.d(TAG, "[클릭이벤트]선택한 날짜: " + selectedDate);

                if (posts != null) {
                    for (int i = 0; i < posts.size(); i++) {
                        Log.d(TAG, "[클릭이벤트]포스트 날짜: " + posts.get(i).second.second);
                        if (posts.get(i).second.second.contains(selectedDate)) {
                            ///subs.add(Pair.create(posts.get(i),Pair.create(ids.get(i),dates.get(i))));
                            subs.add(Pair.create(posts.get(i).first,
                                    Pair.create(posts.get(i).second.first, posts.get(i).second.second)));
                        }
                    }
                }

                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane, parent));


            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ///recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Pair<VM_Data_Default, Pair<String, String>>> mValues;
        private final boolean mTwoPane;
        private final TeacherHomeActivity mParentActivity;

        SimpleItemRecyclerViewAdapter(List<Pair<VM_Data_Default, Pair<String, String>>> items, boolean twoPane, TeacherHomeActivity parent) {
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
//            기존 코드
////            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
//
//            holder.itemView.setTag(mValues.get(position));
////            holder.itemView.setOnClickListener(mOnClickListener);

            //>>>> 2020-01-13 삭제
//            if(holder.getAdapterPosition()!=RecyclerView.NO_POSITION){
//                holder.mContentView.setText(mValues.get(holder.getAdapterPosition()).content);
//                holder.itemView.setTag(mValues.get(holder.getAdapterPosition()));
//            }
            //>>>> 2020-01-13

            holder.mContentView.setText(mValues.get(position).first.getTitle());//내용(post_title)
            holder.itemView.setTag(mValues.get(position));

            //** Date 에서 시간만 표기 (시:분)
            String token = mValues.get(position).second.second.split(" ")[1];
            holder.mTimeView.setText(token);//시간

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTimeView;
            final TextView mContentView;


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
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, subs.get(pos).second.first);
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE, subs.get(pos).first.getTitle());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE, subs.get(pos).first.getGrade());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM, subs.get(pos).first.getProblem());
                            mParentActivity.startActivity(intent);
                            Toast.makeText(v.getContext(), "확인" + pos, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void readDataBase() {

        //** 데이터 읽기
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("STUDENTS");
        reference = reference.child("user_name")
                .child("posts").child("done");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                posts = new ArrayList<Pair<VM_Data_Default, Pair<String, String>>>();
                String post_id, post_date, post_title, post_grade, post_problem;
                String token;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    post_id = snapshot.getKey();
                    post_date = snapshot.child("time").getValue().toString();
                    post_title = snapshot.child("title").getValue().toString();
                    post_grade = snapshot.child("grade").getValue().toString();
                    post_problem = snapshot.child("problem").getValue().toString();
                    posts.add(Pair.create(new VM_Data_Default(post_title, post_grade, post_problem),
                            Pair.create(post_id, post_date)));
                    Log.d(TAG, "ValueEventListener : " + snapshot);

                }

                //** 처음 DashBoard 세팅

                //** 현재 포커싱된 날짜

                subs = new ArrayList<Pair<VM_Data_Default, Pair<String, String>>>();

                String today = this_year + "-" + this_month + "-" + this_day;
                Log.d(TAG, "오늘 날짜: " + today);

                String focusedDate = focusedYear + "-";

                if ((focusedMonth) < 10) {
                    focusedDate += "0" + focusedMonth + "-";
                } else {
                    focusedDate += focusedMonth + "-";
                }
                if (focusedDay < 10) {
                    focusedDate += "0" + focusedDay;
                } else {
                    focusedDate += focusedDay;
                }
                Log.d(TAG, "[ValueListener]현재포지션 날짜:" + focusedDate);


                if (posts != null) {
                    for (int i = 0; i < posts.size(); i++) {
                        Log.d(TAG, "[ValueListener]포스트 날짜: " + posts.get(i).second.second);
                        if (posts.get(i).second.second.contains(focusedDate)) {

                            subs.add(Pair.create(posts.get(i).first,
                                    Pair.create(posts.get(i).second.first, posts.get(i).second.second)));
                        }
                    }
                }

                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane, parent));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }
}
