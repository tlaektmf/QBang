package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class VM_RegisterProblemActivity extends AppCompatActivity {

    final CharSequence[] gradeItems = {"초등", "중등", "고등"};
    Button buttonGrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__register_problem);

        //** ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        init();


    }

    public void init(){
        buttonGrade=findViewById(R.id.btn_grade);
    }
    //** 사용자 갤러리에 접근 or 카메라에 접근
    public void changeProblemFile(View view) {
        ImageView imageView=(ImageView)findViewById(R.id.iv_file_problem);

        String res_name="@drawable/img_math"+(1);
        int resID = getResources().getIdentifier(res_name, "drawable",  getPackageName());
        imageView.setImageResource(resID);

    }

    public void changeGrade(View view) {
        //**초등, 중등, 고등 선택
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        ///oDialog.setPositiveButton("선택",null);
        ///oDialog.setNeutralButton("취소",null);
        oDialog.setTitle("학년을 선택해 주세요.");

        oDialog.setItems(gradeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonGrade.setText(gradeItems[which]);
            }
        }).setCancelable(true)
                .show();
    }

    public void addOther(View view) {
        Intent intent;
        intent = new Intent(VM_RegisterProblemActivity.this, VM_RegiserOtherThingsActivity.class);
        startActivity(intent);
    }

//    public void changeAnswerFile(View view) {
//        ImageView imageView=(ImageView)findViewById(R.id.iv_file_answer);
//
//        String res_name="@drawable/img_math"+(4);
//        int resID = getResources().getIdentifier(res_name, "drawable",  getPackageName());
//        imageView.setImageResource(resID);
//    }
}
