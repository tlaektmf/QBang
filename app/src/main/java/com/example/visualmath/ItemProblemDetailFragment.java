package com.example.visualmath;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.dummy.DummyContent;
import com.example.visualmath.dummy.TestContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemProblemDetailFragment extends Fragment {


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private String post_id;
    private String solveWay;
    View rootView;
    public static final String ARG_ITEM_ID="post_id";
    public static final String ARG_ITEM_DETAIL = "item_problem_detail";
    public static final String TAG=VM_ENUM.TAG;
    /**
     * The dummy content this fragment is presenting.
     */
    private VM_Data_Default vmDataDefault;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    public ItemProblemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            post_id=getArguments().getString(ARG_ITEM_ID);
            solveWay=getArguments().getString(VM_ENUM.DB_SOLVE_WAY);
            Log.d(TAG,post_id+","+solveWay);

            mGlideRequestManager = Glide.with(this);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

            initData();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =inflater.inflate(R.layout.fragment_item_problem_detail, container, false);

        TextView textViewTitle=(TextView)rootView.findViewById(R.id.tv_problem_title);
        TextView textViewProblemGrade= ((TextView) rootView.findViewById(R.id.tv_problem_grade));
        TextView textViewSolveWay= ((TextView) rootView.findViewById(R.id.tv_alarm));

        Button buttonViewDetail=rootView.findViewById(R.id.bt_viewDetail);
        Button buttonMatch=rootView.findViewById(R.id.bt_match);


        buttonViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(getActivity(),VM_ProblemDetailActivity.class);
                Intent intent = new Intent(getContext(), VM_FullViewActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, post_id);
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,vmDataDefault.getTitle());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,vmDataDefault.getGrade());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,vmDataDefault.getProblem());

                startActivity(intent);
            }
        });


        buttonMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** 매칭완료 다이얼로그 생성
//                     Toast.makeText(getActivity(),"다이얼로그 생성 위치",Toast.LENGTH_LONG).show();

                final VM_Dialog_match dialog= new VM_Dialog_match(rootView.getContext());

                dialog.setDialogListener(new VM_DialogListener_matchComplete(){
                    public void onButtonYes(){
                        //네 버튼
//                            Toast.makeText(getActivity(),"네",Toast.LENGTH_LONG).show();

                        //매칭 완료 toast 뷰 형태로 띄우기
                        Toast toast = Toast.makeText(getActivity(),"",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_match_complete,null));
                        toast.show();
                    }
                    public void onButtonNo(){
                        //아니오 버튼
//                            Toast.makeText(getActivity(),"아니오",Toast.LENGTH_LONG).show();

                    }
                });
                dialog.callFunction();

            }
        });


        // Show the dummy content as text in a TextView.
        if (vmDataDefault != null) {
            textViewProblemGrade.setText(vmDataDefault.getGrade());
            textViewTitle.setText(vmDataDefault.getTitle());
            if(solveWay.equals(VM_ENUM.VIDEO)){
                textViewSolveWay.setText("[영상 풀이를 원하는 학생의 질문입니다.]");
            }else if(solveWay.equals(VM_ENUM.TEXT)){
                textViewSolveWay.setText("[텍스트 풀이를 원하는 학생의 질문입니다.]");
            }

        }else{
            Log.i(TAG,"null");
        }

        return rootView;

    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void initData(){
        FirebaseDatabase firebaseDatabase;
        DatabaseReference reference;


        firebaseDatabase= FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS);
        reference=reference.child(post_id)
                .child(VM_ENUM.DB_DATA_DEFAULT);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vmDataDefault=dataSnapshot.getValue(VM_Data_Default.class);
                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] ValueEventListener : " +dataSnapshot );


                //** 사진 파일 읽기
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference pathReference=storageReference.child(vmDataDefault.getProblem());
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //** 사진파일 이미지뷰에 삽입
                        ImageView problem=(ImageView)rootView.findViewById(R.id.iv_problem_image);

                        mGlideRequestManager
                                .load(uri)
                                .into(problem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                //** 프래그먼트 갱신 (가장 마지막에 해야 모든 DB 정보가 들어와서 FullActivity로 이동
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(ItemProblemDetailFragment.this).attach(ItemProblemDetailFragment.this).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

}
