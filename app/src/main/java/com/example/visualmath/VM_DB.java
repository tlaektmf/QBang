package com.example.visualmath;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

public class VM_DB {
    private static final String TAG = "CLASS_DB";
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    VM_DB(){
        init();
    }
    void init(){
        firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언
        storageReference = FirebaseStorage.getInstance().getReference(); //파이어베이스 저장소
        firebaseDatabase = FirebaseDatabase.getInstance();//파이어베이스 데이터 베이스
    }

    boolean createPost(VM_Data_ADD _vmDataAdd,VM_Data_BASIC _vmDataBasic){

        String currentUserEmail=firebaseAuth.getCurrentUser().getEmail();
        String user=currentUserEmail.split("@")[0]+"_"+currentUserEmail.split("@")[1];//이메일 형식은 파이어베이스 정책상 불가
        Log.i(TAG,user);
        databaseReference=firebaseDatabase.getReference();
        SimpleDateFormat dateFormat=new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());


        VM_Data_POST vm_data_post=new VM_Data_POST(user,_vmDataBasic.getProblem(),"1", dateFormat.toString(),VM_ENUM.LIVE_NONE);
        if(vm_data_post!=null){
            databaseReference.child("posts").child(vm_data_post.getP_id().toString()).setValue(vm_data_post.getState());
        }
        return true;
    }


}
