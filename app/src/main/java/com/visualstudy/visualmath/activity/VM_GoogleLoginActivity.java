package com.visualstudy.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_ENUM;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class VM_GoogleLoginActivity extends AppCompatActivity {

    SignInButton Google_Login;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__google_login);

        //로그인 시도할 액티비티에서 유저데이터 요청
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //** 로그인 후, 따로 로그아웃을 하지 않았다면, 다음에 앱 켰을때, 바로 다음 화면으로 넣어주길 바란다면 코드 오픈
//        checkedLogin();

        firebaseAuth = FirebaseAuth.getInstance();
        Google_Login = findViewById(R.id.Google_Login);
        Google_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, VM_ENUM.RC_GOOGLE_LOGIN);
            }
        });
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
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d(VM_ENUM.TAG, "이름 =" + account.getDisplayName());
                Log.d(VM_ENUM.TAG, "이메일=" + account.getEmail());
                Log.d(VM_ENUM.TAG, "getId()=" + account.getId());
                Log.d(VM_ENUM.TAG, "getAccount()=" + account.getAccount());
                Log.d(VM_ENUM.TAG, "getIdToken()=" + account.getIdToken());

                //구글 이용자 확인된 사람정보 파이어베이스로 넘기기
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
            }
        }
        else {

        }
    }


    //구글 파이어베이스로 값넘기기
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //파이어베이스로 받은 구글사용자가 확인된 이용자의 값을 토큰으로 받고
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//** 아이디 생성 완료
                            Intent intent = new Intent(getApplicationContext(), VM_LoginActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(VM_GoogleLoginActivity.this, "아이디 생성완료", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(VM_GoogleLoginActivity.this, "아이디 생설실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void checkedLogin() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) { // 만약 로그인이 되어있으면 다음 액티비티 실행
            Intent intent = new Intent(getApplicationContext(), VM_LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
