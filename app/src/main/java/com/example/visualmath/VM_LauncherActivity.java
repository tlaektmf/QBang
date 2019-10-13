package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class VM_LauncherActivity extends AppCompatActivity {

    private static final String TAG="Launcher";

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

        //** 권한 설정
        Log.i(TAG,Build.VERSION.SDK_INT+"");
        if (Build.VERSION.SDK_INT >= 26) { //6.0 마시멜로우 -> API Level 26
            // Sdk 26버전부터 실행할 코드
            tedPermission();

        }else{
            tedPermission();
        }


//** 3초 후 화면 전환
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent=new Intent(VM_LauncherActivity.this,VM_AutoLoginCheckActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        },3000);


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

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 요청한 모든 권한 요청 성공
                Toast.makeText(VM_LauncherActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(VM_LauncherActivity.this,VM_AutoLoginCheckActivity.class);
                startActivity(intent);

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ** 권한 요청 실패 : 거부당한 권한들의 목록이 나옴
                Toast.makeText(VM_LauncherActivity.this,deniedPermissions.toString(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(VM_LauncherActivity.this,VM_AutoLoginCheckActivity.class);
                startActivity(intent);
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_alarm))
                .setDeniedMessage(getResources().getString(R.string.permission_notice))
                .setPermissions(Manifest.permission.WAKE_LOCK)
                .check();

    }




}
