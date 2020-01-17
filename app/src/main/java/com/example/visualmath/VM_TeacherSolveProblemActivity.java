package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class VM_TeacherSolveProblemActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
//    SolveFragment solveFragment;
//    TeacherProblemFragment teacherProblemFragment;
//    problem_detail problemDetailFragment;

    //문제 목록을 보여줄 리사이클러뷰
    private RecyclerView recycler_view;
    ProblemListAdapter mAdapater;
    ArrayList<problem_item> mList = new ArrayList<problem_item>();

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
        fragmentManager=getSupportFragmentManager();
//        solveFragment=new SolveFragment();
//        teacherProblemFragment=new TeacherProblemFragment();
//        problemDetailFragment = new problem_detail();


//        변경_0
        recycler_view = findViewById(R.id.teacher_solve_rv);
        mAdapater = new ProblemListAdapter(mList);
        recycler_view.setAdapter(mAdapater);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
//        테스트용 아이템 추가
        addItem("테스트 문제 1번","2020-01-01",true);
        addItem("테스트 문제 2번","2020-01-02",true);
        addItem("테스트 문제 3번","2020-01-03",false);
//        변경_0

//        transaction=fragmentManager.beginTransaction();
//        transaction.replace(R.id.teacher_solve_container,solveFragment).commitAllowingStateLoss();
    }

    public void addItem(String name, String date, Boolean live){
        problem_item item = new problem_item();

        item.setProblemName(name);
        item.setProblemDate(date);
        item.setProblemLive(live);

        mList.add(item);
    }

    public void cancel(View view) {
        finish();
    }

    public class problem_item {
        private String problem_name;//문제 제목
        private String problem_date;//문제 날짜
        private Boolean problem_live;//문제 라이브인지 아닌지

        public void setProblemName(String problem_name){
            this.problem_name=problem_name;
        }
        public void setProblemDate(String problem_date){
            this.problem_date=problem_date;
        }
        public void setProblemLive(Boolean problem_live){
            this.problem_live=problem_live;
        }
        public String getProblemName(){
            return this.problem_name;
        }
        public String getProblemDate(){
            return this.problem_date;
        }
        public Boolean getProblemLive(){
            return this.problem_live;
        }
    }

    public class ProblemListAdapter extends RecyclerView.Adapter<VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder>{
        private ArrayList<VM_TeacherSolveProblemActivity.problem_item> mData = null;

        ProblemListAdapter(ArrayList<VM_TeacherSolveProblemActivity.problem_item> list){
            mData = list;
        }

        @NonNull
        @Override
        public VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.item_list_notime,parent,false);
            VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder viewHolder = new VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull VM_TeacherSolveProblemActivity.ProblemListAdapter.ViewHolder holder, int position) {
            VM_TeacherSolveProblemActivity.problem_item item = mData.get(position);

            holder.pName.setText(item.getProblemName());
            holder.pDate.setText(item.getProblemDate());

            if(item.getProblemLive()){
                //라이브인 경우 이미지 표시
                holder.pLive.setVisibility(View.VISIBLE);
            }else{
                holder.pLive.setVisibility(View.INVISIBLE);
            }

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
            }
        }
    }

//    public void showUsersLoad(View view) {
//        Button btn01 = findViewById(R.id.btn_full_problem);
//        Button btn02 = findViewById(R.id.btn_full_solve);
//        Button btn03 = findViewById(R.id.btn_both);
//
//        btn01.setSelected(true);
//        btn02.setSelected(false);
//        btn03.setSelected(false);
//
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.teacher_solve_container, solveFragment).commitAllowingStateLoss();
//    }

//    public void showSolve(View view) {
////        transaction = fragmentManager.beginTransaction();
////        transaction.replace(R.id.teacher_solve_container, teacherProblemFragment).commitAllowingStateLoss();
//        Button btn01 = findViewById(R.id.btn_full_problem);
//        Button btn02 = findViewById(R.id.btn_full_solve);
//        Button btn03 = findViewById(R.id.btn_both);
//
//        btn01.setSelected(false);
//        btn02.setSelected(true);
//        btn03.setSelected(false);
//
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.teacher_solve_container, problemDetailFragment).commitAllowingStateLoss();
//    }

//    public void showBoth(View view) {
//        Button btn01 = findViewById(R.id.btn_full_problem);
//        Button btn02 = findViewById(R.id.btn_full_solve);
//        Button btn03 = findViewById(R.id.btn_both);
//
//        btn01.setSelected(false);
//        btn02.setSelected(false);
//        btn03.setSelected(true);
//
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.teacher_solve_container, teacherProblemFragment).commitAllowingStateLoss();
//    }
}
