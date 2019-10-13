package com.example.visualmath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class VM_RegiserOtherThingsActivity extends AppCompatActivity {

    private Intent intent;
    public EditText editTextdetail;
    String data_detail=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__regiser_other_things);

        init();



    }

    public void init(){
        editTextdetail=findViewById(R.id.tv_detail);
    }

    //** 사용자 카메라, 사진첩에 접근하여 파일 추가
    public void changeAddFile(View view) {
    }

    //** 추가 정보 기입
    public void loadAddInfo(View view) {
        intent.putExtra("data_extra",new VM_Data_ADD(data_detail));
        intent= new Intent(this,VM_RegisterProblemActivity.class);
    }
}
