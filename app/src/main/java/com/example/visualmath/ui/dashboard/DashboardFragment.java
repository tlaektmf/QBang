package com.example.visualmath.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.HomeActivity;
import com.example.visualmath.ItemDetailFragment;
import com.example.visualmath.R;
import com.example.visualmath.VM_Data_Default;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.VM_FullViewActivity;
import com.example.visualmath.dummy.AlarmItem;
import com.example.visualmath.dummy.DummyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment implements TextWatcher {

    private DashboardViewModel dashboardViewModel;
    private boolean mTwoPane;

    private String this_year;
    private String this_month;
    private String this_day;

    private TextView datecheck;
    private  CalendarView calendar;
    private RecyclerView recyclerView;
    private Button cal_mode_btn;

    //    검색창
    private Button search_btn;
    private Button search_cancel_btn;
//    private ConstraintLayout search_input_lay;
    private ConstraintLayout search_container;
    private InputMethodManager imm;
    private EditText search_editText;
    //검색 목록
    private RecyclerView searched_list;

    //lhj_0
//    로딩창
    private ProgressBar cal_loading_bar;
    private View cal_loading_back;
    //lhj_0

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    //** 포커싱된 곳 DB
    ///public static List<VM_Data_Default> subs;
    //** 전체 DB
    ///public static List<VM_Data_Default> posts; //포스트 데이터 리스트
    ///public static List<String> ids;//포스트 아이디를 따로 관리
    ///public static List<String> dates;//포스트 완료 날짜를 따로 관리

    public static List<Pair<VM_Data_Default,Pair<String,String>>> subs; //포스트 데이터 일부 리스트 post/id/date
    public static List<Pair<VM_Data_Default,Pair<String,String>>> posts; //포스트 데이터 전체 리스트 post/id/date

    public HomeActivity parent;
    public static String TAG="DashboardFrag";
    public int focusedYear;
    public int focusedMonth;
    public int focusedDay;

    public DashboardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent=(HomeActivity)getActivity();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = root.findViewById(R.id.calendar_recyclerview);
        datecheck = root.findViewById(R.id.datecheck);
        calendar = root.findViewById(R.id.calendar);

        //lhj_1
        //로딩창
        cal_loading_bar = root.findViewById(R.id.cal_loading_bar);
        cal_loading_back = root.findViewById(R.id.cal_loading_back);
        //lhj_1

        //**검색창
//        search_input_lay = root.findViewById(R.id.search_input_lay);
        search_container = root.findViewById(R.id.search_container);
        //**캘린더 모드 변경
        cal_mode_btn = root.findViewById(R.id.cal_mode_change);
        //검색 버튼
        search_btn = root.findViewById(R.id.cal_search_btn);
        //검색 취소 버튼
        search_cancel_btn = root.findViewById(R.id.serach_cancel_btn);
        search_editText = root.findViewById(R.id.search_editText);
        search_editText.addTextChangedListener(this);
        imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //검색 목록
        searched_list = root.findViewById(R.id.searched_list);

        dateInit();
        readDataBase();

        setupRecyclerView(recyclerView);
        setupRecyclerView(searched_list);

        cal_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                //대시보드리스트 프레그먼트로 replace
                tran.replace(R.id.nav_host_fragment, new DashboardListFragment());
                tran.commit();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_container.setVisibility(search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

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
        return root;
    }

//    키보드 숨기는 함수
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_editText.getWindowToken(),0);
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
        focusedYear=Integer.parseInt(this_year);
        focusedMonth=Integer.parseInt(this_month);
        focusedDay=Integer.parseInt(this_day);

        datecheck.setText(this_year + "년 " + this_month + "월 " + this_day + "일 문제 목록");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //선택한 날짜가 전돨됨


                //현재 포커싱된 달력뷰(년,월,일) 정보 저장
//                focusedYear=year;
//                focusedMonth=month+1;
//                focusedDay=dayOfMonth;
                Log.d(TAG,"선택 -> 포커싱 변경: "+year+"-"+(month+1)+"-"+dayOfMonth);

//                this_year = Integer.toString(year);

                datecheck.setText(year + "년 " + (month+1) + "월 " + dayOfMonth + "일 문제 목록");

                subs=new ArrayList<Pair<VM_Data_Default,Pair<String,String>>>();

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

                if(posts!=null){
                    for(int i=0;i<posts.size();i++){
                        Log.d(TAG,"[클릭이벤트]포스트 날짜: "+posts.get(i).second.second);
                        if(posts.get(i).second.second.contains(selectedDate)){
                            ///subs.add(Pair.create(posts.get(i),Pair.create(ids.get(i),dates.get(i))));
                            subs.add(Pair.create(posts.get(i).first,
                                    Pair.create(posts.get(i).second.first,posts.get(i).second.second)));
                        }
                    }
                }

                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane,parent));

            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       /// recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(posts, mTwoPane,(HomeActivity)getActivity()));

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        ///private final List<VM_Data_Default> mValues;
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
            String token=mValues.get(position).second.second.split(" ")[1];
            holder.mTimeView.setText(token);//시간

        }

        @Override
        public int getItemCount() {
            Log.d("mValue","mValues : "+mValues.size());
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
//                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, ids.get(pos));
//                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,subs.get(pos).getTitle());
//                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,posts.get(pos).getGrade());
//                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,posts.get(pos).getProblem());
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, subs.get(pos).second.first);
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,subs.get(pos).first.getTitle());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,subs.get(pos).first.getGrade());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,subs.get(pos).first.getProblem());
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
    public void readDataBase(){

        //** 데이터 읽기
        firebaseDatabase= FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference("STUDENTS");
        reference=reference.child("user_name")
                .child("posts").child("done");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ///posts=new ArrayList<VM_Data_Default>();
                ///ids=new ArrayList<String>();
                ///dates=new ArrayList<String>();
                ///subs=new ArrayList<VM_Data_Default>();

                posts=new ArrayList<Pair<VM_Data_Default,Pair<String,String>>>();
                String post_id,post_date,post_title,post_grade,post_problem;
                String token;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    post_id=snapshot.getKey();
                    post_date=snapshot.child("time").getValue().toString();
                    post_title=snapshot.child("title").getValue().toString();
                    post_grade=snapshot.child("grade").getValue().toString();
                    post_problem=snapshot.child("problem").getValue().toString();
                    posts.add(Pair.create(new VM_Data_Default(post_title,post_grade,post_problem),
                            Pair.create(post_id,post_date)));

//                    posts.add(new VM_Data_Default(post_title,post_grade,post_problem));
//                    ids.add(post_id);
//                    dates.add(post_date);
                    Log.d(TAG, "ValueEventListener : " +snapshot);

                }

                //** 처음 DashBoard 세팅

                //** 현재 포커싱된 날짜

                ///subs=new ArrayList<VM_Data_Default>();
                subs=new ArrayList<Pair<VM_Data_Default,Pair<String,String>>>();

                String today=this_year+"-"+this_month+"-"+this_day;
                Log.d(TAG,"오늘 날짜: "+today);

                String focusedDate=focusedYear+"-";

                if((focusedMonth)<10){
                    focusedDate+="0"+focusedMonth+"-";
                }else{
                    focusedDate+=focusedMonth+"-";
                }
                if(focusedDay<10){
                    focusedDate+="0"+focusedDay;
                }else{
                    focusedDate+=focusedDay;
                }
                Log.d(TAG,"[ValueListener]현재포지션 날짜:"+focusedDate);


                if(posts!=null){
                    for(int i=0;i<posts.size();i++){
                        Log.d(TAG,"[ValueListener]포스트 날짜: "+posts.get(i).second.second);
                        if(posts.get(i).second.second.contains(focusedDate)){

                            ///subs.add(posts.get(i));
                            subs.add(Pair.create(posts.get(i).first,
                                    Pair.create(posts.get(i).second.first,posts.get(i).second.second)));
                        }
                    }
                }

                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane,parent));

                //lhj_3
                cal_loading_back.setVisibility(View.INVISIBLE);
                cal_loading_bar.setVisibility(View.INVISIBLE);
                //lhj_3

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

///>>>
//        //** 처음 DashBoard 세팀
//        if(posts!=null){
//            for(int i=0;i<posts.size();i++){
//                Log.d(TAG,"포스트 날짜: "+dates.get(i));
//                if(dates.get(i).contains(today)){
//                    subs.add(posts.get(i));
//                }
//            }
//        }
//
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane,parent));
///>>>

    }

}