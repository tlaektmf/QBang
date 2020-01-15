package com.example.visualmath;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;

import com.google.firebase.auth.FirebaseAuth;

public class VM_SettingsActivity extends AppCompatActivity {


    static VM_SettingsActivity parent;
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

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.my_preferences, rootKey);
//            addPreferencesFromResource(R.xml.my_preferences);

//            텍스트만 클릭
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View row = inflater.inflate(R.layout.pre_logout, null);
//
//            final TextView logout_text = row.findViewById(R.id.logout_text);
//            Log.d("로그아웃","로그아웃 : " +logout_text.getText());
//            logout_text.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getContext(), "제에에발2", Toast.LENGTH_LONG).show();
//                }
//            });

//            프레퍼런스 클릭
            Preference logout_preference = findPreference("logout");
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(logout_preference.getLayoutResource(), null);
            final TextView logout_text = row.findViewById(R.id.logout_text);
            Log.d("로그아웃","가져오시죠 : " +logout_text.getText());

            logout_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("로그아웃","가져오시죠2 : " +logout_text.getText());
                    Toast.makeText(getContext(),"프레퍼런스 텍스트만 클릭",Toast.LENGTH_LONG).show();
                }
            });

//            logout_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//               @Override
//               public boolean onPreferenceClick(Preference preference) {
//                   Toast.makeText(getContext(),"프레퍼런스 전체 클릭",Toast.LENGTH_LONG).show();
//                   return false;
//               }
//           });
        }

        // 각각의 Fragment마다 Instance를 반환해 줄 메소드를 생성합니다.
        public static SettingsFragment newInstance() {

            return new SettingsFragment();
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            Log.d(VM_ENUM.TAG, "[클릭된 Preference의 key] " + key);

            if (key.equals(VM_ENUM.PRE_TEST)) { //** Preference에 저장된 user 정보 삭제

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                Log.d(VM_ENUM.TAG, "[Preference] //저장된 user name :" + SaveSharedPreference.getUserName(getActivity().getApplicationContext()));
                editor.clear();
                editor.apply();//commit은 즉시 반영, 따라서 apply함수를 쓰는 게 안전
            }
            if (key.equals(VM_ENUM.PRE_LOGOUT)) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent=new Intent(getActivity().getApplicationContext(),VM_LauncherActivity.class);
//                startActivity(intent);

            }
            if(key.equals("logout")){
                Log.d("key_settingActivity","프레퍼런스 텍스트 클릭하기 전");
            }
            if(key.equals("logout_clicked")){
                Log.d("key_settingActivity","프레퍼런스 텍스트 클릭 후");
            }

            return false;

        }

    }

}