package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class VM_LoginActivity extends AppCompatActivity {

    EditText editTextUserId;
    EditText editTextUserPw;
    Button buttonLogin;
    CheckBox checkBoxAutoLogin;

    String userId = "defaultId";
    String userPw = "defaultPw";
    Boolean isAutoLoginChecked = false;

    Boolean isLoginSuccess=false;

    //define firebase object
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__login);

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();

        }

        editTextUserId = findViewById(R.id.et_userID);
        editTextUserPw = findViewById(R.id.et_userPw);
        buttonLogin = findViewById(R.id.btn_login);
        checkBoxAutoLogin = findViewById(R.id.cb_AutoLogin);

        //**initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //이미 로그인 되었다면 이 액티비티를 종료함
            finish();
            //그리고 MainActivity 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), HomeActivity.class)); //추가해 줄 MainActivity
        }



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = editTextUserId.getText().toString();
                userPw = editTextUserPw.getText().toString();
                isAutoLoginChecked = checkBoxAutoLogin.isChecked();


                //** 로그인 버튼 클릭시, 계정 정보 확인 ->firebase 통신
                if(checkEmpty(userId,userPw)){
                    //계정 정보 맞는 경우
                    userLogin(userId,userPw);
                }


            }
        });


    }


    //**빈칸이 없는 지 확인
    private boolean checkEmpty(String email, String password){

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //**올바른 아이디와 비밀번호가 입력됐는지 확인함
    private void userLogin(String email,String password){
        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            /*
                             * 자동로그인이 체크 되어있는 경우, 파일에 저장
                             * 자동로그인이 체크 되어있지 않은  경우, 파일에 저장x
                             * */
                            if(isAutoLoginChecked){
                                SaveSharedPreference.setUserName(VM_LoginActivity.this, userId);
                                Log.i("VM_LoginActivity",isAutoLoginChecked.toString());
                            }
                            else{
                                /*
                                자동 로그인을 선택하지 않았을 때 실행 되는 코드
                                 */
                                SaveSharedPreference.clearUserName(VM_LoginActivity.this);
                            }

                            // 계정확인이 완료되면, user 정보를 가지고 HomeActivity 이동
                            Intent intent=null;
                            if(editTextUserId.getText().toString().equals("student@gmail.com")){
                                intent= new Intent(getApplicationContext(), HomeActivity.class);
                            }
                            else if(editTextUserId.getText().toString().equals("teacher@gmail.com")){
                                intent = new Intent(getApplicationContext(), TeacherHomeActivity.class);
                            }
                            //intent.putExtra("UID", userId);
                            startActivity(intent);
                            finish();

                        } else {
                            //로그인 실패
                            Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
