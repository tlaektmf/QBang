package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.PointerIcon;
import android.view.View;

public class VM_FullViewActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    SolveFragment solveFragment;
    ProblemFragment problemFragment;

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
        transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.container,solveFragment).commitAllowingStateLoss();

    }
    public void showUsersLoad(View view) {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, solveFragment).commitAllowingStateLoss();
    }

    public void showSolve(View view) {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, problemFragment).commitAllowingStateLoss();
    }

    public void showBoth(View view) {
    }
}
