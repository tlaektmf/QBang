package com.visualstudy.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_ENUM;
import com.visualstudy.visualmath.data.PostCustomData;
import com.visualstudy.visualmath.data.VM_Data_POST;
import com.visualstudy.visualmath.fragment.ItemDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VM_ProblemBoxActivity extends AppCompatActivity {
    public static String TAG= VM_ENUM.TAG;
    public String needToBlock;
    public String fromStudentUnmatched;

    private Button btn_unmatched;
    private Button btn_matched;
    private boolean isUnMatchedClick;

    //문제 목록을 보여줄 리사이클러뷰
    private RecyclerView recycler_view;
    private  ProblemListAdapter mAdapater_unmatched;
    private  ProblemListAdapter mAdapater_matched;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private DatabaseReference reference_posts;

    private static List<PostCustomData> unmatched; //포스트 데이터 unmatched 리스트 /id/title/date
    private static List<PostCustomData> matched; //포스트 데이터 matched 리스트 /id/title/date
    private List<VM_Data_POST> unmatched_posts;
    private List<VM_Data_POST> matched_posts;
    private ValueEventListener singleValueEventListener_unmatched;
    private ValueEventListener singleValueEventListener_matched;

    //    로딩창
    private ProgressBar cal_loading_bar;
    private View cal_loading_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_box);



        Log.d(VM_ENUM.TAG,"[ProblemBoxAct]init 함수 호출");
        init();
    }


    public void init(){
        isUnMatchedClick=true;//** 초기에는 매치 미완료 클릭

        btn_unmatched = findViewById(R.id.btn_unmatched);
        btn_matched = findViewById(R.id.btn_matched);
        recycler_view = findViewById(R.id.problem_recyclerview);

//        레이아웃 매니저 생성 및 지정
        recycler_view.setLayoutManager(new LinearLayoutManager(this));


        //로딩창
        cal_loading_bar = findViewById(R.id.cal_loading_bar);
        cal_loading_back = findViewById(R.id.cal_loading_back);



        needToBlock=null;
        fromStudentUnmatched=null;

//        화면 초기 상태에서는 미완료 목록이 클릭된 상태
        btn_unmatched.setSelected(true);


        //** 데이터 초기화
        unmatched=new ArrayList<PostCustomData>();
        matched=new ArrayList<PostCustomData>();
        unmatched_posts=new ArrayList<>();
        matched_posts=new ArrayList<>();

        //** 어댑터 초기화
        mAdapater_matched = new ProblemListAdapter(matched);
        mAdapater_unmatched = new ProblemListAdapter(unmatched);




        initDatabaseListener();

        getUnmatchedData();
        getMatchedData();
    }

    public void initDatabaseListener(){
        Log.d(VM_ENUM.TAG, "[ProblemBox],onCreate |  singleValueEventListener_unmatched 리스너 생성 ");

        singleValueEventListener_unmatched = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(VM_ENUM.TAG, "[선생님 문제 풀이뷰] unmatched SearchByKey : " + dataSnapshot);

                VM_Data_POST post = dataSnapshot.getChildren().iterator().next().getValue(VM_Data_POST.class);

                unmatched_posts.add(post);

                //** 데이터 셋팅
                unmatched.add(new PostCustomData
                        (post.getP_id()
                                , post.getData_default().getTitle()
                                , post.getSolveWay()
                                , post.getUploadDate()
                                , post.getData_default().getGrade()
                                , post.getData_default().getProblem()
                                , null,VM_ENUM.STUDENT));

                if(isUnMatchedClick){
                    recycler_view.setAdapter(mAdapater_unmatched);
                    ///mAdapater_unmatched.notifyItemInserted(unmatched.size());
                    //mAdapater_matched.notifyDataSetChanged();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(VM_ENUM.TAG, "[선생님 문제 풀이뷰] DatabaseError : ", databaseError.toException());

            }
        };

        Log.d(VM_ENUM.TAG, "[ProblemBox],onCreate |  singleValueEventListener_matched 리스너 생성 ");

        singleValueEventListener_matched = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(VM_ENUM.TAG, "[선생님 문제 풀이뷰] matched SearchByKey : " + dataSnapshot);

                VM_Data_POST post = dataSnapshot.getChildren().iterator().next().getValue(VM_Data_POST.class);

                matched_posts.add(post);

                //** 데이터 셋팅
                matched.add(new PostCustomData
                        (post.getP_id()
                                , post.getData_default().getTitle()
                                , post.getSolveWay()
                                , post.getUploadDate()
                                , post.getData_default().getGrade()
                                , post.getData_default().getProblem()
                                , post.getMatchSet_teacher(),VM_ENUM.STUDENT));

                if(!isUnMatchedClick){
                    recycler_view.setAdapter(mAdapater_matched);
                    ///mAdapater_matched.notifyItemInserted(matched.size());
                    ///mAdapater_matched.notifyDataSetChanged();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(VM_ENUM.TAG, "[선생님 문제 풀이뷰] DatabaseError : ", databaseError.toException());

            }
        };

    }

    //매치 미완료 목록 보여주기 탭(왼쪽)
    public void show_unmatched_list(View view){


        isUnMatchedClick=true;

        btn_unmatched.setSelected(true);
        btn_matched.setSelected(false);

        //** 데이터베이스 트랜젝션
        if(unmatched!=null){
            //데이터 셋팅은 되어있는 상태
            Log.d(VM_ENUM.TAG,"[ProblemBox]show_unmatched_list ");
            //        리사이클러뷰에 객체 지정

            ///mAdapater_unmatched = new ProblemListAdapter(unmatched);
            recycler_view.setAdapter(mAdapater_unmatched);
            ///mAdapater_unmatched.notifyDataSetChanged();

        }

//        else{
//            //초기 셋팅 필요
//            Log.d(VM_ENUM.TAG,"[ProblemBox],초기 셋팅 필요 | setUnmatched 호출");
//            setUnmatchedData();
//        }

    }
    //매치 완료 목록 보여주기 탭(오른쪽)
    public void show_matched_list(View view){

        isUnMatchedClick=false;
        btn_unmatched.setSelected(false);
        btn_matched.setSelected(true);

        //** 데이터베이스 트랜젝션
        if(matched!=null){
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정

            ///mAdapater_matched = new ProblemListAdapter(matched);
            recycler_view.setAdapter(mAdapater_matched);
            Log.d(VM_ENUM.TAG,"[ProblemBox]show_matched_list ");
        }
//
//        else{
//            //초기 셋팅 필요
//            Log.d(VM_ENUM.TAG,"[ProblemBox],초기 셋팅 필요 | setMatchedData 호출");
//            setMatchedData();
//        }

    }

    //뒤로가기 버튼
    public void cancel(View view) {
        finish();
    }


    //어댑터
    public class ProblemListAdapter extends RecyclerView.Adapter<ProblemListAdapter.ViewHolder>{
        private List<PostCustomData> mData = null;

        ProblemListAdapter(List<PostCustomData> list){
            mData = list;
        }


        @NonNull
        @Override
        public ProblemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.matched_item_list_teacher,parent,false);
            ProblemListAdapter.ViewHolder viewHolder = new ProblemListAdapter.ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProblemListAdapter.ViewHolder holder, int position) {

            Log.w(VM_ENUM.TAG,"[학생문제박스] onBindViewHolder position :"+position);
            holder.pName.setText(mData.get(position).getTitle());
            holder.pDate.setText(mData.get(position).getUpLoadDate());
            holder.pLive.setVisibility(View.INVISIBLE);//**라이브 default false

            if(mData.get(position).getMatchSet_teacher()!=null){
                holder.pMatchTeacher.setText(mData.get(position).getMatchSet_teacher());
            }
            if(mData.get(position).getSolveWay()!=null){
                holder.pSolveWay.setText(mData.get(position).getSolveWay());
            }


//            if(item.getProblemLive()){
//                //라이브인 경우 이미지 표시
//                holder.pLive.setVisibility(View.VISIBLE);
//            }else{
//                holder.pLive.setVisibility(View.INVISIBLE);
//            }

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView pName;
            TextView pDate;
            ImageView pLive;
            TextView pMatchTeacher;
            TextView pSolveWay;


            ViewHolder(View itemView){
                super(itemView);
                pName = itemView.findViewById(R.id.problem_name);
                pDate = itemView.findViewById(R.id.problem_date);
                pLive = itemView.findViewById(R.id.problem_live);
                pMatchTeacher = itemView.findViewById(R.id.problem_matchSet);
                pSolveWay = itemView.findViewById(R.id.problem_solveWay);

                //** 아이템 클릭 이벤트 -> 리사이클러뷰 성능을 위해 변경
                itemView.setOnClickListener(this);

                //** 아이템 클릭 이벤트
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int pos = getAdapterPosition();
//
//
//                        if (pos != RecyclerView.NO_POSITION) {
//                            //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
//                            searchData(pos);
//                        }
//                    }
//                });

            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
                            searchData(pos);
                        }
            }
        }
    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void getUnmatchedData(){
        Log.d(VM_ENUM.TAG,"[ProblemBox],onCreate | setUnmatched 호출");
//        unmatched=new ArrayList<PostCustomData>();

        firebaseDatabase=FirebaseDatabase.getInstance();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];

        String userFirebaseId=currentUserEmail.split("@")[0];
        userFirebaseId=userFirebaseId.replace(".", "_");
        userFirebaseId=userFirebaseId.replace("#", "_");
        userFirebaseId=userFirebaseId.replace("$", "_");
        userFirebaseId=userFirebaseId.replace("[", "_");
        userFirebaseId=userFirebaseId.replace("]", "_");


        String user = userFirebaseId + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        Log.d(VM_ENUM.TAG,"[VM_ProblemBox] "+user+" 의 데이터 접근");

        reference=firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                .child(user)
                .child(VM_ENUM.DB_STU_POSTS)
                .child(VM_ENUM.DB_STU_UNMATCHED);
        reference_posts = firebaseDatabase.getReference(VM_ENUM.DB_POSTS);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    reference_posts.orderByKey().equalTo(key).addListenerForSingleValueEvent(singleValueEventListener_unmatched);

                    Log.d(VM_ENUM.TAG, "[ProblemBox] ValueEventListener (unmatched_key) : " + key);

//                    String post_id=snapshot.getKey();
//                    String post_date=snapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue().toString();
//                    String post_title=snapshot.child(VM_ENUM.DB_TITLE).getValue().toString();
//
//                    unmatched.add(new PostCustomData(post_id,post_title,post_date));
//                    Log.d(TAG, "[ProblemBox] ValueEventListener : " +snapshot );

                }

////                //        리사이클러뷰에 객체 지정
//                if(isUnMatchedClick){
//                    mAdapater = new ProblemListAdapter(unmatched);
//                    recycler_view.setAdapter(mAdapater);
//                }

//        mAdapater = new ProblemListAdapter(unmatched);
//       recycler_view.setAdapter(mAdapater);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed to read value", databaseError.toException());
            }

        });
    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void getMatchedData(){

//        matched=new ArrayList<PostCustomData>();
        Log.d(VM_ENUM.TAG,"[ProblemBox],onCreate | setMatchedData 호출");
        firebaseDatabase=FirebaseDatabase.getInstance();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];

        String userFirebaseId=currentUserEmail.split("@")[0];
        userFirebaseId=userFirebaseId.replace(".", "_");
        userFirebaseId=userFirebaseId.replace("#", "_");
        userFirebaseId=userFirebaseId.replace("$", "_");
        userFirebaseId=userFirebaseId.replace("[", "_");
        userFirebaseId=userFirebaseId.replace("]", "_");


        String user = userFirebaseId+ "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        Log.d(VM_ENUM.TAG,"[VM_ProblemBox] "+user+" 의 데이터 접근");

        reference=firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                .child(user)
                .child(VM_ENUM.DB_STU_POSTS)
                .child(VM_ENUM.DB_STU_UNSOLVED);
        reference_posts = firebaseDatabase.getReference(VM_ENUM.DB_POSTS);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//                    matched.add(snapshot.getValue(PostCustomData.class));
//                    Log.d(TAG, "[ProblemBox] ValueEventListener : " +snapshot );

                    String key = snapshot.getKey();
                    reference_posts.orderByKey().equalTo(key).addListenerForSingleValueEvent(singleValueEventListener_matched);

                    Log.d(VM_ENUM.TAG, "[ProblemBox] ValueEventListener (matched_key) : " + key);


                }


                //// 리사이클러뷰에 객체 지정
//                if(!isUnMatchedClick){
//                    mAdapater = new ProblemListAdapter(matched);
//                    recycler_view.setAdapter(mAdapater);
//                }

                //        리사이클러뷰에 객체 지정
//                mAdapater = new ProblemListAdapter(matched);
//                recycler_view.setAdapter(mAdapater);

                //** 데이터 로드가 이부분이 제일 오래 걸리므로, 본 리스너가 종료되면 로딩바 중지시킴
                cal_loading_back.setVisibility(View.INVISIBLE);
                cal_loading_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

    public void searchData(int position){

        //** posts 에 대해서 이미 저장했으므로, 별도로 search 과정을 거치지 않아도 됨

        Intent intent = new Intent(VM_ProblemBoxActivity.this, VM_FullViewActivity.class);

        if(!isUnMatchedClick){//"현재상태: 매치완료 목록"
            needToBlock=null;
            fromStudentUnmatched=null;


            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, matched_posts.get(position).getP_id());
            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,matched_posts.get(position).getData_default().getTitle());
            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,matched_posts.get(position).getData_default().getGrade());
            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,matched_posts.get(position).getData_default().getProblem());



        }else{//"현재 상태: 매치 미완료 목록"
            needToBlock=VM_ENUM.IT_ARG_BLOCK;
            fromStudentUnmatched=VM_ENUM.IT_FROM_UNMATCHED;

            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, unmatched_posts.get(position).getP_id());
            intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,unmatched_posts.get(position).getData_default().getTitle());
            intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,unmatched_posts.get(position).getData_default().getGrade());
            intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,unmatched_posts.get(position).getData_default().getProblem());

           }



        if(needToBlock!=null){ //** 매치 미완료의 경우 사용자의 채팅창 막음 && 매치셋(matchset_teacher)도 찾지 않음
            intent.putExtra(VM_ENUM.IT_ARG_BLOCK, VM_ENUM.IT_ARG_BLOCK);
            intent.putExtra(VM_ENUM.IT_FROM_UNMATCHED,VM_ENUM.IT_FROM_UNMATCHED);
            Log.d(TAG, "[ProblemBox] IT_ARG_BLOCK & IT_FROM_UNMATCHED " );
        }

        ///startActivityForResult(intent,VM_ENUM.RC_PROBLEM_SOLVE);
        startActivity(intent);

    }



}

