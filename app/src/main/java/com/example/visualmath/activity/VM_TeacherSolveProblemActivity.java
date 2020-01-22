package com.example.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VM_TeacherSolveProblemActivity extends AppCompatActivity {

    private List<PostCustomData> postCustomData;
    //문제 목록을 보여줄 리사이클러뷰
    private RecyclerView recycler_view;
    ProblemListAdapter mAdapater;

    //** DB
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    ///private static List<Pair<String,Pair<String,String>>> matched; //포스트 데이터 matched 리스트 /id/title/date


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__teacher_solve_problem);

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        init();
    }

    public void init(){

        recycler_view = findViewById(R.id.teacher_solve_rv);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        //** 데이터 초기화
        postCustomData=null;

        Log.d(VM_ENUM.TAG,"[ProblemBox],onCreate | setMatchedData 호출");
        setMatchedData();

    }

    public void cancel(View view) {
        finish();
    }



    public class ProblemListAdapter extends RecyclerView.Adapter<VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder>{
        private List<PostCustomData> mData = null;

        ProblemListAdapter(List<PostCustomData> list){
            mData = list;
        }

        @NonNull
        @Override
        public VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.matched_item_list_teacher,parent,false);
            VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder viewHolder = new VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder holder, int position) {

            Log.d(VM_ENUM.TAG,"[teacherSolveProblem] "+mData.get(position).getMatchSet_student());
            holder.pName.setText(mData.get(position).getTitle());
            holder.pMatchStudent.setText(mData.get(position).getMatchSet_student());

           holder.pSolveWay.setText(mData.get(position).getSolveWay());
            holder.pDate.setText(mData.get(position).getUpLoadDate());
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
            TextView pMatchStudent;
            TextView pSolveWay;

            ViewHolder(View itemView){
                super(itemView);
                pName = itemView.findViewById(R.id.problem_name);
                pDate = itemView.findViewById(R.id.problem_date);
                pLive = itemView.findViewById(R.id.problem_live);
                pMatchStudent = itemView.findViewById(R.id.problem_matchSet);
                pSolveWay = itemView.findViewById(R.id.problem_solveWay);

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
    public void setMatchedData(){

        postCustomData=new ArrayList<PostCustomData>();

        firebaseDatabase=FirebaseDatabase.getInstance();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        String user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        Log.d(VM_ENUM.TAG,"[VM_TeacherProblemBox] "+user+" 의 데이터 접근");

        reference=firebaseDatabase.getReference(VM_ENUM.DB_TEACHERS)
                .child(user)
                .child(VM_ENUM.DB_TEA_POSTS)
                .child(VM_ENUM.DB_TEA_UNSOLVED);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    postCustomData.add(snapshot.getValue(PostCustomData.class));
                    Log.d(VM_ENUM.TAG, "[TeacherProblemBox] ValueEventListener : " +snapshot );

                }

                //        리사이클러뷰에 객체 지정
                mAdapater = new VM_TeacherSolveProblemActivity.ProblemListAdapter(postCustomData);
                recycler_view.setAdapter(mAdapater);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(VM_ENUM.TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

    public void searchData(int position){
        String post_id=null;

            post_id=postCustomData.get(position).getP_id();
            reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS)
                    .child(post_id)
                    .child(VM_ENUM.DB_DATA_DEFAULT);


        final String finalPost_id = post_id;
        Log.d(VM_ENUM.TAG,"[TeacherProblemBox] POSTS 데이터"+finalPost_id+" 접근");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {//**한번만 호출
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VM_Data_Default vmDataDefault=dataSnapshot.getValue(VM_Data_Default.class);

                assert vmDataDefault != null;
                Log.d(VM_ENUM.TAG, "[TeacherProblemBox] unmatched ValueEventListener : " +vmDataDefault.getTitle() );

                Intent intent = new Intent(VM_TeacherSolveProblemActivity.this, VM_FullViewActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, finalPost_id);
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,vmDataDefault.getTitle());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,vmDataDefault.getGrade());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,vmDataDefault.getProblem());


                startActivity(intent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(VM_ENUM.TAG, "Failed to read value", databaseError.toException());
            }
        });

    }
}
