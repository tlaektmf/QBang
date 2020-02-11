package com.example.visualmath.fragment;


import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
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
    public static final String TAG= VM_ENUM.TAG;
    private VM_Data_EXTRA vmDataExtra;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    private int imageViewID;
    private ImageView[] imageViewsOtherPictureList;
    private FrameLayout detail_front;
    private Button close_btn;

    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ItemDetailFragment.ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            post_id=getArguments().getString(ItemDetailFragment.ARG_ITEM_ID);
            mGlideRequestManager = Glide.with(this);
            context=getContext();

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

        detail_front=_rootView.findViewById(R.id.detail_front);
        close_btn=_rootView.findViewById(R.id.pv_close_btn);
        close_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                detail_front.setVisibility(View.GONE);
            }
        });

        imageViewID=-1;
        imageViewsOtherPictureList = new ImageView[3];
        imageViewsOtherPictureList[0] = _rootView.findViewById(R.id.extra_img_one);
        imageViewsOtherPictureList[0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Drawable.ConstantState constantState = context.getResources()
                            .getDrawable(R.drawable.add_extra_img, context.getTheme())
                            .getConstantState();
                    if(imageViewsOtherPictureList[0].getDrawable()!=null && imageViewsOtherPictureList[0].getDrawable().getConstantState()!=null){
                        if ( imageViewsOtherPictureList[0].getDrawable().getConstantState() != constantState) {
                            zoomImageFromThumb(view);
                        }
                    }else{
                        Log.d(VM_ENUM.TAG,"[problem_detail] imageViewProblem.getDrawable()  imageViewProblem.getDrawable().getConstantState() 둘중 하나 null");
                    }

                }
//                else { //현재 minSdk 23 이므로 22이하는 커버 안해도 되긴 함 (주석처리) -> 추후 대응 필요할 시 오픈
//                    if(imageViewsOtherPictureList[0].getDrawable().getConstantState() != getResources().getDrawable(R.drawable.add_extra_img).getConstantState()){
//                        zoomImageFromThumb(view);
//                    }
//                }

            }
        });
        imageViewsOtherPictureList[1] = _rootView.findViewById(R.id.extra_img_two);
        imageViewsOtherPictureList[1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Drawable.ConstantState constantState = context.getResources()
                        .getDrawable(R.drawable.add_extra_img, context.getTheme())
                        .getConstantState();

                if(imageViewsOtherPictureList[1].getDrawable()!=null && imageViewsOtherPictureList[1].getDrawable().getConstantState()!=null){
                    if ( imageViewsOtherPictureList[1].getDrawable().getConstantState() != constantState) {
                        zoomImageFromThumb(view);
                    }
                }
                else{
                    Log.d(VM_ENUM.TAG,"[problem_detail] imageViewProblem.getDrawable()  imageViewProblem.getDrawable().getConstantState() 둘중 하나 null");
                }

            }
        });
        imageViewsOtherPictureList[2] = _rootView.findViewById(R.id.extra_img_three);
        imageViewsOtherPictureList[2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Drawable.ConstantState constantState = context.getResources()
                        .getDrawable(R.drawable.add_extra_img, context.getTheme())
                        .getConstantState();
                if(imageViewsOtherPictureList[2].getDrawable()!=null && imageViewsOtherPictureList[2].getDrawable().getConstantState()!=null){
                    if ( imageViewsOtherPictureList[2].getDrawable().getConstantState() != constantState) {
                        zoomImageFromThumb(view);
                    }
                }else{
                    Log.d(VM_ENUM.TAG,"[problem_detail] imageViewProblem.getDrawable()  imageViewProblem.getDrawable().getConstantState() 둘중 하나 null");
                }

            }
        });

        setWidget();

    }
    public void zoomImageFromThumb(final View thumView){
//        Toast.makeText(thumView.getContext(),"아아아악",Toast.LENGTH_LONG).show();
        ImageView smallView = (ImageView) thumView;
        ImageView bigView = rootView.findViewById(R.id.iv_photo);

        BitmapDrawable  bitmapDrawable = (BitmapDrawable) smallView.getDrawable();
        if(bitmapDrawable!=null){
            Bitmap tmpBitmap = bitmapDrawable.getBitmap();

            if(tmpBitmap!=null){
                bigView.setImageBitmap(tmpBitmap);
                detail_front.setVisibility(View.VISIBLE);
            }else{
                Log.d(VM_ENUM.TAG,"[problem_detiail] tmpBitmap==null ");
            }

        }else{
            Log.d(VM_ENUM.TAG,"[problem_detiail] bitmapDrawable==null ");
        }


    }


    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void initData(){

        firebaseDatabase= FirebaseDatabase.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();

        reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS);
        reference=reference.child(post_id)
                .child(VM_ENUM.DB_DATA_EXTRA);
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


//        //** 프래그먼트 갱신 (가장 마지막에 해야 모든 DB 정보가 들어와서 FullActivity로 이동
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (Build.VERSION.SDK_INT >= 26) {
//            ft.setReorderingAllowed(false);
//        }
//        ft.detach(problem_detail.this).attach(problem_detail.this).commit();

    }


    public void setWidget(){

        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_TITLE)){
            textViewTitle.setText(getArguments().getString(VM_FullViewActivity.ARG_ITEM_TITLE));
        }
        if(getArguments().containsKey(VM_FullViewActivity.ARG_ITEM_GRADE)){
            textViewGrade.setText(getArguments().getString(VM_FullViewActivity.ARG_ITEM_GRADE));
        }

        if(vmDataExtra!=null){ //*** 데이터를 DB에서 읽어온 경우
            ///Toast.makeText(getContext(),"로딩완료.",Toast.LENGTH_SHORT).show();
            Log.d(VM_ENUM.TAG,"[problem_detail] 로딩완료.");

        }else {
            //데이터 읽어오는 중 => 로딩 필요
            ///Toast.makeText(getContext(), "로딩중입니다.", Toast.LENGTH_SHORT).show();
            Log.d(VM_ENUM.TAG,"[problem_detail] 로딩중입니다.");
        }

    }

}
