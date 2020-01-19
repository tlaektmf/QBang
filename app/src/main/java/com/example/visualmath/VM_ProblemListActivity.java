package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.dummy.AlarmItem;
import com.example.visualmath.dummy.DummyContent;
import com.example.visualmath.dummy.PostCustomData;
import com.example.visualmath.dummy.TestContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VM_ProblemListActivity extends AppCompatActivity {
    View recyclerView;
    ViewGroup layout;
    public static String TAG=VM_ENUM.TAG;
    private ProgressBar list_loading_bar;

    Button btnElement ;
    Button btnMid ;
    Button btnHigh;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private static List<PostCustomData> unmatched_element; //포스트 데이터 unmatched 리스트 /id/title/video or text
    private static List<PostCustomData> unmatched_mid; //포스트 데이터 unmatched 리스트 /id/title/video or text
    private static List<PostCustomData> unmatched_high; //포스트 데이터 unmatched 리스트 /id/title/video or text
    private String nowGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_list);

        init();

    }

    public void init(){
        recyclerView = findViewById(R.id.item_problem_list);
        layout = (ViewGroup) findViewById(R.id.item_problem_detail_container);
        assert recyclerView != null;

        list_loading_bar = findViewById(R.id.list_loading_bar);
         btnElement = findViewById(R.id.ib_element);
         btnMid = findViewById(R.id.ib_mid);
         btnHigh = findViewById(R.id.ib_high);


        unmatched_element=null;
        unmatched_mid=null;
        unmatched_high=null;

        Log.d(VM_ENUM.TAG,"[TeacherProblemSelect],onCreate | setUnmatched 호출");
        setUnmatchedData(VM_ENUM.GRADE_ELEMENT);
        nowGrade=VM_ENUM.GRADE_ELEMENT;

    }

    private void setData(@NonNull RecyclerView recyclerView,List<PostCustomData> items) {
        recyclerView.setAdapter(new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter(this, items));

        list_loading_bar.setVisibility(View.INVISIBLE);
    }


    /**
     * class SimpleItemRecyclerViewAdapter
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final VM_ProblemListActivity mParentActivity;
        private final List<PostCustomData> mValues;

        /**
         * 문제 상세뷰로 이동
         */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestContent.TestItem item = (TestContent.TestItem) view.getTag();

                Bundle arguments = new Bundle();
                arguments.putString(ItemProblemDetailFragment.ARG_ITEM_ID, item.getId());
                arguments.putString(ItemProblemDetailFragment.ARG_ITEM_CONTENT, item.getContent());
                arguments.putString(ItemProblemDetailFragment.ARG_ITEM_DETAIL, item.getDetails());
                ItemProblemDetailFragment fragment = new ItemProblemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_problem_detail_container, fragment)
                        .commit();
            }
        };

        SimpleItemRecyclerViewAdapter(VM_ProblemListActivity parent,
                                      List<PostCustomData> items) {
            mValues = items;
            mParentActivity = parent;

        }

        @Override
        public VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_problem_list_content, parent, false);
            return new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            if(mValues.get(position).getSolveWay().equals(VM_ENUM.VIDEO)){
                holder.mtitleView.setText("[영상 질문]");//video or text
            }else if(mValues.get(position).getSolveWay().equals(VM_ENUM.TEXT)){
                holder.mtitleView.setText("[텍스트 질문]");//video or text
            }

            holder.mDetailView.setText(mValues.get(position).getP_title());//title
            holder.mDateView.setText(mValues.get(position).getUpLoadDate());//title

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mtitleView;
            final TextView mDetailView;
            final TextView mDateView;

            ViewHolder(View view) {
                super(view);
                mtitleView = (TextView) view.findViewById(R.id.problem_content_title);
                mDetailView = (TextView) view.findViewById(R.id.problem_content_detail);
                mDateView = (TextView) view.findViewById(R.id.problem_content_time);

                //** 아이템 클릭 이벤트
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            //** 프래그먼트의 아이템 클릭 시, ProblemDetail 전환
                            ///searchData(pos);
                        }
                    }
                });


            }
        }
    }

    //**
    public void activateList(View view) {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.choose_drawer_menu);
        View clickview = findViewById(R.id.teacher_clickview);
        if(layout.getVisibility()==View.VISIBLE){
            //현재 뷰가 보이면
            layout.setVisibility(View.GONE);
            clickview.setVisibility(View.INVISIBLE);
        }else{
            //뷰가 보이지 않으면
            layout.setVisibility(View.VISIBLE);
            clickview.setVisibility(View.VISIBLE);
            clickview.setClickable(true);
        }
    }




    public void showElementary(View view) {
        //** 초등

        nowGrade=VM_ENUM.GRADE_ELEMENT;
        btnElement.setSelected(true);
        btnMid.setSelected(false);
        btnHigh.setSelected(false);

//** 데이터베이스 트랜젝션
        if(unmatched_element!=null){
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
           setData((RecyclerView) recyclerView,unmatched_element);
            Log.d(VM_ENUM.TAG,"[선생님 문제 선택],데이터 셋팅은 되어있는 상태 ");
        }else{
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG,"[선생님 문제 선택],초기 셋팅 필요 | setUnmatchedData 호출");
            setUnmatchedData(VM_ENUM.GRADE_ELEMENT);
        }

    }

    public void showMid(View view) {

        btnElement.setSelected(false);
        btnMid.setSelected(true);
        btnHigh.setSelected(false);
        nowGrade=VM_ENUM.GRADE_MID;

        //** 데이터베이스 트랜젝션
        if(unmatched_mid!=null){
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
            setData((RecyclerView) recyclerView,unmatched_mid);
            Log.d(VM_ENUM.TAG,"[선생님 문제 선택],데이터 셋팅은 되어있는 상태 ");
        }else{
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG,"[선생님 문제 선택],초기 셋팅 필요 | setUnmatchedData 호출");
            setUnmatchedData(VM_ENUM.GRADE_MID);
        }

    }

    public void showHigh(View view) {
        nowGrade=VM_ENUM.GRADE_HIGH;
        btnElement.setSelected(false);
        btnMid.setSelected(false);
        btnHigh.setSelected(true);

//** 데이터베이스 트랜젝션
        if(unmatched_high!=null){
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
            setData((RecyclerView) recyclerView,unmatched_high);
            Log.d(VM_ENUM.TAG,"[선생님 문제 선택],데이터 셋팅은 되어있는 상태 ");
        }else{
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG,"[선생님 문제 선택],초기 셋팅 필요 | setUnmatchedData 호출");
            setUnmatchedData(VM_ENUM.GRADE_HIGH);
        }
    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void setUnmatchedData(final String grade){


        firebaseDatabase=FirebaseDatabase.getInstance();

        reference=firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED);

        reference.orderByChild(VM_ENUM.DB_GRADE).equalTo(grade).addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<PostCustomData> unmatched=new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    String post_id=snapshot.getKey();
                    String post_date=snapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue().toString();
                    String post_title=snapshot.child(VM_ENUM.DB_TITLE).getValue().toString();
                    String post_solveWay=snapshot.child(VM_ENUM.DB_SOLVE_WAY).getValue().toString();

//                    PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate)
                    unmatched.add(new PostCustomData(post_id,post_title,post_solveWay,post_date) );
                    Log.d(TAG, "[선생님 문제 선택뷰] ValueEventListener : " +snapshot.getValue() );

                }

                if(grade.equals(VM_ENUM.GRADE_ELEMENT)){
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_ELEMENT 데이터 생성 "  );
                    unmatched_element=unmatched;
                }else if(grade.equals(VM_ENUM.GRADE_MID)){
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_MID 데이터 생성 "  );
                    unmatched_mid=unmatched;
                }else if(grade.equals(VM_ENUM.GRADE_HIGH)){
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_HIGH 데이터 생성 "  );
                    unmatched_high=unmatched;
                }

               setData((RecyclerView) recyclerView,unmatched);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

    public void searchData(int position){
        String post_id=null;

        if(nowGrade.equals(VM_ENUM.GRADE_ELEMENT)){//"현재상태: 초등"
            post_id=unmatched_element.get(position).getP_id();
            reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS)
                    .child(post_id)
                    .child(VM_ENUM.DB_DATA_DEFAULT);

        }else if(nowGrade.equals(VM_ENUM.GRADE_MID)){//"현재 상태: 중등"
            post_id=unmatched_mid.get(position).getP_id();
            reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS)
                    .child(post_id)
                    .child(VM_ENUM.DB_DATA_DEFAULT);
        }else if(nowGrade.equals(VM_ENUM.GRADE_HIGH)){//"현재 상태: 고등"
            post_id=unmatched_high.get(position).getP_id();
            reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS)
                    .child(post_id)
                    .child(VM_ENUM.DB_DATA_DEFAULT);
        }

        final String finalPost_id = post_id;
        Log.d(VM_ENUM.TAG,"[선생님 문제 선택] POSTS 데이터"+finalPost_id+" 접근");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VM_Data_Default vmDataDefault=dataSnapshot.getValue(VM_Data_Default.class);

                assert vmDataDefault != null;
                Log.d(TAG, "[선생님 문제 선택] "+vmDataDefault.getGrade()+", ValueEventListener : " +vmDataDefault.getTitle() );



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }
}
