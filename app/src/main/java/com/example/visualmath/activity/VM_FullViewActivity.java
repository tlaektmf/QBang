package com.example.visualmath.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.example.visualmath.fragment.ProblemFragment;
import com.example.visualmath.fragment.SolveFragment;
import com.example.visualmath.fragment.problem_detail;

public class VM_FullViewActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    SolveFragment solveFragment;
    ProblemFragment problemFragment;
    problem_detail problemDetailFragment;
    String post_id,post_title,post_grade,post_problem;
    Intent intent;
    Bundle arguments ;

    public String needToBlock;
    public String fromStudentUnmatched;//** 학생의 unmatched 뷰에서 fragment로 전환되는 경우, matchSet_teacher를 찾지 않기로 하기 위함
    public static final String ARG_ITEM_TITLE = "post_title";
    public static final String ARG_ITEM_GRADE = "post_grade";
    public static final String ARG_ITEM_PROBLEM = "post_problem";

    public static final String TAG= VM_ENUM.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__full_view);

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

       /// Log.d(TAG,"Activity 호출");
        init();

    }

    public void init(){
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        solveFragment=new SolveFragment();
        problemFragment=new ProblemFragment();
        problemDetailFragment = new problem_detail();

        Button btn01 = findViewById(R.id.btn_full_problem);
        btn01.setSelected(true);
        needToBlock=null;
        fromStudentUnmatched=null;

        arguments = new Bundle();
        intent=getIntent();
        post_id=intent.getStringExtra(ItemDetailFragment.ARG_ITEM_ID);
        post_title=intent.getStringExtra(VM_FullViewActivity.ARG_ITEM_TITLE);
        post_grade=intent.getStringExtra(VM_FullViewActivity.ARG_ITEM_GRADE);
        post_problem=intent.getStringExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM);
        needToBlock=intent.getStringExtra(VM_ENUM.IT_ARG_BLOCK);
        fromStudentUnmatched=intent.getStringExtra(VM_ENUM.IT_FROM_UNMATCHED);
        if(fromStudentUnmatched!=null){
            Log.d(TAG, "[FullViewAct] fromStudentUnmatched 넘어옴" );
        }


        // 프래그먼트 초기 세팅
        arguments.putString(VM_FullViewActivity.ARG_ITEM_TITLE, post_title);
        arguments.putString(VM_FullViewActivity.ARG_ITEM_GRADE, post_grade);
        arguments.putString(VM_FullViewActivity.ARG_ITEM_PROBLEM, post_problem);

        solveFragment.setArguments(arguments);

        transaction.replace(R.id.container,solveFragment).commitAllowingStateLoss();


    }

    public void showProblem(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(true);
        btn02.setSelected(false);
        btn03.setSelected(false);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, solveFragment).commitAllowingStateLoss();
    }

    public void showUsersLoad(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(false);
        btn02.setSelected(true);
        btn03.setSelected(false);

        transaction = fragmentManager.beginTransaction();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, post_id);
        problemDetailFragment.setArguments(arguments);
        transaction.replace(R.id.container, problemDetailFragment).commitAllowingStateLoss();
    }

    public void showSolve(View view) {//** ProblemBoxActivity
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(false);
        btn02.setSelected(false);
        btn03.setSelected(true);

        transaction = fragmentManager.beginTransaction();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, post_id);
        arguments.putString(VM_FullViewActivity.ARG_ITEM_TITLE, post_title);

        if(needToBlock!=null){
            if(needToBlock.equals(VM_ENUM.IT_ARG_BLOCK)){
                arguments.putBoolean(VM_ENUM.IT_ARG_BLOCK,true);
            }

        }

        if(fromStudentUnmatched!=null){
            //** 학생의 unmatched 에서 fragment로 넘어온 경우에 해당함
            //이때는 DB에서 matchSet_teacher 을 찾지 않기로 한다
            if(fromStudentUnmatched.equals(VM_ENUM.IT_FROM_UNMATCHED)){
                arguments.putBoolean(VM_ENUM.IT_FROM_UNMATCHED,true);
                Log.d(TAG, "[FullViewAct] fromStudentUnmatched arguments 넘김" );
            }

        }

        problemFragment.setArguments(arguments);
        transaction.replace(R.id.container, problemFragment).commitAllowingStateLoss();
    }

    public void cancel(View view) {
        //부착되어 있는 프래그먼트 삭제
        finish();
    }
}
