package com.example.visualmath.preference;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.visualmath.R;
import com.example.visualmath.activity.VM_LauncherActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutPreference extends Preference {
    private TextView logout_textview;


    public LogoutPreference(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setWidgetLayoutResource(R.layout.pre_logout);

    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        logout_textview = (TextView) holder.findViewById(R.id.logout_text);
        logout_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getParent().getContext().getApplicationContext(), VM_LauncherActivity.class);
                getParent().getContext().startActivity(intent);
            }
        });

    }

}
