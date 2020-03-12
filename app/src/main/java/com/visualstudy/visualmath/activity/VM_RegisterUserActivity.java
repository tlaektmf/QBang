package com.visualstudy.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_ENUM;
import com.visualstudy.visualmath.dialog.VM_Dialog_registerProblem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class VM_RegisterUserActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    private FirebaseAuth firebaseAuth;

    // ** 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;
    private String email = "";
    private String password = "";



    // ** 사용자 편의를 위한 로딩바
    private ProgressBar send_mail_loading_bar;
    private View send_mail_loading_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__register_user);

        init();




    }

    public void init(){

        firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언
        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);

        //로딩창
        send_mail_loading_bar = findViewById(R.id.send_mail_loading_bar);
        send_mail_loading_back = findViewById(R.id.send_mail_loading_back);
    }

    public void registerUser(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }else{
            //이메일 혹은 패스워드 형식이 맞지 않는경우

        }
    }

    // ** 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치

            return false;
        } else {
            return true;
        }
    }

    //** 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    //** 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            send_mail_loading_back.setVisibility(View.VISIBLE);
                            send_mail_loading_bar.setVisibility(View.VISIBLE);

                            //인증 메일 보냄
                            sendVerifyEmail();

                            ///Toast.makeText(VM_RegisterUserActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                        } else {
                            // 회원가입 실패
                            Toast.makeText(VM_RegisterUserActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendVerifyEmail(){
        FirebaseUser currentUser  = FirebaseAuth.getInstance().getCurrentUser();

        assert currentUser != null;
        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {                         //해당 이메일에 확인메일을 보냄
                    Log.d(VM_ENUM.TAG, "[메일 요청 완료]");

                    send_mail_loading_back.setVisibility(View.INVISIBLE);
                    send_mail_loading_bar.setVisibility(View.INVISIBLE);

                    //회원가입 성공. 메일 인증을 완료해주세요.
                    final VM_Dialog_registerProblem checkDialog =
                            new VM_Dialog_registerProblem(VM_RegisterUserActivity.this);
                    checkDialog.callFunction(9,VM_RegisterUserActivity.this);

                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            VM_Dialog_registerProblem.dig.dismiss();
                            t.cancel();
                            finish();
                        }
                    }, 3000);



                } else {                                             //메일 보내기 실패
                    Log.d(VM_ENUM.TAG, "[메일 발송 실패]");
                }
            }
        });

    }
    public void onBackPressed() { //뒤로가기 버튼 막음
        ///super.onBackPressed();
    }

    public void cancel(View view) {
        finish();
    }
}
