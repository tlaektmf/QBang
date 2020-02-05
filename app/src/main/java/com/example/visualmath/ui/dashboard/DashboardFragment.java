package com.example.visualmath.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.adapter.FilterAdapter;
import com.example.visualmath.activity.HomeActivity;
import com.example.visualmath.calendarListAdapater;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.data.VM_Data_CHAT;
import com.example.visualmath.fragment.CalendarFullViewFragment;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.example.visualmath.R;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DashboardFragment extends Fragment implements TextWatcher {

    private DashboardViewModel dashboardViewModel;


    private Button cal_mode_btn;

    //    검색창
    private Button search_btn;
    private Button search_cancel_btn;

    private ConstraintLayout search_container;
    private InputMethodManager imm;
    private EditText search_editText;
    //검색 목록
    private RecyclerView searched_list;
    private  FilterAdapter filterAdapter;

    //lhj_0
//    로딩창
    private ProgressBar cal_loading_bar;
    private View cal_loading_back;
    //lhj_0

//    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private String user_join_date;
    private String user_id;
    private String user_type;

//    public static List<Pair<VM_Data_Default,Pair<String,String>>> subs; //포스트 데이터 일부 리스트 post/id/date
    public static List<Pair<VM_Data_Default,Pair<String,String>>> posts; //포스트 데이터 전체 리스트 post/id/date
    public static List <String> dates;
    private HomeActivity parent;
    private String TAG=VM_ENUM.TAG;

    private  int view_click_count;

    public DashboardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent=(HomeActivity)getActivity();

        //** 유저 정보 설정
        user_join_date=null;
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        user_id = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가
        DashboardFragment.dates=new ArrayList<String>();
        if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
            //선생님
            user_type=VM_ENUM.TEACHER;
        }else{
            user_type=VM_ENUM.STUDENT;
        }

        //Fragment 초기화


    }



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);



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
        //검색 목록 리사이클러뷰
        searched_list = root.findViewById(R.id.searched_list);

        Log.d(VM_ENUM.TAG,"[DashBoardFragment] 데이터베이스 호출");
        readDataBase();

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
                       Log.d(VM_ENUM.TAG,"[DashBoardFrag] user_join_date 이미 생성완료");
                       //대시보드리스트 프레그먼트로 replace
                       Bundle arguments = new Bundle();
                       arguments.putString(VM_ENUM.IT_ARG_USER_JOIN_DATE,user_join_date);

                       DashboardListFragment dashboardListFragment=new DashboardListFragment();
                       dashboardListFragment.setArguments(arguments);

                       tran.replace(R.id.frame, dashboardListFragment);
                       tran.commit();
                   }else{
                       Log.d(VM_ENUM.TAG,"[DashBoardFrag] user_join_date 생성 필요");
                       getJoinDate();
                   }


               }

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_container.setVisibility(search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                //검색 어댑터 생성
                Log.d("filter","posts 사이즈 : "+posts.size());
                filterAdapter = new FilterAdapter(getContext(),posts);
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
        return root;
    }

//    키보드 숨기는 함수
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_editText.getWindowToken(),0);
    }

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
        DatabaseReference ref;
        if (user_type.equals(VM_ENUM.STUDENT)) {
            ref= FirebaseDatabase.getInstance().getReference(VM_ENUM.DB_STUDENTS);
        }else {
            ref= FirebaseDatabase.getInstance().getReference(VM_ENUM.DB_TEACHERS);
        }

        ref.child(user_id).child(VM_ENUM.DB_INFO).child(VM_ENUM.DB_JOIN_DATE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                Log.d(VM_ENUM.TAG,"[DashBoardFrag] 최초 회원가입 날짜"+dataSnapshot.getValue());

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
    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void readDataBase(){


        //** 데이터 읽기
        firebaseDatabase= FirebaseDatabase.getInstance();
        if (user_type.equals(VM_ENUM.STUDENT)) {

            reference=firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
            .child(user_id)
            .child(VM_ENUM.DB_STU_POSTS).child(VM_ENUM.DB_STU_DONE);
        }else if(user_type.equals(VM_ENUM.TEACHER)){
            reference=firebaseDatabase.getReference(VM_ENUM.DB_TEACHERS)
            .child(user_id).child(VM_ENUM.DB_TEA_POSTS).child(VM_ENUM.DB_TEA_DONE);
        }

        Log.d(TAG, "[DashboardFragment] 데이터 접근 : " +user_id+","+user_type);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                posts=new ArrayList<Pair<VM_Data_Default,Pair<String,String>>>();
                String post_id,post_date,post_title,post_grade,post_problem;
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
                    dates.add(post_date);
                    posts.add(Pair.create(new VM_Data_Default(post_title,post_grade,post_problem),
                            Pair.create(post_id,post_date)));

                    Log.d(TAG, "[Student DashBoard]ValueEventListener : " + snapshot);

                }

                //** 처음 DashBoard 세팅

                filterAdapter = new FilterAdapter(getContext(),posts);

                //lhj_3
                cal_loading_back.setVisibility(View.INVISIBLE);
                cal_loading_bar.setVisibility(View.INVISIBLE);
                //lhj_3

                FragmentManager fm = (parent).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                //대시보드리스트 프레그먼트로 replace
                CalendarFullViewFragment calendarFullViewFragment=new CalendarFullViewFragment();
                tran.replace(R.id.frame, calendarFullViewFragment);
                tran.commit();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(parent,"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });


    }

}