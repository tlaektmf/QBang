package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class VM_TeacherSolveProblemActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    SolveFragment solveFragment;
    TeacherProblemFragment teacherProblemFragment;

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
        transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container,solveFragment).commitAllowingStateLoss();

    }

    public void showUsersLoad(View view) {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container, solveFragment).commitAllowingStateLoss();
    }

    public void showSolve(View view) {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_solve_container, teacherProblemFragment).commitAllowingStateLoss();
    }

    public void showBoth(View view) {
    }
}
