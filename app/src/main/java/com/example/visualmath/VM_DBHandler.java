package com.example.visualmath;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.logging.Handler;

public class VM_DBHandler {
    private static final String TAG = "CLASS_DB";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    Handler handler;

    public VM_DBHandler(String TABLE){//default constructor
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference(TABLE);
        firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언

        if(databaseReference==null){
            //table이 없으면 생성
            databaseReference.child(TABLE);
        }else{
            //table이 있으면 진행하지 않음
        }

    }

    boolean newPost(VM_Data_ADD _vmDataAdd, VM_Data_BASIC _vmDataBasic){

        String currentUserEmail=firebaseAuth.getCurrentUser().getEmail();
        String user=currentUserEmail.split("@")[0]+"_"+currentUserEmail.split("@")[1];//이메일 형식은 파이어베이스 정책상 불가
        Log.i(TAG,user);

        SimpleDateFormat dateFormat=new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        long t=System.currentTimeMillis();

        VM_Data_POST vm_data_post=new VM_Data_POST(_vmDataBasic,_vmDataAdd,
                user,_vmDataBasic.getProblem(), t+"" , dateFormat.toString(),VM_ENUM.LIVE_NONE);

        if(vm_data_post!=null){
            Log.i(TAG,vm_data_post.getState()+"");
            databaseReference.child(vm_data_post.getP_id()).setValue(vm_data_post);


        }
        return true;
    }

}
