package com.example.visualmath.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.DashboardListFragment;
import com.example.visualmath.HomeActivity;
import com.example.visualmath.ItemDetailFragment;
import com.example.visualmath.ItemListActivity;
import com.example.visualmath.MainActivity;
import com.example.visualmath.R;
import com.example.visualmath.TabFragment;
import com.example.visualmath.TeacherItemDetailFragment;
import com.example.visualmath.VM_FullViewActivity;
import com.example.visualmath.dummy.DummyContent;
import com.example.visualmath.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private boolean mTwoPane;

    private String this_year;
    private String this_month;
    private String this_day;
    View root;
    private TextView datecheck;
    private CalendarView calendar;
    private RecyclerView recyclerView;
    private Button cal_mode_btn;

    public DashboardFragment(){

    }


    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
         //root = inflater.inflate(R.layout.fragment_dashboard, container, false);
       ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard,container,false);

        recyclerView = root.findViewById(R.id.calendar_recyclerview);

        datecheck=root.findViewById(R.id.datecheck);
        calendar=root.findViewById(R.id.calendar);
        dateInit();

//        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        //캘린더 모드 변경
        cal_mode_btn = root.findViewById(R.id.cal_mode_change);



        cal_mode_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //datecheck.setVisibility(datecheck.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                //recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                FragmentManager fm = ((HomeActivity)getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기

                //대시보드리스트 프레그먼트로 replace
                tran.replace(R.id.nav_host_fragment,new DashboardListFragment());
                tran.commit();
            }
        });

        return root;
    }


    private void dateInit(){
        final long now = System.currentTimeMillis();//현재시간
        final Date date = new Date(now);//현재날짜

        final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());//현재 지역의 년도
        final SimpleDateFormat monthFormat = new SimpleDateFormat("MM",Locale.getDefault());
        final SimpleDateFormat dayFormat = new SimpleDateFormat("dd",Locale.getDefault());//현재 몇일

        final String year = yearFormat.format(date);
        final String month = monthFormat.format(date);
        final String day = dayFormat.format(date);

        this_year = year;
        this_month = month;
        this_day = day;

//        final TextView datecheck = getView().findViewById(R.id.datecheck);
        datecheck.setText(this_year+"년 "+this_month+"월 "+this_day+"일 문제 목록");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //선택한 날짜가 전돨됨

                this_year = Integer.toString(year);
                datecheck.setText(year+"년 "+month+"월 "+dayOfMonth+"일 문제 목록");
            }
        });
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS, mTwoPane));
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>{

//        private final ItemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final boolean mTwoPane;

//        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
////                Toast.makeText(view.getContext(),"목록 선택",Toast.LENGTH_LONG).show();
//
////                화면전환
//
//
//            }
//        };

        SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items,boolean twoPane) {
            mValues = items;
//            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_calendar, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
//            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            //final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                //mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.problem_name);

                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){
                            Toast.makeText(v.getContext(),"확인"+pos,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }


}