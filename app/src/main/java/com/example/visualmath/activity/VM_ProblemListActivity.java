package com.example.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.example.visualmath.fragment.ItemProblemDetailFragment;
import com.google.firebase.database.ChildEventListener;
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
    public static String TAG = VM_ENUM.TAG;
    private ProgressBar list_loading_bar;

    Button btnElement;
    Button btnMid;
    Button btnHigh;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private static List<PostCustomData> unmatched_element; //포스트 데이터 unmatched 리스트 /id/title/video or text
    private static List<PostCustomData> unmatched_mid; //포스트 데이터 unmatched 리스트 /id/title/video or text
    private static List<PostCustomData> unmatched_high; //포스트 데이터 unmatched 리스트 /id/title/video or text
    private String nowGrade;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_list);

        init();

    }

    public void init() {
        recyclerView = findViewById(R.id.item_problem_list);
        layout = (ViewGroup) findViewById(R.id.item_problem_detail_container);
        assert recyclerView != null;
        list_loading_bar = findViewById(R.id.list_loading_bar);
        btnElement = findViewById(R.id.ib_element);
        btnMid = findViewById(R.id.ib_mid);
        btnHigh = findViewById(R.id.ib_high);

        unmatched_element = new ArrayList<>();
        unmatched_mid =  new ArrayList<>();
        unmatched_high =  new ArrayList<>();

        initDatabaseListener();



        //** 인텐트 확인
        String afterMatchSuccess = getIntent().getStringExtra(VM_ENUM.IT_MATCH_SUCCESS);


        if (afterMatchSuccess == null) {
            Log.d(VM_ENUM.TAG, "[선생님 문제선택 뷰] : Match 성공 이후, 넘어온 게 아님 : 그냥 문제선택뷰에 들어가는 경우(초기 상태)");
            btnElement.setSelected(true);
            btnMid.setSelected(false);
            btnHigh.setSelected(false);

            nowGrade = VM_ENUM.GRADE_ELEMENT;

            Log.d(VM_ENUM.TAG, "[TeacherProblemSelect],onCreate | setUnmatched 호출");
            setUnmatchedData(VM_ENUM.GRADE_ELEMENT);
        } else if (afterMatchSuccess.equals(VM_ENUM.GRADE_ELEMENT)) {
            Log.d(VM_ENUM.TAG, "[Match 성공 이후, 넘어옴 GRADE_ELEMENT]");

            btnElement.setSelected(true);
            btnMid.setSelected(false);
            btnHigh.setSelected(false);

            nowGrade = VM_ENUM.GRADE_ELEMENT;

            Log.d(VM_ENUM.TAG, "[TeacherProblemSelect],onCreate | setUnmatched 호출");
            setUnmatchedData(VM_ENUM.GRADE_ELEMENT);
        } else if (afterMatchSuccess.equals(VM_ENUM.GRADE_MID)) {
            Log.d(VM_ENUM.TAG, "[Match 성공 이후, 넘어옴 GRADE_MID]");

            btnElement.setSelected(false);
            btnMid.setSelected(true);
            btnHigh.setSelected(false);

            nowGrade = VM_ENUM.GRADE_MID;

            Log.d(VM_ENUM.TAG, "[TeacherProblemSelect],onCreate | setUnmatched 호출");
            setUnmatchedData(VM_ENUM.GRADE_MID);
        } else if (afterMatchSuccess.equals(VM_ENUM.GRADE_HIGH)) {
            Log.d(VM_ENUM.TAG, "[Match 성공 이후, 넘어옴 GRADE_HIGH]");

            btnElement.setSelected(false);
            btnMid.setSelected(false);
            btnHigh.setSelected(true);

            nowGrade = VM_ENUM.GRADE_HIGH;

            Log.d(VM_ENUM.TAG, "[TeacherProblemSelect],onCreate | setUnmatched 호출");
            setUnmatchedData(VM_ENUM.GRADE_HIGH);
        }


    }

    private void setData(@NonNull RecyclerView recyclerView, List<PostCustomData> items) {
        recyclerView.setAdapter(new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter(this, items));

        list_loading_bar.setVisibility(View.INVISIBLE);
    }


    /**
     * class SimpleItemRecyclerViewAdapter
     */
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final VM_ProblemListActivity mParentActivity;
        private final List<PostCustomData> mValues;

        /**
         * 문제 상세뷰로 이동
         */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostCustomData item = (PostCustomData) view.getTag();

                Log.d(VM_ENUM.TAG, "[아이템 선택]" + item.getP_id() + "," + item.getSolveWay());
                //** 프래그먼트의 아이템 클릭 시, ProblemDetail 전환
                ///searchData(pos);
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getP_id());
                arguments.putString(VM_ENUM.DB_SOLVE_WAY, item.getSolveWay());

                arguments.putString(VM_ENUM.IT_FROM_UNMATCHED, VM_ENUM.IT_FROM_UNMATCHED);//매치 이전 문제이므로 matchset 찾지 않음
                arguments.putString(VM_ENUM.IT_ARG_BLOCK, VM_ENUM.IT_ARG_BLOCK);//채팅창을 막음

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
        public VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_problem_list_content, parent, false);
            return new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            if (mValues.get(position).getSolveWay().equals(VM_ENUM.VIDEO)) {
                holder.mtitleView.setText("[영상 질문]");//video or text
            } else if (mValues.get(position).getSolveWay().equals(VM_ENUM.TEXT)) {
                holder.mtitleView.setText("[텍스트 질문]");//video or text
            }

            holder.mDetailView.setText(mValues.get(position).getTitle());//title
            holder.mDateView.setText(mValues.get(position).getUpLoadDate());//title

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

        }

        @Override
        public int getItemCount() {
//            if(mValues.size()==0){
//                return 0;
//            }
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

            }
        }
    }

    //**
    public void activateList(View view) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.choose_drawer_menu);
        View clickview = findViewById(R.id.teacher_clickview);
        if (layout.getVisibility() == View.VISIBLE) {
            //현재 뷰가 보이면
            layout.setVisibility(View.GONE);
            clickview.setVisibility(View.INVISIBLE);
        } else {
            //뷰가 보이지 않으면
            layout.setVisibility(View.VISIBLE);
            clickview.setVisibility(View.VISIBLE);
            clickview.setClickable(true);
        }
    }


    public void showElementary(View view) {
        //** 초등

        nowGrade = VM_ENUM.GRADE_ELEMENT;
        btnElement.setSelected(true);
        btnMid.setSelected(false);
        btnHigh.setSelected(false);

//** 데이터베이스 트랜젝션
        if (unmatched_element != null) {
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
            setData((RecyclerView) recyclerView, unmatched_element);
            Log.d(VM_ENUM.TAG, "[선생님 문제 선택] : 데이터 셋팅은 되어있는 상태 ");
        } else {
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG, "[선생님 문제 선택] : 초기 셋팅 필요 | setUnmatchedData 호출");
            setUnmatchedData(VM_ENUM.GRADE_ELEMENT);
        }

    }

    public void showMid(View view) {

        btnElement.setSelected(false);
        btnMid.setSelected(true);
        btnHigh.setSelected(false);
        nowGrade = VM_ENUM.GRADE_MID;

        //** 데이터베이스 트랜젝션
        if (unmatched_mid != null) {
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
            setData((RecyclerView) recyclerView, unmatched_mid);
            Log.d(VM_ENUM.TAG, "[선생님 문제 선택],데이터 셋팅은 되어있는 상태 ");
        } else {
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG, "[선생님 문제 선택],초기 셋팅 필요 | setUnmatchedData 호출");
            setUnmatchedData(VM_ENUM.GRADE_MID);
        }

    }

    public void showHigh(View view) {
        nowGrade = VM_ENUM.GRADE_HIGH;

        btnElement.setSelected(false);
        btnMid.setSelected(false);
        btnHigh.setSelected(true);

//** 데이터베이스 트랜젝션
        if (unmatched_high != null) {
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
            setData((RecyclerView) recyclerView, unmatched_high);
            Log.d(VM_ENUM.TAG, "[선생님 문제 선택],데이터 셋팅은 되어있는 상태 ");
        } else {
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG, "[선생님 문제 선택],초기 셋팅 필요 | setUnmatchedData 호출");
            setUnmatchedData(VM_ENUM.GRADE_HIGH);
        }
    }


    public void initDatabaseListener() { //** 모든 데이터를 초기화시킴 -> childAddedListener 이용
        firebaseDatabase = FirebaseDatabase.getInstance();

        reference = firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED);

        childEventListener=new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d(TAG, "[선생님 문제 선택뷰] ValueEventListener : " + dataSnapshot);

                String post_id = dataSnapshot.getKey();
                String post_date = Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue()).toString();
                String post_title = Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_TITLE).getValue()).toString();
                String post_solveWay = Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_SOLVE_WAY).getValue()).toString();
                String post_grade= Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_GRADE).getValue()).toString();



                //PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate)

                if (post_grade.equals(VM_ENUM.GRADE_ELEMENT)) {
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_ELEMENT 데이터 생성 ");
                    unmatched_element.add(new PostCustomData(post_id,post_title,post_solveWay,post_date) );
                } else if (post_grade.equals(VM_ENUM.GRADE_MID)) {
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_MID 데이터 생성 ");
                    unmatched_mid.add(new PostCustomData(post_id,post_title,post_solveWay,post_date) );
                } else if (post_grade.equals(VM_ENUM.GRADE_HIGH)) {
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_HIGH 데이터 생성 ");
                    unmatched_high.add(new PostCustomData(post_id,post_title,post_solveWay,post_date) );
                }

                //** 현재 활성화 되어있는 버튼(초, 중, 고)에 따라서 리사이클러뷰에 셋팅을 할 지 결정함.
                if (nowGrade.equals(VM_ENUM.GRADE_ELEMENT)) {
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_ELEMENT 데이터 셋팅 ");
                    setUnmatchedData(VM_ENUM.GRADE_ELEMENT);

                } else if (nowGrade.equals(VM_ENUM.GRADE_MID)) {
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_MID 데이터 셋팅 ");
                    setUnmatchedData(VM_ENUM.GRADE_MID);

                } else if (nowGrade.equals(VM_ENUM.GRADE_HIGH)) {
                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_HIGH 데이터 셋팅 ");
                    setUnmatchedData(VM_ENUM.GRADE_HIGH);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.w(TAG, "Failed to read value :onChildChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.w(TAG, "Failed to read value : onChildChanged");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.w(TAG, "Failed to read value : onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getBaseContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        };

        //** 리스너 동작
        Log.w(TAG, "[선생님 문제선택 뷰] : 리스너 부착");
        reference.addChildEventListener(childEventListener);
    }


    public void setUnmatchedData(final String grade) {

        //** 변경된 setUnmatchedData 함수

        if (grade.equals(VM_ENUM.GRADE_ELEMENT)) {
            Log.d(TAG, "[선생님 문제 선택뷰] GRADE_ELEMENT 데이터 셋팅 ");
            setData((RecyclerView) recyclerView, unmatched_element);

        } else if (grade.equals(VM_ENUM.GRADE_MID)) {
            Log.d(TAG, "[선생님 문제 선택뷰] GRADE_MID 데이터 셋팅 ");
            setData((RecyclerView) recyclerView, unmatched_mid);
        } else if (grade.equals(VM_ENUM.GRADE_HIGH)) {
            Log.d(TAG, "[선생님 문제 선택뷰] GRADE_HIGH 데이터 셋팅 ");
            setData((RecyclerView) recyclerView, unmatched_high);

        }


        //** 기존 setUnmatchedData 함수
//
//        firebaseDatabase=FirebaseDatabase.getInstance();
//
//        reference=firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED);
//
//        reference.orderByChild(VM_ENUM.DB_GRADE).equalTo(grade).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                List<PostCustomData> unmatched=new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//
//                    String post_id=snapshot.getKey();
//                    String post_date=snapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue().toString();
//                    String post_title=snapshot.child(VM_ENUM.DB_TITLE).getValue().toString();
//                    String post_solveWay=snapshot.child(VM_ENUM.DB_SOLVE_WAY).getValue().toString();
//
////                    PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate)
//                    unmatched.add(new PostCustomData(post_id,post_title,post_solveWay,post_date) );
//                    Log.d(TAG, "[선생님 문제 선택뷰] ValueEventListener : " +snapshot.getValue() );
//
//                }
//
//                if(grade.equals(VM_ENUM.GRADE_ELEMENT)){
//                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_ELEMENT 데이터 생성 "  );
//                    unmatched_element=unmatched;
//                }else if(grade.equals(VM_ENUM.GRADE_MID)){
//                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_MID 데이터 생성 "  );
//                    unmatched_mid=unmatched;
//                }else if(grade.equals(VM_ENUM.GRADE_HIGH)){
//                    Log.d(TAG, "[선생님 문제 선택뷰] GRADE_HIGH 데이터 생성 "  );
//                    unmatched_high=unmatched;
//                }
//
//               setData((RecyclerView) recyclerView,unmatched);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
//                Log.w(TAG, "Failed to read value", databaseError.toException());
//            }
//        });
//
//    }

    }

    }
