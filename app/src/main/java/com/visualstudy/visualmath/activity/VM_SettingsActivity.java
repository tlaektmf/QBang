package com.visualstudy.visualmath.activity;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.visualstudy.visualmath.R;

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

        }

        // 각각의 Fragment마다 Instance를 반환해 줄 메소드를 생성합니다.
        public static SettingsFragment newInstance() {

            return new SettingsFragment();
        }

//        @Override
//        public boolean onPreferenceTreeClick(Preference preference) {
//            String key = preference.getKey();
//            Log.d(VM_ENUM.TAG, "[클릭된 Preference의 key] " + key);
//
//            if (key.equals(VM_ENUM.PRE_TEST)) { //** Preference에 저장된 user 정보 삭제
//
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//                SharedPreferences.Editor editor = preferences.edit();
//                Log.d(VM_ENUM.TAG, "[Preference] //저장된 user name :" + SaveSharedPreference.getUserName(getActivity().getApplicationContext()));
//                editor.clear();
//                editor.apply();//commit은 즉시 반영, 따라서 apply함수를 쓰는 게 안전
//            }
//            if (key.equals(VM_ENUM.PRE_LOGOUT)) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent=new Intent(getActivity().getApplicationContext(),VM_LauncherActivity.class);
//                startActivity(intent);
//
//            }
//
//            return false;
//
//        }

    }

}