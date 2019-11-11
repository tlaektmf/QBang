package com.example.visualmath;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class VM_DBHandler {
    private static final String TAG = "CLASS_DB";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private VM_Data_POST vm_data_post;
    private VM_Data_EXTRA vm_data_extra;
    private VM_Data_Default vm_data_default;

    private String user;

    public VM_DBHandler(String TABLE){//default constructor
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference(TABLE);
        firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언
        storageReference = FirebaseStorage.getInstance().getReference();

        if(databaseReference==null){
            //table이 없으면 생성
            databaseReference.child(TABLE);
        }else{
            //table이 있으면 진행하지 않음
        }

    }

    boolean newPost(VM_Data_ADD _vmDataAdd, VM_Data_BASIC _vmDataBasic){

        String currentUserEmail=firebaseAuth.getCurrentUser().getEmail();
        user=currentUserEmail.split("@")[0]+"_"+currentUserEmail.split("@")[1];//이메일 형식은 파이어베이스 정책상 불가
       /// Log.i(TAG,user);

        long time=System.currentTimeMillis();
        SimpleDateFormat dateFormat=new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        String uploadDate=dateFormat.format(time);
        Log.i(TAG,uploadDate);

        //** VM_Data_Extra 생성
        vm_data_extra=new VM_Data_EXTRA(_vmDataAdd);
        if(_vmDataAdd.getFilePathElement(0)!=null){
            vm_data_extra.setAdd_picture1(_vmDataAdd.getFilePathElement(0).toString());
        }
        if(_vmDataAdd.getFilePathElement(1)!=null){
            vm_data_extra.setAdd_picture2(_vmDataAdd.getFilePathElement(1).toString());
        }
        if(_vmDataAdd.getFilePathElement(2)!=null){
            vm_data_extra.setAdd_picture3(_vmDataAdd.getFilePathElement(2).toString());
        }


        //** VM_Data_Default 생성
        vm_data_default=new VM_Data_Default(_vmDataBasic.getTitle(),_vmDataBasic.getGrade(),_vmDataBasic.getProblem().toString());

        vm_data_post=
                new VM_Data_POST
                        (vm_data_default
                                ,vm_data_extra
                                , user
                                , time+""
                                , uploadDate
                                ,VM_ENUM.LIVE_NONE);


        if(vm_data_post!=null){
            databaseReference.child(vm_data_post.getP_id()).setValue(vm_data_post);
            StartupLoadFile(_vmDataAdd,_vmDataBasic);
        }

        return true;
    }

    public void StartupLoadFile(VM_Data_ADD _vmDataAdd, VM_Data_BASIC _vmDataBasic){
        //** 업로드
        if(_vmDataBasic.getProblem()!=null){
            Log.i(TAG,"problem");
            goLoad(_vmDataBasic.getProblem(),"problem");
        }

        if(_vmDataAdd!=null){
            if(_vmDataAdd.getFilePathElement(0)!=null){
                goLoad(_vmDataAdd.getFilePathElement(0),"picture1");
            }
            if(_vmDataAdd.getFilePathElement(1)!=null){
                goLoad(_vmDataAdd.getFilePathElement(1),"picture2");
            }
            if(_vmDataAdd.getFilePathElement(2)!=null){
                goLoad(_vmDataAdd.getFilePathElement(2),"picture3");
            }
        }

    }

    public void goLoad(Uri uri, String fileName){

        StorageReference riversRef = storageReference.child
                (user+"/"+
                vm_data_post.getP_id()+"/"+
                fileName);

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Log.i(TAG,"성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.i(TAG,"실패...");
                    }
                });
    }

}
