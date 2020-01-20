package com.example.visualmath.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.R;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.example.visualmath.data.VM_Data_EXTRA;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class problem_detail extends Fragment {

    private ViewGroup rootView;
    private TextView textViewTitle;
    private TextView textViewGrade;
    private TextView textViewContent;

    private ImageView imageViewOther1;
    private ImageView imageViewOther2;
    private ImageView imageViewOther3;

    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private String post_id;
    public static final String TAG="problem_detail_fr";
    private VM_Data_EXTRA vmDataExtra;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ItemDetailFragment.ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            post_id=getArguments().getString(ItemDetailFragment.ARG_ITEM_ID);
            mGlideRequestManager = Glide.with(this);

            initData();//DB는 한번만 읽음
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_problem_detail, container, false);
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_problem_detail, container, false);
        init(rootView);
        return rootView;
    }

    public void init(ViewGroup _rootView){
        textViewTitle=_rootView.findViewById(R.id.detail_frag_tv_grade);//** 위치 그냥바꿈..
        textViewGrade=_rootView.findViewById(R.id.detail_frag_tv_title);
        textViewContent=_rootView.findViewById(R.id.tv_question);
        imageViewOther1=_rootView.findViewById(R.id.extra_img_one);
        imageViewOther2=_rootView.findViewById(R.id.extra_img_two);
        imageViewOther3=_rootView.findViewById(R.id.extra_img_three);

        setWidget();

    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void initData(){

        firebaseDatabase= FirebaseDatabase.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();

        reference=firebaseDatabase.getReference("POSTS");
        reference=reference.child(post_id)
                .child("data_extra");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vmDataExtra=dataSnapshot.getValue(VM_Data_EXTRA.class);
                Log.d(TAG, "ValueEventListener : " +dataSnapshot );

                if(vmDataExtra==null){//** <내용추가뷰>에서 채운 데이터가 없더라도 "질문내용" 부분에는 Default 메시지 필요
                    textViewContent.setText("추가 질문 내용이 없습니다.");
                }
                if(vmDataExtra!=null){
                    if(vmDataExtra.getContent()!=null){
                        textViewContent.setText(vmDataExtra.getContent());
                    }else{//추가 설명이 없는 경우
                        textViewContent.setText("추가 질문 내용이 없습니다.");
                    }

                    //*** 사진 파일 읽기
                    //** storage 파일 가져오기

                    //다운로드할 파일을 가르키는 참조 만들기
                    StorageReference pathReference;

                    if(vmDataExtra.getAdd_picture1()!=null){

                        pathReference = storageReference.child(vmDataExtra.getAdd_picture1());
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mGlideRequestManager
                                        .load(uri)
                                        .into(imageViewOther1);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    //두번째 사진
                    if(vmDataExtra.getAdd_picture2()!=null){
                        pathReference = storageReference.child(vmDataExtra.getAdd_picture2());
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mGlideRequestManager
                                        .load(uri)
                                        .into(imageViewOther2);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    //세번째 사진
                    if(vmDataExtra.getAdd_picture3()!=null){
                        pathReference = storageReference.child(vmDataExtra.getAdd_picture3());
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mGlideRequestManager
                                        .load(uri)
                                        .into(imageViewOther3);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });


        //** 프래그먼트 갱신 (가장 마지막에 해야 모든 DB 정보가 들어와서 FullActivity로 이동
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(problem_detail.this).attach(problem_detail.this).commit();

    }


    public void setWidget(){

        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_TITLE)){
            textViewTitle.setText(getArguments().getString(VM_FullViewActivity.ARG_ITEM_TITLE));
        }
        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_GRADE)){
            textViewGrade.setText(getArguments().getString(VM_FullViewActivity.ARG_ITEM_GRADE));
        }

        if(vmDataExtra!=null){ //*** 데이터를 DB에서 읽어온 경우
            Toast.makeText(getContext(),"로딩완료.",Toast.LENGTH_SHORT).show();

        }else{
            //데이터 읽어오는 중 => 로딩 필요
            Toast.makeText(getContext(),"로딩중입니다.",Toast.LENGTH_SHORT).show();
        }


    }
}
