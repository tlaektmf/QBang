package com.example.visualmath;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class LogoutPreference extends Preference {
    private TextView logout_textview;
    private String current_key;
    private Boolean isClicked=false;

    public LogoutPreference(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setWidgetLayoutResource(R.layout.pre_logout);

        current_key=this.getKey();
        Log.d("key","생성 후/변경 전 키 : "+current_key);
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        logout_textview = (TextView) holder.findViewById(R.id.logout_text);
        logout_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                change_key("logout_clicked");
                isClicked=true;


                change_key();
            }
        });
//        this.setKey("logout_clicked");
//        Log.d("key","변경 후 키 : "+this.getKey());
    }
    public void change_key(){
        if(isClicked){
            this.setKey("logout_clicked");
            Log.d("key","변경 후 키 : "+this.getKey());
        }
    }
}
