package com.example.visualmath;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.visualmath.dummy.AlarmItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.dummy.DummyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "post_id";
    private static final String TAG = "ItemDetail";
    private String post_id;
    View rootView;

    /**
     * The dummy content this fragment is presenting.
     */
    private VM_Data_Default vmDataDefault;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            post_id=getArguments().getString(ARG_ITEM_ID);
            Log.d(TAG,post_id);

            initData();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (vmDataDefault != null) {

            TextView title=(TextView)rootView.findViewById(R.id.tv_title);
            title.setText(vmDataDefault.getTitle());
            TextView grade=(TextView)rootView.findViewById(R.id.tv_grade);
            grade.setText(vmDataDefault.getGrade());
            TextView alarm=(TextView)rootView.findViewById(R.id.tv_alarm);
            alarm.setText(VM_ENUM.SOLVED_ALARM_MESSAGE);

        }


//        프래그먼트의 "상세보기" 버튼 클릭 시 부모 액티비티에서 다른 인텐트 시작
        Button goto_detal_btn = (Button)rootView.findViewById(R.id.show_detail_btn);
        goto_detal_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), VM_FullViewActivity.class);
//                intent.putExtra("UID", userId);
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

                        Glide.with(ItemDetailFragment.this)
                                .load(uri)
                                .into(problem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                //** 프래그먼트 갱신
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(ItemDetailFragment.this).attach(ItemDetailFragment.this).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }
}
