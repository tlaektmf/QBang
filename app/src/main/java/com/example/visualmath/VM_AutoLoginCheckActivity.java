package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class VM_AutoLoginCheckActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__auto_login_check);

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        //** 자동 로그인 체크 확인
        if(SaveSharedPreference.getUserName(VM_AutoLoginCheckActivity.this).length() == 0) {
            // call Login Activity : sharedPreference에 저장되어 있는 정보의 길이가 0인 경우
            intent = new Intent(VM_AutoLoginCheckActivity.this, VM_LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            // Call Next Activity : 저장된 정보가 있는 경우 VM_LoginActivity를 건너뜀
            String currentUser=SaveSharedPreference.getUserName(this);
            if(currentUser.contains("@visualmath.com")){
                intent = new Intent(VM_AutoLoginCheckActivity.this, TeacherHomeActivity.class);
            }
            else if(currentUser.equals("teacher@gmail.com")){
                intent = new Intent(VM_AutoLoginCheckActivity.this, TeacherHomeActivity.class);
            }
            else{
                intent = new Intent(VM_AutoLoginCheckActivity.this, HomeActivity.class);
            }
//            else if(currentUser.equals("student@gmail.com")){
//                intent = new Intent(VM_AutoLoginCheckActivity.this, HomeActivity.class);
//            }
//            intent.putExtra("UID", SaveSharedPreference.getUserName(this).toString());//user id정보를 함께 넘김
            startActivity(intent);
            this.finish();
        }

    }
}
