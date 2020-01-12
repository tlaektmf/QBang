package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.visualmath.ui.dashboard.DashboardFragment;
import com.example.visualmath.ui.dashboard.TeacherDashboardFragment;
import com.example.visualmath.ui.home.HomeFragment;
import com.example.visualmath.ui.home.TeacherHomeFragment;
import com.example.visualmath.ui.notifications.NotificationsFragment;
import com.example.visualmath.ui.notifications.TeacherNotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeacherHomeActivity extends AppCompatActivity {

    ImageButton buttonAlarm;
    Intent intent;

    //
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
    private TeacherHomeFragment homeFragment;
    private TeacherDashboardFragment dashboardFragment;
    private TeacherNotificationsFragment notificationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        BottomNavigationView navView = findViewById(R.id.teacher_nav_view);

        init();

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.teacher_nav_host_fragment, homeFragment).commitAllowingStateLoss();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.teacher_navigation_home, R.id.teacher_navigation_dashboard, R.id.teacher_navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.teacher_nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.teacher_navigation_home: {
                        transaction.replace(R.id.teacher_nav_host_fragment, homeFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.teacher_navigation_dashboard: {
                        transaction.replace(R.id.teacher_nav_host_fragment, dashboardFragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.teacher_navigation_notifications: {
                        transaction.replace(R.id.teacher_nav_host_fragment, notificationsFragment).commitAllowingStateLoss();
                        break;
                    }

                }

                return true;
            }
        });

    }

    public void init(){
//        buttonAlarm = findViewById(R.id.teacher_ib_alarm);

        intent = getIntent();
        homeFragment = new TeacherHomeFragment();
        dashboardFragment = new TeacherDashboardFragment();
        notificationsFragment = new TeacherNotificationsFragment();
    }

    public void clickAlarm(View view) {
        Intent intent;
        intent = new Intent(TeacherHomeActivity.this, TeacherItemListActivity.class);
        startActivity(intent);
    }

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
//                        ActivityCompat.finishAffinity(TeacherHomeActivity.this);

//
//                    }
//                })
//                .setNegativeButton("아니오",null)
//                .show();
        ///super.onBackPressed();
        final VM_Dialog_finish_app dialog = new VM_Dialog_finish_app(TeacherHomeActivity.this);

        dialog.setDialogListener(new VM_DialogListener_finish_app(){
            public void onButtonYes(){
//                네 버튼
                moveTaskToBack(true);
                ActivityCompat.finishAffinity(TeacherHomeActivity.this);
            }
            public void onButtonNo(){
//                아니오 버튼
            }
        });
        dialog.callFunction();
    }
}
