package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.dummy.AlarmItem;
import com.example.visualmath.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VM_ProblemBoxActivity extends AppCompatActivity {
    public static String TAG=VM_ENUM.TAG;

    private Button btn_unmatched;
    private Button btn_matched;
    private boolean isUnMatchedClick;

    //문제 목록을 보여줄 리사이클러뷰
    private RecyclerView recycler_view;
    private  ProblemListAdapter mAdapater;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private static List<Pair<String,Pair<String,String>>> unmatched; //포스트 데이터 unmatched 리스트 /id/title/date
    private static List<Pair<String,Pair<String,String>>> matched; //포스트 데이터 matched 리스트 /id/title/date


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_box);

        init();
    }


    public void init(){
        isUnMatchedClick=true;//** 초기에는 매치 미완료 클릭
        btn_unmatched = findViewById(R.id.btn_unmatched);
        btn_matched = findViewById(R.id.btn_matched);
        recycler_view = findViewById(R.id.problem_recyclerview);

//        화면 초기 상태에서는 미완료 목록이 클릭된 상태
        btn_unmatched.setSelected(true);

//        레이아웃 매니저 생성 및 지정
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        //** 데이터 초기화
        unmatched=null;
        matched=null;

        Log.d(VM_ENUM.TAG,"[ProblemBox],onCreate | setUnmatched 호출");
        setUnmatchedData();


    }


    //매치 미완료 목록 보여주기 탭(왼쪽)
    public void show_unmatched_list(View view){
        isUnMatchedClick=true;
        Toast.makeText(this, "매치 미완료 목록",Toast.LENGTH_LONG).show();
        btn_unmatched.setSelected(true);
        btn_matched.setSelected(false);

        //** 데이터베이스 트랜젝션
        if(unmatched!=null){
            //데이터 셋팅은 되어있는 상태
            Log.d(VM_ENUM.TAG,"[ProblemBox],데이터 셋팅은 되어있는 상태 ");
            //        리사이클러뷰에 객체 지정
            mAdapater = new ProblemListAdapter(unmatched);
            recycler_view.setAdapter(mAdapater);

        }else{
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG,"[ProblemBox],초기 셋팅 필요 | setUnmatched 호출");
            setUnmatchedData();
        }
    }
    //매치 완료 목록 보여주기 탭(오른쪽)
    public void show_matched_list(View view){
        isUnMatchedClick=false;
        Toast.makeText(this, "매치 완료 목록",Toast.LENGTH_LONG).show();
        btn_unmatched.setSelected(false);
        btn_matched.setSelected(true);

        //** 데이터베이스 트랜젝션
        if(matched!=null){
            //데이터 셋팅은 되어있는 상태
            //        리사이클러뷰에 객체 지정
            mAdapater = new ProblemListAdapter(matched);
            recycler_view.setAdapter(mAdapater);
            Log.d(VM_ENUM.TAG,"[ProblemBox],데이터 셋팅은 되어있는 상태 ");
        }else{
            //초기 셋팅 필요
            Log.d(VM_ENUM.TAG,"[ProblemBox],초기 셋팅 필요 | setMatchedData 호출");
            setMatchedData();
        }

    }

    //뒤로가기 버튼
    public void cancel(View view) {
        finish();
    }


    //어댑터
    public class ProblemListAdapter extends RecyclerView.Adapter<ProblemListAdapter.ViewHolder>{
        private List<Pair<String,Pair<String,String>>> mData = null;

        ProblemListAdapter(List<Pair<String,Pair<String,String>>> list){
            mData = list;
        }

        @NonNull
        @Override
        public ProblemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.item_list_notime,parent,false);
            ProblemListAdapter.ViewHolder viewHolder = new ProblemListAdapter.ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProblemListAdapter.ViewHolder holder, int position) {

            holder.pName.setText(mData.get(position).second.first);
            holder.pDate.setText(mData.get(position).second.second);
            holder.pLive.setVisibility(View.INVISIBLE);//**라이브 default false

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

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView pName;
            TextView pDate;
            ImageView pLive;

            ViewHolder(View itemView){
                super(itemView);
                pName = itemView.findViewById(R.id.problem_name);
                pDate = itemView.findViewById(R.id.problem_date);
                pLive = itemView.findViewById(R.id.problem_live);

                //** 아이템 클릭 이벤트
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();


                        if (pos != RecyclerView.NO_POSITION) {
                            //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
                            searchData(pos);
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
    public void setUnmatchedData(){

        unmatched=new ArrayList<Pair<String,Pair<String,String>>>();

        firebaseDatabase=FirebaseDatabase.getInstance();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        String user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        Log.d(VM_ENUM.TAG,"[VM_ProblemBox] "+user+" 의 데이터 접근");

        reference=firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                .child(user)
                .child(VM_ENUM.DB_STU_POSTS)
                .child(VM_ENUM.DB_STU_UNMATCHED);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String post_id=snapshot.getKey();
                    String post_date=snapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue().toString();
                    String post_title=snapshot.child(VM_ENUM.DB_TITLE).getValue().toString();
                    unmatched.add(Pair.create(post_id, Pair.create(post_title,post_date)));

                    Log.d(TAG, "[ProblemBox] ValueEventListener : " +post_id );

                }

                //        리사이클러뷰에 객체 지정
        mAdapater = new ProblemListAdapter(unmatched);
       recycler_view.setAdapter(mAdapater);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void setMatchedData(){

        matched=new ArrayList<Pair<String,Pair<String,String>>>();

        firebaseDatabase=FirebaseDatabase.getInstance();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        String user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        Log.d(VM_ENUM.TAG,"[VM_ProblemBox] "+user+" 의 데이터 접근");

        reference=firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                .child(user)
                .child(VM_ENUM.DB_STU_POSTS)
                .child(VM_ENUM.DB_STU_UNSOLVED);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String post_id=snapshot.getKey();
                    String post_date=snapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue().toString();
                    String post_title=snapshot.child(VM_ENUM.DB_TITLE).getValue().toString();
                    matched.add(Pair.create(post_id, Pair.create(post_title,post_date)));

                    Log.d(TAG, "[ProblemBox] ValueEventListener : " +post_id );

                }

                //        리사이클러뷰에 객체 지정
                mAdapater = new ProblemListAdapter(matched);
                recycler_view.setAdapter(mAdapater);

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

        if(isUnMatchedClick==false){//"현재상태: 매치완료 목록"
//            reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS)
//                    .child(matched)
//                    .child(VM_ENUM.DB_STU_POSTS)
//                    .child(VM_ENUM.DB_STU_UNMATCHED);
        }else{//"현재 상태: 매치 미완료 목록"
            post_id=unmatched.get(position).first;
            reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS)
                    .child(post_id)
                    .child(VM_ENUM.DB_DATA_DEFAULT);

        }

        final String finalPost_id = post_id;
        Log.d(VM_ENUM.TAG,"[VM_ProblemBox] POSTS 데이터"+finalPost_id+" 접근");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VM_Data_Default vmDataDefault=dataSnapshot.getValue(VM_Data_Default.class);

                assert vmDataDefault != null;
                Log.d(TAG, "[ProblemBox] unmatched ValueEventListener : " +vmDataDefault.getTitle() );

                Intent intent = new Intent(VM_ProblemBoxActivity.this, VM_FullViewActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, finalPost_id);
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,vmDataDefault.getTitle());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,vmDataDefault.getGrade());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,vmDataDefault.getProblem());


                startActivity(intent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

}

