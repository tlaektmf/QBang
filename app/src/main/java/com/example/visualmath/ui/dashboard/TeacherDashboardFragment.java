package com.example.visualmath.ui.dashboard;

import android.content.Context;
import android.content.Intent;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.VM_ENUM;
import com.example.visualmath.adapter.FilterAdapter;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.fragment.CalendarFullViewFragment;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.example.visualmath.R;
import com.example.visualmath.activity.TeacherHomeActivity;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.activity.VM_FullViewActivity;
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
public class TeacherDashboardFragment extends Fragment implements TextWatcher {
    private DashboardViewModel dashboardViewModel;

//    private String this_year;
//    private String this_month;
//    private String this_day;

//    private TextView datecheck;
//    private CalendarView calendar;
//    private RecyclerView recyclerView;

    //    모드 변경 버튼
    private Button cal_mode_btn;

    //    검색창
    private Button search_btn;
    private Button search_cancel_btn;
    private ConstraintLayout teacher_search_container;
    private InputMethodManager imm;
    private EditText search_editText;
    private RecyclerView searched_list;
    private FilterAdapter filterAdapter;

    //로딩창
    private ProgressBar cal_loading_bar;
    private View cal_loading_back;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

//    public static List<Pair<VM_Data_Default, Pair<String, String>>> subs; //포스트 데이터 일부 리스트 post/id/date
//    public static List<Pair<VM_Data_Default, Pair<String, String>>> posts; //포스트 데이터 전체 리스트 post/id/date
//    public static List <String> dates;

    private TeacherHomeActivity parent;
    private String TAG = VM_ENUM.TAG;
//    public int focusedYear;
//    public int focusedMonth;
//    public int focusedDay;

    private String user_id;
    private String user_type;
    private String user_join_date;

    private  int view_click_count;

    public TeacherDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (TeacherHomeActivity) getActivity();

        //** 유저 정보 설정
        user_join_date=null;
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        user_id = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
            //선생님
            user_type=VM_ENUM.TEACHER;
        }else{
            user_type=VM_ENUM.STUDENT;
        }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_teacher_dashboard, container, false);
//        recyclerView = root.findViewById(R.id.teacher_calendar_recyclerview);
//        datecheck = root.findViewById(R.id.teacher_datecheck);
//        calendar = root.findViewById(R.id.teacher_calendar);

        //로딩창
        cal_loading_bar = root.findViewById(R.id.cal_loading_bar);
        cal_loading_back = root.findViewById(R.id.cal_loading_back);

        //검색창
        teacher_search_container = root.findViewById(R.id.teacher_search_container);
        //캘린더 모드 변경
        cal_mode_btn = root.findViewById(R.id.teacher_cal_mode_change);
        //검색 버튼
        search_btn = root.findViewById(R.id.teacher_cal_search_btn);
        //검색 취소 버튼
        search_cancel_btn = root.findViewById(R.id.teacher_search_cancel_btn);
        search_editText = root.findViewById(R.id.teacher_search_editText);
        search_editText.addTextChangedListener(this);
        imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        searched_list=root.findViewById(R.id.teacher_searched_list);
        //setupRecyclerView(recyclerView);

        //dateInit();
        Log.d(VM_ENUM.TAG,"[DashBoardFragment] 데이터베이스 호출");
        readDataBase();

        //** 프래그먼트 초기 설정
        FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
        FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기
        CalendarFullViewFragment calendarFullViewFragment=new CalendarFullViewFragment();
        tran.replace(R.id.frame, calendarFullViewFragment);
        tran.commit();
        //>>>

        cal_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_click_count++;
                FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                if(view_click_count%2==0){
                    view_click_count=0;

                    //small view mode
                    CalendarFullViewFragment calendarFullViewFragment=new CalendarFullViewFragment();
                    tran.replace(R.id.frame, calendarFullViewFragment);
                    tran.commit();

                }else{

                    if(user_join_date!=null){
                        Log.d(VM_ENUM.TAG,"[TeacherDashBoardFrag] user_join_date 이미 생성완료");
                        //대시보드리스트 프레그먼트로 replace
                        Bundle arguments = new Bundle();
                        arguments.putString(VM_ENUM.IT_ARG_USER_JOIN_DATE,user_join_date);

                        DashboardListFragment dashboardListFragment=new DashboardListFragment();
                        dashboardListFragment.setArguments(arguments);

                        tran.replace(R.id.frame, dashboardListFragment);
                        tran.commit();
                    }else{
                        Log.d(VM_ENUM.TAG,"[TeacherDashBoardFrag] user_join_date 생성 필요");
                        getJoinDate();
                    }


                }
            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            teacher_search_container.setVisibility(teacher_search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

            searched_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            searched_list.setAdapter(filterAdapter);
            }
        });

        search_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacher_search_container.setVisibility(teacher_search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                hideKeyboard();
                search_editText.setText("");
            }
        });
        return root;
    }
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_editText.getWindowToken(),0);
    }

//    private void dateInit() {
//        final long now = System.currentTimeMillis();//현재시간
//        final Date date = new Date(now);//현재날짜
//
//        final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);//현재 지역의 년도
//        final SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.KOREA);
//        final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.KOREA);//현재 몇일
//
//        final String year = yearFormat.format(date);
//        final String month = monthFormat.format(date);
//        final String day = dayFormat.format(date);
//
//        this_year = year;
//        this_month = month;
//        this_day = day;
//
//        //현재 포커싱된 달력뷰(년,월,일) 초기값
//        focusedYear = Integer.parseInt(this_year);
//        focusedMonth = Integer.parseInt(this_month);
//        focusedDay = Integer.parseInt(this_day);
//
//
//        datecheck.setText(this_year + "년 " + this_month + "월 " + this_day + "일 문제 목록");
//
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                //선택한 날짜가 전돨됨
//                //현재 포커싱된 달력뷰(년,월,일) 정보 저장
////                focusedYear = year;
////                focusedMonth = month + 1;
////                focusedDay = dayOfMonth;
//                Log.d(TAG, "선택 -> 포커싱 변경: " + focusedYear + "-" + focusedMonth + "-" + focusedDay);
//
//                datecheck.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일 문제 목록");
//
//                subs = new ArrayList<Pair<VM_Data_Default, Pair<String, String>>>();
//
//                String selectedDate = year + "-";
//
//                if ((month + 1) < 10) {
//                    selectedDate += "0" + (month + 1) + "-";
//                } else {
//                    selectedDate += (month + 1) + "-";
//                }
//
//                if (dayOfMonth < 10) {
//                    selectedDate += "0" + (dayOfMonth);
//                } else {
//                    selectedDate += dayOfMonth;
//                }
//
//                Log.d(TAG, "[클릭이벤트]선택한 날짜: " + selectedDate);
//
//                if (posts != null) {
//                    for (int i = 0; i < posts.size(); i++) {
//                        Log.d(TAG, "[클릭이벤트]포스트 날짜: " + posts.get(i).second.second);
//                        if (posts.get(i).second.second.contains(selectedDate)) {
//                            ///subs.add(Pair.create(posts.get(i),Pair.create(ids.get(i),dates.get(i))));
//                            subs.add(Pair.create(posts.get(i).first,
//                                    Pair.create(posts.get(i).second.first, posts.get(i).second.second)));
//                        }
//                    }
//                }
//
//                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane, parent));
//
//
//            }
//        });
//    }
//
//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        ///recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
//    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        filterAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    public void getJoinDate(){
        //** 최초 회원가입 날짜

        FirebaseDatabase.getInstance().getReference(VM_ENUM.DB_TEACHERS).child(user_id).child(VM_ENUM.DB_INFO).child(VM_ENUM.DB_JOIN_DATE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                Log.d(VM_ENUM.TAG,"[TeacherDashBoardFrag] 최초 회원가입 날짜"+dataSnapshot.getValue());

                user_join_date= Objects.requireNonNull(dataSnapshot.getValue()).toString();
                Bundle arguments = new Bundle();
                arguments.putString(VM_ENUM.IT_ARG_USER_JOIN_DATE,user_join_date);

                DashboardListFragment dashboardListFragment=new DashboardListFragment();
                dashboardListFragment.setArguments(arguments);

                tran.replace(R.id.frame, dashboardListFragment);
                tran.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public static class SimpleItemRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//        private final List<Pair<VM_Data_Default, Pair<String, String>>> mValues;
//        private final boolean mTwoPane;
//        private final TeacherHomeActivity mParentActivity;
//
//        SimpleItemRecyclerViewAdapter(List<Pair<VM_Data_Default, Pair<String, String>>> items, boolean twoPane, TeacherHomeActivity parent) {
//            mValues = items;
//            mTwoPane = twoPane;
//            mParentActivity = parent;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_calendar, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
////            기존 코드
//////            holder.mIdView.setText(mValues.get(position).id);
////            holder.mContentView.setText(mValues.get(position).content);
////
////            holder.itemView.setTag(mValues.get(position));
//////            holder.itemView.setOnClickListener(mOnClickListener);
//
//            //>>>> 2020-01-13 삭제
////            if(holder.getAdapterPosition()!=RecyclerView.NO_POSITION){
////                holder.mContentView.setText(mValues.get(holder.getAdapterPosition()).content);
////                holder.itemView.setTag(mValues.get(holder.getAdapterPosition()));
////            }
//            //>>>> 2020-01-13
//
//            holder.mContentView.setText(mValues.get(position).first.getTitle());//내용(post_title)
//            holder.itemView.setTag(mValues.get(position));
//
//            //** Date 에서 시간만 표기 (시:분)
//            String token = mValues.get(position).second.second;
//            holder.mTimeView.setText(token);//시간
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//            final TextView mTimeView;
//            final TextView mContentView;
//
//
//            ViewHolder(View view) {
//                super(view);
//
//                mContentView = (TextView) view.findViewById(R.id.problem_name);
//                mTimeView = (TextView) view.findViewById(R.id.endTime);
//
//                //** 아이템 클릭 이벤트
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int pos = getAdapterPosition();
//
//                        if (pos != RecyclerView.NO_POSITION) {
//                            //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
//                            // post_id 인자
//                            Intent intent = new Intent(mParentActivity, VM_FullViewActivity.class);
//                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, subs.get(pos).second.first);
//                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE, subs.get(pos).first.getTitle());
//                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE, subs.get(pos).first.getGrade());
//                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM, subs.get(pos).first.getProblem());
//                            intent.putExtra(VM_ENUM.IT_ARG_BLOCK,VM_ENUM.IT_ARG_BLOCK); //** dashboard에서는 완료된 항목이 가기 때문에 모든 창을 비활성화
//                            mParentActivity.startActivity(intent);
//                            Toast.makeText(v.getContext(), "확인" + pos, Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//        }
//    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void readDataBase() {

        //** 데이터 읽기
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference(VM_ENUM.DB_TEACHERS)
                .child(user_id)
                .child(VM_ENUM.DB_TEA_POSTS).child(VM_ENUM.DB_TEA_DONE);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CalendarData.dates=new ArrayList<String>();
                CalendarData.posts = new ArrayList<Pair<VM_Data_Default, Pair<String, String>>>();
                String post_id, post_date, post_title, post_grade, post_problem;
                PostCustomData postCustomData;

                //PostCustomData(String p_id,String p_title,String grade,String problem,String time)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    postCustomData=snapshot.getValue(PostCustomData.class);
                    assert postCustomData != null;
                    post_id=postCustomData.getP_id();
                    post_date=postCustomData.getTime();
                    post_title=postCustomData.getTitle();
                    post_grade=postCustomData.getGrade();
                    post_problem=postCustomData.getProblem();
                    CalendarData.dates.add(post_date);
                    CalendarData.posts.add(Pair.create(new VM_Data_Default(post_title,post_grade,post_problem),
                            Pair.create(post_id,post_date)));
                    Log.d(TAG, "[TeacherDashBoard]ValueEventListener : " + snapshot);

                }


                filterAdapter = new FilterAdapter(getContext(),CalendarData.posts);

                //로딩창 숨기기
                cal_loading_back.setVisibility(View.INVISIBLE);
                cal_loading_bar.setVisibility(View.INVISIBLE);



//                if (getFragmentManager() != null) {
//                    Log.d(VM_ENUM.TAG,"[TeacherDashBoardFragment] visible");
//                    //대시보드리스트 프레그먼트로 replace
//                    FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
//                    FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기
//                    CalendarFullViewFragment calendarFullViewFragment=new CalendarFullViewFragment();
//                    tran.replace(R.id.frame, calendarFullViewFragment);
//                    tran.commit();
//
//                }else{
//                    Log.d(VM_ENUM.TAG,"[TeacherDashBoardFragment] invisible");
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }
}
