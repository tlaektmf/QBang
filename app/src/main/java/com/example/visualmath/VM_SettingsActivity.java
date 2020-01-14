package com.example.visualmath;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

public class VM_SettingsActivity extends AppCompatActivity {

    TextView buttonLogOut;
    VM_SettingsActivity parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        parent=this;

        buttonLogOut = findViewById(R.id.logout_text);
//        buttonLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
//                alt_bld.setMessage("로그아웃 하시겠습니까?").setCancelable(false)
//                        .setPositiveButton("네",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        FirebaseAuth.getInstance().signOut();
//                                        Intent intent=new Intent(parent,VM_LauncherActivity.class);
//                                        startActivity(intent);
//                                    }
//                                }).setNegativeButton("아니오",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//            }
//        });

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.my_preferences, rootKey);



        }

        // 각각의 Fragment마다 Instance를 반환해 줄 메소드를 생성합니다.
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }



    }
}