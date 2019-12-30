package com.example.visualmath;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG ="HomeActivity" ;
    ImageButton buttonAlarm;
    ImageButton buttonLive;
    Intent intent;
    String currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        init();

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();

        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //ds.shim



        buttonAlarm = findViewById(R.id.ib_alarm);
        buttonLive = findViewById(R.id.ib_live);

        //** 질문 중 뷰로 이동
        buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity 이동
                Intent intent;
                intent = new Intent(HomeActivity.this, ItemListActivity.class);
                startActivity(intent);

            }
        });

        //** 유투브 뷰로 이동
        buttonLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity 이동
                Intent intent;
                intent = new Intent(HomeActivity.this, VM_WebViewActivity.class);
                startActivity(intent);
            }
        });

    }

    public void init(){
        intent=getIntent();
        currentID=intent.getStringExtra("UID");

    }
    public void replaceFragment(){

        //** SettingsFragment 등록
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, new VM_SettingsActivity.SettingsFragment())
                .commit();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();
//        // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(this)
                .setTitle("VM")
                .setMessage("정말 종료하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        ActivityCompat.finishAffinity(HomeActivity.this);

                    }
                })
                .setNegativeButton("아니오",null)
                .show();
        ///super.onBackPressed();
    }
}

