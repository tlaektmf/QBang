package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class VM_LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //전체화면 모드 (setContentView(레이아웃) 보다 앞부분에 넣어줘야 됨)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_vm__launcher);


        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(VM_LauncherActivity.this,VM_AutoLoginCheckActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);



    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //다음 화면에 들어와있을 때 예약 걸어주기
//        handler.postDelayed(r, 4000);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //화면을 벗어나면, handler에 예약해놓은 작업을 취소.
//        handler.removeCallbacks(r); //예약 취소
//    }

}
