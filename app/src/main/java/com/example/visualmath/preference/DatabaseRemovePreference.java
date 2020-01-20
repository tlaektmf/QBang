package com.example.visualmath.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;

public class DatabaseRemovePreference extends Preference {
    private TextView all_preference_delete_textview;


    public DatabaseRemovePreference(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setWidgetLayoutResource(R.layout.pre_test);

    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        all_preference_delete_textview = (TextView) holder.findViewById(R.id.all_preference_delete);
        all_preference_delete_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getParent().getContext());
                SharedPreferences.Editor editor = preferences.edit();
                Log.d(VM_ENUM.TAG, "[Preference] //저장된 user name :" + SaveSharedPreference.getUserName(getParent().getContext()));
                editor.clear();
                editor.apply();//commit은 즉시 반영, 따라서 apply함수를 쓰는 게 안전
            }
        });

    }

}
