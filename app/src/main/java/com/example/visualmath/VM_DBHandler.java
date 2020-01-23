package com.example.visualmath;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.visualmath.data.VM_Data_ADD;
import com.example.visualmath.data.VM_Data_BASIC;
import com.example.visualmath.data.VM_Data_CHAT;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.data.VM_Data_EXTRA;
import com.example.visualmath.data.VM_Data_POST;
import com.example.visualmath.data.VM_Data_STUDENT;
import com.example.visualmath.data.VM_Data_TEACHER;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VM_DBHandler {
    private static final String TAG = VM_ENUM.TAG;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private VM_Data_POST vm_data_post;
    private VM_Data_EXTRA vm_data_extra;
    private VM_Data_Default vm_data_default;

    private String user;

    public VM_DBHandler() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언
        storageReference = FirebaseStorage.getInstance().getReference();
    }

//    public VM_DBHandler(String TABLE){//default constructor
//
////        databaseReference=firebaseDatabase.getReference(TABLE);
//
//
//
////        if(databaseReference==null){
////            //table이 없으면 생성
////            Log.i(TAG,"[POSTS TABLE 생성] ");
////            databaseReference.child(TABLE);
////        }else{
////            //table이 있으면 진행하지 않음
////            Log.i(TAG,"[POSTS TABLE 이미 있음] ");
////        }
//
//    }


///*** open with addvalueListenr     <<<<<<<
//    public void newChat(String post_id, List<VM_Data_CHAT> chat) {
//
//        databaseReference = firebaseDatabase.getReference(VM_ENUM.DB_POSTS).child(post_id).child(VM_ENUM.DB_chatList);
//        databaseReference.setValue(chat); //** 파이어베이스 DB 등록
//
//    }
///*** open with addvalueListenr <<<<<<<

    public void newChat(String post_id, VM_Data_CHAT chat) {

        databaseReference = firebaseDatabase.getReference(VM_ENUM.DB_POSTS).child(post_id).child(VM_ENUM.DB_chatList);
        databaseReference.push().setValue(chat); //** 파이어베이스 DB 등록

    }

    /**
     * USERS 객체 생성
     *
     * @param user_email
     * @param user_type
     */
    public void newUser(String user_email, String user_type) {
        databaseReference = firebaseDatabase.getReference(VM_ENUM.DB_USERS);
        if (databaseReference == null) {
            //table이 없으면 생성
            databaseReference.child(VM_ENUM.DB_USERS);
            Log.i(TAG, "[USERS TABLE 생성] ");
        }

        //** 학생과 선생님을 구분해서 유저 객체 생성
        long time = System.currentTimeMillis();//시스템 시간
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);//사용할 포맷 정의
        String joinDate = dateFormat.format(date);

        Log.i(TAG, "[유저 최초 가입 날짜:] " + joinDate);

        if (user_type.equals(VM_ENUM.TEACHER)) {
            VM_Data_TEACHER teacher = new VM_Data_TEACHER(user_email, joinDate);

            databaseReference.child(user_email).child(VM_ENUM.DB_T_ID).setValue(user_email); //** 파이어베이스 DB 등록
            databaseReference.child(user_email).child(VM_ENUM.DB_USER_TYPE).setValue(user_type); //** 파이어베이스 DB 등록
            Log.i(TAG, "[USERS instance 생성] ");

            databaseReference = firebaseDatabase.getReference(VM_ENUM.DB_TEACHERS);
            databaseReference.child(user_email).child(VM_ENUM.DB_INFO).setValue(teacher); //** 파이어베이스 DB 등록
            Log.i(TAG, "[TEACHERS instance 생성] ");

        } else if (user_type.equals(VM_ENUM.STUDENT)) {
            VM_Data_STUDENT student = new VM_Data_STUDENT(user_email, joinDate);

            databaseReference.child(user_email).child(VM_ENUM.DB_S_ID).setValue(user_email); //** 파이어베이스 DB 등록
            databaseReference.child(user_email).child(VM_ENUM.DB_USER_TYPE).setValue(user_type); //** 파이어베이스 DB 등록
            Log.i(TAG, "[USERS instance 생성] ");

            databaseReference = firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS);
            databaseReference.child(user_email).child(VM_ENUM.DB_INFO).setValue(student); //** 파이어베이스 DB 등록
            Log.i(TAG, "[STUDENTS instance 생성] ");
        }


    }

    /**
     * POST 객체 생성
     *
     * @param _vmDataAdd
     * @param _vmDataBasic
     * @param _solveWay
     * @return
     */
    boolean newPost(VM_Data_ADD _vmDataAdd, VM_Data_BASIC _vmDataBasic, String _solveWay) {


        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가


        long time = System.currentTimeMillis();//시스템 시간
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);//사용할 포맷 정의
        String uploadDate = dateFormat.format(date);

        Log.i(TAG, "[포스트 업로드 날짜:] " + uploadDate);

        //** VM_Data_Extra 생성
        if (_vmDataAdd != null) {
            vm_data_extra = new VM_Data_EXTRA(_vmDataAdd);
            if (_vmDataAdd.getFilePathElement(0) != null) {
                vm_data_extra.setAdd_picture1(_vmDataAdd.getFilePathElement(0).toString());
            }
            if (_vmDataAdd.getFilePathElement(1) != null) {
                vm_data_extra.setAdd_picture2(_vmDataAdd.getFilePathElement(1).toString());
            }
            if (_vmDataAdd.getFilePathElement(2) != null) {
                vm_data_extra.setAdd_picture3(_vmDataAdd.getFilePathElement(2).toString());
            }
        }


        //** VM_Data_Default 생성
        vm_data_default = new VM_Data_Default(_vmDataBasic.getTitle(), _vmDataBasic.getGrade(), _vmDataBasic.getProblem().toString());

        //** 최종 post 데이터 생성 => DB에 저장
        /*
            public VM_Data_POST(VM_Data_Default _vm_data_default,
                        VM_Data_EXTRA _vm_data_extra,
                        String _s_id,
                        String _key,
                        String _uploadDate,
                        String _solveWay)
         */
        vm_data_post =
                new VM_Data_POST
                        (vm_data_default
                                , vm_data_extra
                                , user
                                , Long.toString(time)
                                , uploadDate
                                , _solveWay);


        if (vm_data_post != null) { //파이어베이스와 저장소에 데이터 로딩

            //** TABLE : POSTS 에 등록
            firebaseDatabase.getReference(VM_ENUM.DB_POSTS).child(vm_data_post.getP_id()).setValue(vm_data_post); //** 파이어베이스 DB 등록
            StartupLoadFile(_vmDataAdd, _vmDataBasic); //** 파이어베이스 저장소에 파일 등록


            firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED).child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_P_ID).setValue(vm_data_post.getP_id());
            firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED).child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_UPLOAD_DATE).setValue(vm_data_post.getUploadDate());
            firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED).child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_SOLVE_WAY).setValue(vm_data_post.getSolveWay());
            firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED).child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_TITLE).setValue(vm_data_post.getData_default().getTitle());
            firebaseDatabase.getReference(VM_ENUM.DB_UNMATCHED).child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_GRADE).setValue(vm_data_post.getData_default().getGrade());
            Log.i(TAG, "[UNMATCHED instance 생성] ");

            //** TABLE : STUDENTS - child(unmatched)에 등록
            firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                    .child(user)
                    .child(VM_ENUM.DB_STU_POSTS)
                    .child(VM_ENUM.DB_STU_UNMATCHED)
                    .child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_P_ID).setValue(vm_data_post.getP_id()); //** 파이어베이스 DB 등록
            firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                    .child(user)
                    .child(VM_ENUM.DB_STU_POSTS)
                    .child(VM_ENUM.DB_STU_UNMATCHED)
                    .child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_TITLE).setValue(vm_data_post.getData_default().getTitle()); //** 파이어베이스 DB 등록
            firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                    .child(user)
                    .child(VM_ENUM.DB_STU_POSTS)
                    .child(VM_ENUM.DB_STU_UNMATCHED)
                    .child(vm_data_post.getP_id())
                    .child(VM_ENUM.DB_UPLOAD_DATE).setValue(vm_data_post.getUploadDate()); //** 파이어베이스 DB 등록

            Log.i(TAG, "[STUDENTS - child(unmatched) instance 생성] ");

        }


        return true;
    }


    /**
     * @param _vmDataAdd   내용추가뷰에서 넘어온 데이터
     * @param _vmDataBasic 문제등록뷰에서 넘어온 데이터
     */
    public void StartupLoadFile(VM_Data_ADD _vmDataAdd, VM_Data_BASIC _vmDataBasic) {
        //** 업로드

        if (_vmDataBasic.getProblem() != null) {
            storageFileLoad(_vmDataBasic.getProblem(), "problem");
        }

        if (_vmDataAdd != null) {
            if (_vmDataAdd.getFilePathElement(0) != null) {
                storageFileLoad(_vmDataAdd.getFilePathElement(0), "picture1");
            }
            if (_vmDataAdd.getFilePathElement(1) != null) {
                storageFileLoad(_vmDataAdd.getFilePathElement(1), "picture2");
            }
            if (_vmDataAdd.getFilePathElement(2) != null) {
                storageFileLoad(_vmDataAdd.getFilePathElement(2), "picture3");
            }
        }

    }

    /**
     * storage에 파일을 업로드함
     *
     * @param uri
     * @param fileName
     */
    public void storageFileLoad(Uri uri, String fileName) {

        StorageReference riversRef = storageReference.child
                (user + "/" +
                        vm_data_post.getP_id() + "/" +
                        fileName);

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Log.i(TAG, "[파이어베이스 저장소 저장] 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.i(TAG, "[파이어베이스 저장소 저장] 실패...");
                    }
                });
    }

}
