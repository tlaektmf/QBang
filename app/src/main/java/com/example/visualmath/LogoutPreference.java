package com.example.visualmath;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

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

                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getParent().getContext().getApplicationContext(),VM_LauncherActivity.class);
                getParent().getContext().startActivity(intent);
            }
        });

    }

}
