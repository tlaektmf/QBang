package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VM_TeacherSolveProblemActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    SolveFragment solveFragment;
    TeacherProblemFragment teacherProblemFragment;
    problem_detail problemDetailFragment;

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
        solveFragment=new SolveFragment();
        teacherProblemFragment=new TeacherProblemFragment();
        problemDetailFragment = new problem_detail();
        transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container,solveFragment).commitAllowingStateLoss();

    }

    public void showUsersLoad(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(true);
        btn02.setSelected(false);
        btn03.setSelected(false);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container, solveFragment).commitAllowingStateLoss();
    }

    public void showSolve(View view) {
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.teacher_solve_container, teacherProblemFragment).commitAllowingStateLoss();
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(false);
        btn02.setSelected(true);
        btn03.setSelected(false);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container, problemDetailFragment).commitAllowingStateLoss();
    }

    public void showBoth(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(false);
        btn02.setSelected(false);
        btn03.setSelected(true);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container, teacherProblemFragment).commitAllowingStateLoss();
    }
}
