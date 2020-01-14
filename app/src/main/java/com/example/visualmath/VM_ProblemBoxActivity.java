package com.example.visualmath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class VM_ProblemBoxActivity extends AppCompatActivity {

    private Button btn_unmatched;
    private Button btn_matched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_box);

        init();
    }
    public void init(){
        btn_unmatched = findViewById(R.id.btn_unmatched);
        btn_matched = findViewById(R.id.btn_matched);

//        화면 초기 상태에서는 미완료 목록이 클릭된 상태
        btn_unmatched.setSelected(true);
    }

    //매치 미완료 목록 보여주기 탭(왼쪽)
    public void show_unmatched_list(View view){
        Toast.makeText(this, "매치 미완료 목록",Toast.LENGTH_LONG).show();
        btn_unmatched.setSelected(true);
        btn_matched.setSelected(false);
    }
    //매치 완료 목록 보여주기 탭(오른쪽)
    public void show_matched_list(View view){
        Toast.makeText(this, "매치 완료 목록",Toast.LENGTH_LONG).show();
        btn_unmatched.setSelected(false);
        btn_matched.setSelected(true);
    }

    public void cancel(View view) {
        finish();
    }
}
