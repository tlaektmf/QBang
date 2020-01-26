package com.example.visualmath.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.visualmath.R;
import com.example.visualmath.VM_DBHandler;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.VM_RegisterProblemActivity;
import com.example.visualmath.activity.TeacherHomeActivity;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.example.visualmath.activity.VM_LoginActivity;
import com.example.visualmath.activity.VM_ProblemListActivity;
import com.example.visualmath.activity.VM_TeacherSolveProblemActivity;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.dialog.VM_DialogListener_matchComplete;
import com.example.visualmath.dialog.VM_Dialog_match;
import com.example.visualmath.dialog.VM_Dialog_registerProblem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
    private String matchSet_student;
    private String upLoadDate;
    View rootView;
    public static final String ARG_ITEM_ID = "post_id";
    public static final String ARG_ITEM_DETAIL = "item_problem_detail";
    public static final String TAG = VM_ENUM.TAG;
    /**
     * The dummy content this fragment is presenting.
     */
    private VM_Data_Default vmDataDefault;
    private PostCustomData postCustomData;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;
    public Activity parent;

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

            post_id = getArguments().getString(ARG_ITEM_ID);
            solveWay = getArguments().getString(VM_ENUM.DB_SOLVE_WAY);
            Log.d(TAG, post_id + "," + solveWay);
            parent = (VM_ProblemListActivity) getActivity();
            mGlideRequestManager = Glide.with(this);
            Activity activity = this.getActivity();

            initData();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_item_problem_detail, container, false);

        TextView textViewTitle = (TextView) rootView.findViewById(R.id.tv_problem_title);
        TextView textViewProblemGrade = ((TextView) rootView.findViewById(R.id.tv_problem_grade));
        TextView textViewSolveWay = ((TextView) rootView.findViewById(R.id.tv_alarm));

        Button buttonViewDetail = rootView.findViewById(R.id.bt_viewDetail);
        Button buttonMatch = rootView.findViewById(R.id.bt_match);


        buttonViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), VM_FullViewActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, post_id);
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE, vmDataDefault.getTitle());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE, vmDataDefault.getGrade());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM, vmDataDefault.getProblem());
                intent.putExtra(VM_ENUM.IT_ARG_BLOCK, VM_ENUM.IT_ARG_BLOCK);//Teacher 문제 선택에서 넘어왔으므로, 채팅창 모두 막아야됨
                startActivity(intent);
            }
        });


        buttonMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** 매칭완료 다이얼로그 생성

                final VM_Dialog_match dialog= new VM_Dialog_match(rootView.getContext());
                final VM_Dialog_registerProblem checkDialog = new VM_Dialog_registerProblem(rootView.getContext());

                dialog.setDialogListener(new VM_DialogListener_matchComplete() {
                    public void onButtonYes() {
                        matchProgress();
                        //checkDialog.callFunction(5);
                    }

                    public void onButtonNo() {

                    }
                });
                dialog.callFunction();

            }
        });


        // Show the dummy content as text in a TextView.
        if (vmDataDefault != null) {
            textViewProblemGrade.setText(vmDataDefault.getGrade());
            textViewTitle.setText(vmDataDefault.getTitle());
            if (solveWay.equals(VM_ENUM.VIDEO)) {
                textViewSolveWay.setText("[영상 풀이를 원하는 학생의 질문입니다.]");
            } else if (solveWay.equals(VM_ENUM.TEXT)) {
                textViewSolveWay.setText("[텍스트 풀이를 원하는 학생의 질문입니다.]");
            }

        } else {
            Log.i(TAG, "null");
        }

        return rootView;

    }

    public void matchProgress() { //** 데이터를 unmatched에서 검사

        Log.d(VM_ENUM.TAG, "[matchProgress 시작]");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(VM_ENUM.DB_UNMATCHED);
        ref.orderByKey().equalTo(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    Log.d(VM_ENUM.TAG, "이미 누가 가져감");
                    AlertDialog.Builder alert = new AlertDialog.Builder(parent);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // ->문제선택 화면으로 다시 전환
                            Intent intent = new Intent(parent, VM_ProblemListActivity.class);
                            intent.putExtra(VM_ENUM.IT_MATCH_SUCCESS, vmDataDefault.getGrade());
                            parent.startActivity(intent);
                            parent.finish();
                            dialog.dismiss();     //닫기

                        }
                    });
                    alert.setMessage("이미 매치 완료된 문제입니다.");
                    alert.show();

                } else {
                    Log.d(VM_ENUM.TAG, "문제가 유효함. dataUpdate 함수 호출 ");
                    dataUpdate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }

    /**
     *
     */
    public void dataUpdate() { //DB를 업데이트 함
        Log.d(VM_ENUM.TAG, "[dataUpdate 시작]");
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        String user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        if (makeMatchSet(user)) {
            getFirstTeacher(user);
        }
    }


    public boolean makeMatchSet(String user) {

        Log.d(VM_ENUM.TAG, "[makeMatchSet 시작]");

        //매치 셋 생성 :  public PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate,String student,String teacher)
        postCustomData = new PostCustomData(post_id, vmDataDefault.getTitle(), solveWay, upLoadDate, matchSet_student, user);

        //>>> UNMATCHED 에서의 삭제는 mutable 로 해야되기 때문에 isDataAvailable() 함수에서 조건문에 따라 처리함
        //** 1. UNMATCHED 에서 삭제
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_UNMATCHED)
                .child(post_id).removeValue();
        Log.d(TAG, "[UNMATCHED에서 삭제완료]");

        //** 5. POSTS 의 matchset에 설정
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_POSTS)
                .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).push().setValue(user);
        Log.d(TAG, "[POSTS 의 matchset 등록 완료]");

        return true;
    }


    /**
     * 가장 첫번째 matchSet으로 등록된 teacher를 반환함
     *
     * @return
     */
    public void getFirstTeacher(final String user) {

        Log.d(VM_ENUM.TAG, "[getFirstTeacher 시작]");
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_POSTS)
                .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ///Log.d(VM_ENUM.TAG, "[getFirstTeacher]" + dataSnapshot.getChildren().iterator().next().getValue());
                String firstTeacher = dataSnapshot.getChildren().iterator().next().getValue().toString();

                if (firstTeacher.equals(user)) {
                    //자신이 first matchSet teacher이면
                    Log.d(TAG, "[자신이 first matchSet teacher]");

                    //** 1. POSTS 의 matchset에 설정
                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_POSTS)
                            .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).setValue(null);

                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_POSTS)
                            .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).setValue(user);

                    Log.d(TAG, "[POSTS 의 matchset 재등록 완료]");

                    //** 2. teacher unsolved 에 저장
                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_TEACHERS)
                            .child(user)
                            .child(VM_ENUM.DB_TEA_POSTS)
                            .child(VM_ENUM.DB_TEA_UNSOLVED)
                            .child(post_id)
                            .setValue(postCustomData);
                    Log.d(TAG, "[teacher unsolved 에 저장]");

                    //** 3. student unsolved에 저장
                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS)
                            .child(matchSet_student)
                            .child(VM_ENUM.DB_STU_POSTS)
                            .child(VM_ENUM.DB_STU_UNSOLVED)
                            .child(post_id)
                            .setValue(postCustomData);
                    Log.d(TAG, "[student unsolved에 저장]");


                    //** 4. student unmatched에서 삭제
                    FirebaseDatabase.getInstance().getReference()
                            .child(VM_ENUM.DB_STUDENTS)
                            .child(matchSet_student)
                            .child(VM_ENUM.DB_STU_POSTS)
                            .child(VM_ENUM.DB_STU_UNMATCHED)
                            .child(post_id).removeValue();

                    Log.d(TAG, "[student unmatched에서 삭제]");

                    //매치완료 ->문제선택 화면으로 다시 전환
//                    Toast toast = Toast.makeText(parent, "", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_match_complete, null));
//                    toast.show();

                    AlertDialog.Builder alert = new AlertDialog.Builder(parent);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                            Log.d(VM_ENUM.TAG, "매치완료 ->문제선택 화면으로 다시 전환");
                            Intent intent = new Intent(parent, VM_ProblemListActivity.class);
                            intent.putExtra(VM_ENUM.IT_MATCH_SUCCESS, vmDataDefault.getGrade());
                            parent.startActivity(intent);
                            parent.finish();
                        }
                    });
                    alert.setMessage("매치 완료");
                    alert.show();



                } else {
                    //아닌경우
                    Log.d(TAG, "[자신이 first matchSet teacher 아님]");
                    AlertDialog.Builder alert = new AlertDialog.Builder(parent);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // ->문제선택 화면으로 다시 전환
                            Intent intent = new Intent(parent, VM_ProblemListActivity.class);
                            intent.putExtra(VM_ENUM.IT_MATCH_SUCCESS, vmDataDefault.getGrade());
                            parent.startActivity(intent);
                            parent.finish();

                            dialog.dismiss();     //닫기

                        }
                    });
                    alert.setMessage("이미 매치 완료된 문제입니다.");
                    alert.show();
                }

                Log.d(TAG, "[getFirstTeacher 완료]");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void initData() {
        FirebaseDatabase firebaseDatabase;
        DatabaseReference reference;


        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference(VM_ENUM.DB_POSTS);
        reference = reference.child(post_id);
        //.child(VM_ENUM.DB_DATA_DEFAULT);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vmDataDefault = dataSnapshot.child(VM_ENUM.DB_DATA_DEFAULT).getValue(VM_Data_Default.class);
                matchSet_student = dataSnapshot.child(VM_ENUM.DB_MATCH_STUDENT).getValue(String.class);
                upLoadDate = dataSnapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue(String.class);
                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] ValueEventListener : " + dataSnapshot);
                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] matchSet_student : " + matchSet_student);
                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] upLoadDate : " + upLoadDate);

                //** 사진 파일 읽기
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference pathReference = storageReference.child(vmDataDefault.getProblem());
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //** 사진파일 이미지뷰에 삽입
                        ImageView problem = (ImageView) rootView.findViewById(R.id.iv_problem_image);

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
                Toast.makeText(getActivity().getBaseContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });

    }

}
