package com.visualstudy.visualmath.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_ENUM;
import com.visualstudy.visualmath.activity.VM_LauncherActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutPreference extends Preference {
    private Button logout_textview;


    public LogoutPreference(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setWidgetLayoutResource(R.layout.pre_logout);

    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        logout_textview = (Button) holder.findViewById(R.id.logout_text);
        logout_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //** 로그아웃의 경우, 자동로그인 정보 삭제후 로그아웃
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getParent().getContext());
                SharedPreferences.Editor editor = preferences.edit();
                Log.d(VM_ENUM.TAG, "[Preference] //저장된 user name :" + SaveSharedPreference.getUserName(getParent().getContext()));
                editor.clear();
                editor.apply();//commit은 즉시 반영, 따라서 apply함수를 쓰는 게 안전

                //로그아웃
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getParent().getContext().getApplicationContext(), VM_LauncherActivity.class);
                getParent().getContext().startActivity(intent);
            }
        });

    }

}
