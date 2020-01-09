package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Button;

public class VM_FullViewActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    SolveFragment solveFragment;
    ProblemFragment problemFragment;
    problem_detail problemDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__full_view);

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
        problemFragment=new ProblemFragment();
        problemDetailFragment = new problem_detail();
        transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.container,solveFragment).commitAllowingStateLoss();

        Button btn01 = findViewById(R.id.btn_full_problem);
        btn01.setSelected(true);
    }

    public void showUsersLoad(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(true);
        btn02.setSelected(false);
        btn03.setSelected(false);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, solveFragment).commitAllowingStateLoss();
    }

    public void showSolve(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(false);
        btn02.setSelected(true);
        btn03.setSelected(false);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, problemDetailFragment).commitAllowingStateLoss();
    }

    public void showBoth(View view) {
        Button btn01 = findViewById(R.id.btn_full_problem);
        Button btn02 = findViewById(R.id.btn_full_solve);
        Button btn03 = findViewById(R.id.btn_both);

        btn01.setSelected(false);
        btn02.setSelected(false);
        btn03.setSelected(true);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, problemFragment).commitAllowingStateLoss();
    }

    public void cancel(View view) {
        finish();
    }
}
