package com.example.visualmath;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class SolveFragment extends Fragment {

   /// private final static String storagePath="gs://visualmath-92ae4.appspot.com";
    private ViewGroup rootView;
    private TextView textViewTitle;
    private TextView textViewGrade;
    private ImageView imageViewProblem;

    ///private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    ///private FirebaseAuth firebaseAuth;

    ///private String user;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    public SolveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_solve, container, false);
        init(rootView);
        //return inflater.inflate(R.layout.fragment_solve, container, false);
        return rootView;
    }

    public void init(ViewGroup _rootView){
         textViewTitle=_rootView.findViewById(R.id.tv_title);
         textViewGrade=_rootView.findViewById(R.id.tv_grade);
         imageViewProblem=_rootView.findViewById(R.id.iv_file_problem);
        mGlideRequestManager = Glide.with(this);
        ///firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언
        ///firebaseStorage = FirebaseStorage.getInstance();

        //** 이렇게 해도됨
        //storageReference = firebaseStorage.getReferenceFromUrl(storagePath);
        storageReference = FirebaseStorage.getInstance().getReference();

        //현재 사용자 정보
        ///String currentUserEmail=firebaseAuth.getCurrentUser().getEmail();
        ///user=currentUserEmail.split("@")[0]+"_"+currentUserEmail.split("@")[1];//이메일 형식은 파이어베이스 정책상 불가

        //Activity 참조 DB에서 base 읽어오기
        setWidget();
    }
    public void setWidget(){

        //** 데이터베이스 read
        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_TITLE)){
            textViewTitle.setText(getArguments().getString(VM_FullViewActivity.ARG_ITEM_TITLE));
        }
        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_GRADE)){
            textViewGrade.setText(getArguments().getString(VM_FullViewActivity.ARG_ITEM_GRADE));
        }
        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_PROBLEM)){

            //** storage 파일 가져오기

            //다운로드할 파일을 가르키는 참조 만들기
            StorageReference pathReference = storageReference.child(getArguments().getString(VM_FullViewActivity.ARG_ITEM_PROBLEM));
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mGlideRequestManager
                            .load(uri)
                            .into(imageViewProblem);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }
}
