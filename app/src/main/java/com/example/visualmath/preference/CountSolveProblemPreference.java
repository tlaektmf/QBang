package com.example.visualmath.preference;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.visualmath.R;
import com.example.visualmath.activity.VM_LauncherActivity;
import com.google.firebase.auth.FirebaseAuth;

public class CountSolveProblemPreference extends Preference {
    private TextView textViewCount;


    public CountSolveProblemPreference(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setWidgetLayoutResource(R.layout.pre_count);

    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        textViewCount = (TextView) holder.findViewById(R.id.solve_cnt_tv);

        textViewCount.setText("5");

    }

}
