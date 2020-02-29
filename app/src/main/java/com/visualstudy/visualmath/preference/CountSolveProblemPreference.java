package com.visualstudy.visualmath.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_ENUM;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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

        //** 유저 정보 설정
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        String user_id = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가
        String user_type;
        if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
            //선생님
            user_type=VM_ENUM.DB_TEACHERS;
            readDB(user_id,user_type);
        }else{
            user_type=VM_ENUM.DB_STUDENTS;
            readDB(user_id,user_type);
        }





    }

    public void readDB(final String user_id, String user_type) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(user_type).child(user_id).child(VM_ENUM.DB_INFO);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String solve_num= dataSnapshot.child(VM_ENUM.DB_SOLVE_PROBLEM).getValue().toString();
                Log.d(VM_ENUM.TAG,user_id+", 해결한 문제수 "+solve_num);
                textViewCount.setText(solve_num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
