package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeacherHomeActivity extends AppCompatActivity {

    ImageButton buttonAlarm;

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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.teacher_navigation_home, R.id.teacher_navigation_dashboard, R.id.teacher_navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.teacher_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    public void init(){
        buttonAlarm = findViewById(R.id.teacher_ib_alarm);
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
