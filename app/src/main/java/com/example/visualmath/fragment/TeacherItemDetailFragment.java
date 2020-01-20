package com.example.visualmath.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.preference.LogoutPreference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
public class TeacherItemDetailFragment extends Fragment {


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "post_id";
    private static final String TAG = "TeacherItemDetail";
    private String post_id;
    View rootView;
    /**
     * The dummy content this fragment is presenting.
     */
    private VM_Data_Default vmDataDefault;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    public TeacherItemDetailFragment() {
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
            Log.d(TAG,post_id);

            mGlideRequestManager = Glide.with(this);
            Activity activity = this.getActivity();

            initData();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         rootView =inflater.inflate(R.layout.fragment_teacher_item_detail, container, false);

        TextView title=(TextView)rootView.findViewById(R.id.tv_title);
        TextView grade=(TextView)rootView.findViewById(R.id.tv_grade);
        TextView alarm=(TextView)rootView.findViewById(R.id.tv_alarm);


        // Show the dummy content as text in a TextView.
        if (vmDataDefault != null) {//*** 데이터를 DB에서 읽어온 경우
            title.setText(vmDataDefault.getTitle());
            grade.setText(vmDataDefault.getGrade());
            alarm.setText(VM_ENUM.SOLVED_ALARM_MESSAGE);
            Toast.makeText(getContext(),"로딩완료.",Toast.LENGTH_SHORT).show();
        }else{ //데이터 읽어오는 중 => 로딩 필요
            Toast.makeText(getContext(),"로딩중입니다.",Toast.LENGTH_SHORT).show();
        }

        //***프래그먼트의 "상세보기" 버튼 클릭 시 부모 액티비티에서 다른 인텐트 시작
        Button goto_detal_btn = (Button)rootView.findViewById(R.id.teacher_show_detail_btn);
        goto_detal_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(getContext(), VM_FullViewActivity.class);
                intent.putExtra(TeacherItemDetailFragment.ARG_ITEM_ID, post_id);
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,vmDataDefault.getTitle());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,vmDataDefault.getGrade());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,vmDataDefault.getProblem());

                startActivity(intent);
            }
        });

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
        reference=firebaseDatabase.getReference("POSTS");
        reference=reference.child(post_id)
                .child("data_default");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vmDataDefault=dataSnapshot.getValue(VM_Data_Default.class);
                Log.d(TAG, "ValueEventListener : " +dataSnapshot );


                //** 사진 파일 읽기
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference pathReference=storageReference.child(vmDataDefault.getProblem());
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //** 사진파일 이미지뷰에 삽입
                        ImageView problem=(ImageView)rootView.findViewById(R.id.iv_problem);

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
                ft.detach(TeacherItemDetailFragment.this).attach(TeacherItemDetailFragment.this).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

}
