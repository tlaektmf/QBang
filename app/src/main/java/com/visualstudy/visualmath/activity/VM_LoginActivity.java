package com.visualstudy.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.dialog.VM_Dialog_registerProblem;
import com.visualstudy.visualmath.preference.SaveSharedPreference;
import com.visualstudy.visualmath.VM_DBHandler;
import com.visualstudy.visualmath.VM_ENUM;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class VM_LoginActivity extends AppCompatActivity {

    EditText editTextUserId;
    EditText editTextUserPw;
    Button buttonLogin;
    CheckBox checkBoxAutoLogin;
    TextView textViewRegister;

    String userId = "defaultId";
    String userPw = "defaultPw";
    Boolean isAutoLoginChecked = false;

    Boolean isLoginSuccess=false;

    //define firebase object
    FirebaseAuth firebaseAuth;

    //** <<<<<<<<구글 로그인
    private GoogleSignInClient googleSignInClient;

    //** 구글 로그인>>>>>>>>
//   구글 버튼 이미지
    private ConstraintLayout layoutGoogle;

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
        textViewRegister=findViewById(R.id.singup_text);
        //**initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

//        if(firebaseAuth.getCurrentUser() != null){
//            //이미 로그인 되었다면 이 액티비티를 종료함
//            finish();
//            //그리고 MainActivity 액티비티를 연다.
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class)); //추가해 줄 MainActivity
//        }


        //** <<<<<<<<구글 로그인
        //로그인 시도할 액티비티에서 유저데이터 요청
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //** 로그인 후, 따로 로그아웃을 하지 않았다면, 다음에 앱 켰을때, 바로 다음 화면으로 넣어주길 바란다면 코드 오픈
        //checkedLogin();

        firebaseAuth = FirebaseAuth.getInstance();

        //** <<<<<<<<구글 로그인

        layoutGoogle = findViewById(R.id.google_Login_btn);
        layoutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(VM_ENUM.TAG,"소셜 로그인(구글 로그인)의 경우, 자동 로그인이 불가합니다.");
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, VM_ENUM.RC_GOOGLE_LOGIN);
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
    private void userLogin(final String email, String password){
        //logging in the user

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {//** 로그인 성공 시

                            String currentUserEmail = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
                            assert currentUserEmail != null;
                            String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];

                            String userFirebaseId=currentUserEmail.split("@")[0];
                            userFirebaseId=userFirebaseId.replace(".", "_");
                            userFirebaseId=userFirebaseId.replace("#", "_");
                            userFirebaseId=userFirebaseId.replace("$", "_");
                            userFirebaseId=userFirebaseId.replace("[", "_");
                            userFirebaseId=userFirebaseId.replace("]", "_");


                            String user=userFirebaseId + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

                            boolean isAllowed=false;

                            if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
                                //**선생님인 경우
                                isAllowed=true;
                            }else{
                                //** 이메일 검증 절차 확인
                                if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()){
                                    Log.d(VM_ENUM.TAG,"[사용자 이메일 인증 완료]");
                                    isAllowed=true;
                                }else{
                                    Log.d(VM_ENUM.TAG,"[사용자 이메일 인증 이전]");
                                    isAllowed=false;
                                }
                            }
                            if(isAllowed){
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

                                //** 데이터 베이스 유무 확인 및 인텐트 이동
                                checkUserDatabase(user,mailDomain);


                            }else{

                                final VM_Dialog_registerProblem checkDialog =
                                        new VM_Dialog_registerProblem(VM_LoginActivity.this);
                                checkDialog.callFunction(4);
                            }

                            }
                      else {//로그인 실패
                            ///Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_LONG).show();
                            final VM_Dialog_registerProblem checkDialog =
                                    new VM_Dialog_registerProblem(VM_LoginActivity.this);
                            checkDialog.callFunction(10);
                        }
                    }
                });
    }


    public void clickLoginButton(View view) {

        userId = editTextUserId.getText().toString();
        userPw = editTextUserPw.getText().toString();
        isAutoLoginChecked = checkBoxAutoLogin.isChecked();


        //** 로그인 버튼 클릭시, 계정 정보 확인 ->firebase 통신
        if(checkEmpty(userId,userPw)){
            //계정 정보 맞는 경우
            userLogin(userId,userPw);
        }

    }


    public void checkgoogleDB(final GoogleSignInAccount acct){


        //** DB 있는 지 확인
        final String user;
        final String mailDomain = acct.getEmail().split("@")[1].split("\\.")[0];

        String userFirebaseId=acct.getEmail().split("@")[0];
        userFirebaseId=userFirebaseId.replace(".", "_");
        userFirebaseId=userFirebaseId.replace("#", "_");
        userFirebaseId=userFirebaseId.replace("$", "_");
        userFirebaseId=userFirebaseId.replace("[", "_");
        userFirebaseId=userFirebaseId.replace("]", "_");


        user = userFirebaseId + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        Log.d(VM_ENUM.TAG,user);

        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference reference= firebaseDatabase.getReference(VM_ENUM.DB_USERS);
        reference.orderByKey().equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    Log.d(VM_ENUM.TAG,"계정이 없습니다. DB 등록");
                    //구글 이용자 확인된 사람정보 파이어베이스로 넘기기

                    //데이터 베이스 등록
                    VM_DBHandler dbHandler=new VM_DBHandler();
                    Log.d(VM_ENUM.TAG, "[google user email]"+acct.getEmail());

                    Log.d(VM_ENUM.TAG, "[google user email]"+acct.getEmail());

                    String mailDomain=acct.getEmail().split("@")[1].split("\\.")[0];
                    //String user=acct.getEmail().split("@")[0]+"_"+mailDomain;//이메일 형식은 파이어베이스 정책상 불가
                    dbHandler.newUser(user,VM_ENUM.STUDENT);

                    if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
                        Intent intent = new Intent(getApplicationContext(), TeacherHomeActivity.class); //**
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class); //**
                        startActivity(intent);
                        finish();
                    }

                }else{

                    String user_type=dataSnapshot.getChildren().iterator().next().child(VM_ENUM.DB_USER_TYPE).getValue().toString();
                    Log.d(VM_ENUM.TAG,"이미 계정이 존재합니다. DB 등록 하지 않음, "+user_type);

                    if(user_type.equals(VM_ENUM.TEACHER)){
                        Intent intent = new Intent(getApplicationContext(), TeacherHomeActivity.class); //**
                        startActivity(intent);
                        finish();
                    }else if(user_type.equals(VM_ENUM.STUDENT)){
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class); //**
                        startActivity(intent);
                        finish();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //구글 파이어베이스로 값넘기기
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final String user_type) {
        //파이어베이스로 받은 구글사용자가 확인된 이용자의 값을 토큰으로 받고
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//** 아이디 생성 완료

                            //** 자동로그인 체크
                            isAutoLoginChecked=checkBoxAutoLogin.isChecked();
                            if(isAutoLoginChecked){
                                SaveSharedPreference.setUserName(VM_LoginActivity.this, acct.getEmail());
                                Log.i(VM_ENUM.TAG,"[자동로그인 클릭]");
                            }
                            else{
                                Log.i(VM_ENUM.TAG,"[자동로그인 클릭 안함]");
                                SaveSharedPreference.clearUserName(VM_LoginActivity.this);
                            }

                            checkgoogleDB(acct);

//                            //** 액티비티 전환
//                            if(user_type.equals(VM_ENUM.TEACHER)){
//                                Intent intent = new Intent(getApplicationContext(), TeacherHomeActivity.class); //**
//                                startActivity(intent);
//                                finish();
//                            }else if(user_type.equals(VM_ENUM.STUDENT)){
//                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class); //**
//                                startActivity(intent);
//                                finish();
//                            }

                            Toast.makeText(VM_LoginActivity.this, "아이디 생성완료", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(VM_ENUM.TAG,"아이디 생성 실패! 다시 시도해주세요");
                            Toast.makeText(VM_LoginActivity.this, "아이디 생설실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void checkedLogin() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) { // 만약 로그인이 되어있으면 다음 액티비티 실행
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Intent Result 반환
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //VM_ENUM.RC_GOOGLE_LOGIN 을 통해 로그인 확인여부 코드가 정상 전달되었다면
        if (requestCode == VM_ENUM.RC_GOOGLE_LOGIN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d(VM_ENUM.TAG, "이름 =" + account.getDisplayName());
                Log.d(VM_ENUM.TAG, "이메일=" + account.getEmail());
                Log.d(VM_ENUM.TAG, "getId()=" + account.getId());
                Log.d(VM_ENUM.TAG, "getAccount()=" + account.getAccount());
                Log.d(VM_ENUM.TAG, "getIdToken()=" + account.getIdToken());

                firebaseAuthWithGoogle(account,VM_ENUM.STUDENT);//** 구글 로그인으로 한 유저는 일단 무조건 학생으로 구분함

            } catch (ApiException e) {
            }
        }
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 막음
        //super.onBackPressed();
    }

    public void clickRegisterUser(View view) {
        Intent intent=new Intent(VM_LoginActivity.this, VM_RegisterUserActivity.class);
        startActivity(intent);
    }

    public void createUserDatabase(String user,String user_type){
        //데이터 베이스 등록
        VM_DBHandler dbHandler=new VM_DBHandler();
        Log.d(VM_ENUM.TAG, "[ createUserDatabase | user email, user type]"+user+","+user_type);

        dbHandler.newUser(user,user_type);

        if(user_type.equals(VM_ENUM.TEACHER)){
            Log.d(VM_ENUM.TAG, "[createUserDatabase | 선생님 모드 인텐트 이동]");
            Intent intent = new Intent(getApplicationContext(), TeacherHomeActivity.class); //**
            startActivity(intent);
            finish();
        }else if(user_type.equals(VM_ENUM.STUDENT)){
            Log.d(VM_ENUM.TAG, "[createUserDatabase | 학생 모드 인텐트 이동]");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class); //**
            startActivity(intent);
            finish();
        }


    }
    public void checkUserDatabase(final String user, final String mailDomain){

        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference reference= firebaseDatabase.getReference(VM_ENUM.DB_USERS);

        reference.orderByKey().equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    Log.d(VM_ENUM.TAG,"checkUserDatabase 계정이 없습니다. DB 등록");
                    //구글 이용자 확인된 사람정보 파이어베이스로 넘기기

                    if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){//** 선생님인 경우
                        createUserDatabase(user,VM_ENUM.TEACHER);
                        Log.d(VM_ENUM.TAG,"checkUserDatabase 선생님인 경우 DB 등록 함수 호출");
                    }else{
                        //** 학생인 경우
                        Log.d(VM_ENUM.TAG,"checkUserDatabase 학생인 경우 DB 등록 함수 호출");
                        createUserDatabase(user,VM_ENUM.STUDENT);
                    }


                }else{

                    String user_type=dataSnapshot.getChildren().iterator().next().child(VM_ENUM.DB_USER_TYPE).getValue().toString();
                    Log.d(VM_ENUM.TAG,"checkUserDatabase 이미 계정이 존재합니다. DB 등록 하지 않음, "+user_type);

                    if(user_type.equals(VM_ENUM.TEACHER)){
                        Intent intent = new Intent(getApplicationContext(), TeacherHomeActivity.class); //**
                        startActivity(intent);
                        finish();
                    }else if(user_type.equals(VM_ENUM.STUDENT)){
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class); //**
                        startActivity(intent);
                        finish();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
