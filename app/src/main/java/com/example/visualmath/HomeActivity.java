package com.example.visualmath;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.example.visualmath.ui.dashboard.DashboardFragment;
import com.example.visualmath.ui.dashboard.DashboardListFragment;
import com.example.visualmath.ui.home.HomeFragment;
import com.example.visualmath.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG ="HomeActivity" ;
    ImageButton buttonAlarm;
    ImageButton buttonLive;
    Intent intent;
    String currentID;

    //
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
    private HomeFragment homeFragment;
    private DashboardFragment dashboardFragment;
    private NotificationsFragment notificationsFragment;

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

        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, homeFragment).commitAllowingStateLoss();

//
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
//
//        //ds.shim


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home: {
                        transaction.replace(R.id.nav_host_fragment, homeFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_dashboard: {
                        transaction.replace(R.id.nav_host_fragment, dashboardFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.navigation_notifications: {
                        transaction.replace(R.id.nav_host_fragment, notificationsFragment).commitAllowingStateLoss();
                        break;
                    }

                }

                return true;
            }
        });

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

        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationsFragment = new NotificationsFragment();


    }

//    public void replaceFragment(){
//
//        //** SettingsFragment 등록
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.nav_host_fragment, new VM_SettingsActivity.SettingsFragment())
//                .commit();
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();
////        // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
//    }



    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();

//        new AlertDialog.Builder(this)
//                .setTitle("VM")
//                .setMessage("정말 종료하시겠습니까?")
//                .setPositiveButton("네", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        moveTaskToBack(true);
//                        ActivityCompat.finishAffinity(HomeActivity.this);
//
//                    }
//                })
//                .setNegativeButton("아니오",null)
//                .show();
        ///super.onBackPressed();

//        커스텀 다이얼로그로 변경
        final VM_Dialog_finish_app dialog = new VM_Dialog_finish_app(HomeActivity.this);

        dialog.setDialogListener(new VM_DialogListener_finish_app(){
            public void onButtonYes(){
//                네 버튼
                moveTaskToBack(true);
                ActivityCompat.finishAffinity(HomeActivity.this);
            }
            public void onButtonNo(){
//                아니오 버튼
            }
        });
        dialog.callFunction();
    }
}

