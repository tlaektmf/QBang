package com.example.visualmath;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VM_LoginActivity extends AppCompatActivity {

    EditText editTextUserId;
    EditText editTextUserPw;
    Button buttonLogin;
    CheckBox checkBoxAutoLogin;

    String userId = "defaultId";
    String userPw = "defaultPw";
    Boolean isAutoLoginChecked = false;

    Boolean isLoginChecked=false;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

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

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = editTextUserId.getText().toString();
                userPw = editTextUserPw.getText().toString();

                //** 로그인 버튼 클릭시, 계정 정보 확인

                loginValidation(userId,userPw);

                /*
                 * 자동로그인이 체크 되어있는 경우, 파일에 저장
                 * 자동로그인이 체크 되어있지 않은  경우, 파일에 저장x
                 * */
                isAutoLoginChecked = checkBoxAutoLogin.isChecked();
                if(isAutoLoginChecked){
                    /*
                    자동 로그인을 선택하였을 때 실행 되는 코드
                     */

                    isLoginChecked = true;
                    if(isLoginChecked) {
                        //autologin이 체크되어 있을 경우에는, 값을 저장한다.
                        Log.i("VM_LoginActivity",userId);
                        editor.putString("id",userId);
                        editor.putString("pw", userPw);
                        editor.putBoolean("autoLogin", true);
                        editor.commit();
                    }
                }
                else{
                    /*
                    자동 로그인을 선택하지 않았을 때 실행 되는 코드
                     */
                    isLoginChecked = false;
                    editor.clear();
                    editor.commit();
                }

                // 계정확인이 완료되면, user 정보를 가지고 MainAcitivty로 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

    }


    //**올바른 아이디와 비밀번호가 입력됐는지 확인함

    /**
     * 아이디와 비밀번호가 일치하는지 확인
     * @param id : 사용자 입력 ID
     * @param password : 사용자 입력 PW
     * @return
     */
    private boolean loginValidation(String id, String password) {

        SharedPreferences preferences;
        preferences=getSharedPreferences("pref", 0);

        if(preferences.getString("id","").equals(id) &&
                preferences.getString("pw","").equals(password)) {
            // login success, 로그인 성공 시
            return true;
        } else if (preferences.getString("id","").equals(null)){
            // sign in first, 가입 처음할 때
            Toast.makeText(this, "Please Sign in first", Toast.LENGTH_LONG).show();
            return false;
        } else {
            // login failed, 로그인 실패시
            return false;
        }
    }

}
