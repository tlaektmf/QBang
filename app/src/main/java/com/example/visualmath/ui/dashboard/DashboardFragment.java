package com.example.visualmath.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private boolean mTwoPane;

    private String this_year;
    private String this_month;
    private String this_day;

    private TextView datecheck;
    private CalendarView calendar;
    private RecyclerView recyclerView;
    private Button cal_mode_btn;

    //    검색창
    private Button search_btn;
    private Button search_cancel_btn;
    private ConstraintLayout search_input_lay;

    //  DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    public List<VM_Data_Default> subs; //포스트 데이터 일부 리스트
    public static List<VM_Data_Default> posts; //포스트 데이터 리스트
    public static List<String> ids;//포스트 아이디를 따로 관리
    public static List<String> dates;//포스트 완료 날짜를 따로 관리

    public static String TAG="DashboardFrag";


    public DashboardFragment() {

    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = root.findViewById(R.id.calendar_recyclerview);
        datecheck = root.findViewById(R.id.datecheck);
        calendar = root.findViewById(R.id.calendar);

        //**검색창
        search_input_lay = root.findViewById(R.id.search_input_lay);

        //**캘린더 모드 변경
        cal_mode_btn = root.findViewById(R.id.cal_mode_change);

        //검색 버튼
        search_btn = root.findViewById(R.id.cal_search_btn);

        //검색 취소 버튼
        search_cancel_btn = root.findViewById(R.id.serach_cancel_btn);

        dateInit();
        readDataBase();


        setupRecyclerView(recyclerView);



        cal_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = ((HomeActivity) getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                //대시보드리스트 프레그먼트로 replace
                tran.replace(R.id.nav_host_fragment, new DashboardListFragment());
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


        datecheck.setText(this_year + "년 " + this_month + "월 " + this_day + "일 문제 목록");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //선택한 날짜가 전돨됨

                this_year = Integer.toString(year);
                datecheck.setText(year + "년 " + month + "월 " + dayOfMonth + "일 문제 목록");

                subs=new ArrayList<VM_Data_Default>();

                String selectedDate=this_year+"-"+this_month+"-"+this_day;
                Log.d(TAG,"선택한 날짜: "+selectedDate);

                if(posts!=null){
                    for(int i=0;i<posts.size();i++){
                        Log.d(TAG,"포스트 날짜: "+dates.get(i));
                        if(dates.get(i).contains(selectedDate)){
                            subs.add(posts.get(i));
                        }
                    }
                }

                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(subs, mTwoPane,(HomeActivity)getActivity()));
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(posts, mTwoPane,(HomeActivity)getActivity()));
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<VM_Data_Default> mValues;
        private final boolean mTwoPane;
        private final HomeActivity mParentActivity;

        SimpleItemRecyclerViewAdapter(List<VM_Data_Default> items, boolean twoPane,HomeActivity parent) {
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

            holder.mContentView.setText(mValues.get(position).getTitle());
            holder.itemView.setTag(mValues.get(position));

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mContentView;

            ViewHolder(View view) {
                super(view);

                mContentView = (TextView) view.findViewById(R.id.problem_name);

                //** 아이템 클릭 이벤트
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
                            // post_id 인자
                            Intent intent = new Intent(mParentActivity, VM_FullViewActivity.class);
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, ids.get(pos));
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,posts.get(pos).getTitle());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,posts.get(pos).getGrade());
                            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,posts.get(pos).getProblem());

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
        posts=new ArrayList<VM_Data_Default>();
        ids=new ArrayList<String>();
        dates=new ArrayList<String>();


        firebaseDatabase= FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference("STUDENTS");
        reference=reference.child("user_name")
                .child("posts").child("done");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String post_id=snapshot.getKey();
                    String post_date=snapshot.child("time").getValue().toString();
                    String post_title=snapshot.child("title").getValue().toString();
                    String post_grade=snapshot.child("grade").getValue().toString();
                    String post_problem=snapshot.child("problem").getValue().toString();

                    posts.add(new VM_Data_Default(post_title,post_grade,post_problem));
                    ids.add(post_id);
                    dates.add(post_date);

                    Log.d(TAG, "ValueEventListener : " +post_date );

                }
                setupRecyclerView((RecyclerView) recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });
    }
}