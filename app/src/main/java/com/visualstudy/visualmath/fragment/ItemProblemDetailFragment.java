package com.visualstudy.visualmath.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_ENUM;
import com.visualstudy.visualmath.activity.VM_FullViewActivity;
import com.visualstudy.visualmath.activity.VM_ProblemListActivity;
import com.visualstudy.visualmath.data.AlarmItem;
import com.visualstudy.visualmath.data.PostCustomData;
import com.visualstudy.visualmath.data.VM_Data_Default;
import com.visualstudy.visualmath.dialog.VM_DialogListener_matchComplete;
import com.visualstudy.visualmath.dialog.VM_Dialog_match;
import com.visualstudy.visualmath.dialog.VM_Dialog_registerProblem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemProblemDetailFragment extends Fragment {


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public String fromUnmatched;
    public String needToBlock;

    private ImageView problem;
    private TextView textViewTitle;
    private TextView textViewProblemGrade;
    private TextView textViewSolveWay;
    private String post_id;
    private String solveWay;
    private String matchSet_student;
    private String upLoadDate;
    private VM_Data_Default data_default;
    private String user;

    View rootView;
    public static final String ARG_ITEM_ID = "post_id";

    public static final String TAG = VM_ENUM.TAG;
    /**
     * The dummy content this fragment is presenting.
     */

   /// private VM_Data_Default vmDataDefault;
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
            matchSet_student=getArguments().getString(VM_ENUM.DB_MATCH_STUDENT);
            upLoadDate=getArguments().getString(VM_ENUM.DB_UPLOAD_DATE);

            Log.d(TAG, post_id + "," + solveWay);
            parent = (VM_ProblemListActivity) getActivity();
            mGlideRequestManager = Glide.with(this);
            Activity activity = this.getActivity();

            // VM_Data_Default(String _title, String _grade, String _problem)
            data_default=new VM_Data_Default(
                    getArguments().getString(VM_ENUM.IT_POST_TITLE),
                    getArguments().getString(VM_ENUM.IT_POST_GRADE),
                    getArguments().getString(VM_ENUM.IT_PROBLEM_URI));

            if(getArguments().getString(VM_ENUM.IT_ARG_BLOCK)!=null){
                Log.d(TAG,"[needToBlock 설정]");
                needToBlock=VM_ENUM.IT_ARG_BLOCK;
            }
            if(getArguments().getString(VM_ENUM.IT_FROM_UNMATCHED)!=null){
                Log.d(TAG,"[fromUnmatched 설정]");
                fromUnmatched=VM_ENUM.IT_FROM_UNMATCHED;
            }



        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_item_problem_detail, container, false);
         problem = (ImageView) rootView.findViewById(R.id.iv_problem_image);
         textViewTitle = (TextView) rootView.findViewById(R.id.tv_problem_title);
         textViewProblemGrade = ((TextView) rootView.findViewById(R.id.tv_problem_grade));
         textViewSolveWay = ((TextView) rootView.findViewById(R.id.tv_alarm));

        Button buttonViewDetail = rootView.findViewById(R.id.bt_viewDetail);
        Button buttonMatch = rootView.findViewById(R.id.bt_match);

        initData();

        buttonViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 상세보기 화면

                Intent intent = new Intent(getContext(), VM_FullViewActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, post_id);
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE, data_default.getTitle());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE, data_default.getGrade());
                intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM, data_default.getProblem());
                intent.putExtra(VM_ENUM.IT_ARG_BLOCK, VM_ENUM.IT_ARG_BLOCK);//Teacher 문제 선택에서 넘어왔으므로, 채팅창 모두 막아야됨

                if(needToBlock!=null){ //** 매치 미완료의 경우
                    intent.putExtra(VM_ENUM.IT_ARG_BLOCK, VM_ENUM.IT_ARG_BLOCK);//사용자의 채팅창 막음
                    intent.putExtra(VM_ENUM.IT_FROM_UNMATCHED,VM_ENUM.IT_FROM_UNMATCHED);// 매치셋 생성 하지 않음
                    Log.d(TAG, "[ProblemBox] IT_ARG_BLOCK & IT_FROM_UNMATCHED " );
                }

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

                    }

                    public void onButtonNo() {

                    }
                });
                dialog.callFunction();

            }
        });


        // Show the dummy content as text in a TextView.
//        if (vmDataDefault != null) {
//            textViewProblemGrade.setText(vmDataDefault.getGrade());
//            textViewTitle.setText(vmDataDefault.getTitle());
//            if (solveWay.equals(VM_ENUM.VIDEO)) {
//                textViewSolveWay.setText("[영상 풀이를 원하는 학생의 질문입니다.]");
//            } else if (solveWay.equals(VM_ENUM.TEXT)) {
//                textViewSolveWay.setText("[텍스트 풀이를 원하는 학생의 질문입니다.]");
//            }
//
//        } else {
//            Log.i(TAG, "null");
//        }

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

                    final VM_Dialog_registerProblem checkDialog =
                            new VM_Dialog_registerProblem(parent);
                    checkDialog.callFunction(7,parent);
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            VM_Dialog_registerProblem.dig.dismiss();
                            t.cancel();
                            Intent intent = new Intent(parent, VM_ProblemListActivity.class);
                            intent.putExtra(VM_ENUM.IT_MATCH_SUCCESS, data_default.getGrade());
                            parent.startActivity(intent);
                            parent.finish();
                        }
                    }, 2000);

                } else {
                    Log.d(VM_ENUM.TAG, "문제가 유효함. dataUpdate 함수 호출 ");
                    makeMatchSet();
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
    public void makeMatchSet() {

        Log.w(VM_ENUM.TAG, "[makeMatchSet 시작]");

        //매치 셋 생성 :  public PostCustomData(String p_id,String p_title,String solveWaym ,String upLoadDate,String student,String teacher)
        postCustomData = new PostCustomData(post_id, data_default.getTitle(), solveWay, upLoadDate, matchSet_student, user);

        //** 1. UNMATCHED 에서 삭제
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_UNMATCHED)
                .child(post_id).removeValue();
        Log.d(TAG, "[UNMATCHED에서 삭제완료]");

        //** 5. POSTS 의 matchset에 설정
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_POSTS)
                .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).push().setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.w(TAG, "[POSTS 의 matchset 등록 완료] CompletionListener -> getFirstTeacher 함수 호출");
                getFirstTeacher();
            }
        });

    }


    /**
     * 가장 첫번째 matchSet으로 등록된 teacher를 반환함
     *
     * @return
     */
    public void getFirstTeacher() {

        Log.w(VM_ENUM.TAG, "[getFirstTeacher 시작]");
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
                            .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).setValue(null, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_POSTS)
                                    .child(post_id).child(VM_ENUM.DB_MATCH_TEACHER).setValue(user);

                            Log.d(TAG, "[POSTS 의 matchset (전부 삭제 후) 재등록 완료] CompletionListener");
                        }
                    });



                    //** 2. teacher unsolved 에 저장
//                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_TEACHERS)
//                            .child(user)
//                            .child(VM_ENUM.DB_TEA_POSTS)
//                            .child(VM_ENUM.DB_TEA_UNSOLVED)
//                            .child(post_id)
//                            .setValue(postCustomData);

                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_TEACHERS)
                            .child(user)
                            .child(VM_ENUM.DB_TEA_POSTS)
                            .child(VM_ENUM.DB_TEA_UNSOLVED)
                            .child(post_id)
                            .child(VM_ENUM.DB_P_ID)
                            .setValue(post_id);

                    Log.d(TAG, "[teacher unsolved 에 저장]");

                    //** 3. student unsolved에 저장
//                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS)
//                            .child(matchSet_student)
//                            .child(VM_ENUM.DB_STU_POSTS)
//                            .child(VM_ENUM.DB_STU_UNSOLVED)
//                            .child(post_id)
//                            .setValue(postCustomData);

                    FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS)
                            .child(matchSet_student)
                            .child(VM_ENUM.DB_STU_POSTS)
                            .child(VM_ENUM.DB_STU_UNSOLVED)
                            .child(post_id)
                            .child(VM_ENUM.DB_P_ID)
                            .setValue(post_id);

                    Log.d(TAG, "[student unsolved에 저장]");


                    //** 4. student unmatched에서 삭제
                    FirebaseDatabase.getInstance().getReference()
                            .child(VM_ENUM.DB_STUDENTS)
                            .child(matchSet_student)
                            .child(VM_ENUM.DB_STU_POSTS)
                            .child(VM_ENUM.DB_STU_UNMATCHED)
                            .child(post_id).removeValue();

                    Log.d(TAG, "[student unmatched에서 삭제]");


                    //** 5. 학생 알람에 등록
                    AlarmItem alarmItem=new AlarmItem(post_id,data_default.getTitle(),VM_ENUM.ALARM_MATCHED);
                    FirebaseDatabase.getInstance().getReference()
                            .child(VM_ENUM.DB_STUDENTS)
                            .child(matchSet_student)
                            .child(VM_ENUM.DB_STU_ALARM)
                            .push().setValue(alarmItem);
                    Log.d(TAG, "[student 알람에 등록]");

                    //매치완료 ->문제선택 화면으로 다시 전환
//                    Toast toast = Toast.makeText(parent, "", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_match_complete, null));
//                    toast.show();

                    final VM_Dialog_registerProblem checkDialog =
                            new VM_Dialog_registerProblem(parent);
                    checkDialog.callFunction(5,parent);
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            VM_Dialog_registerProblem.dig.dismiss();
                            t.cancel();
                            Log.d(VM_ENUM.TAG, "매치완료 ->문제선택 화면으로 다시 전환");
                            Intent intent = new Intent(parent, VM_ProblemListActivity.class);
                            intent.putExtra(VM_ENUM.IT_MATCH_SUCCESS, data_default.getGrade());
                            parent.startActivity(intent);
                            parent.finish();
                        }
                    }, 2000);

                } else {
                    //아닌경우
                    Log.d(TAG, "[자신이 first matchSet teacher 아님]");
                    final VM_Dialog_registerProblem checkDialog =
                            new VM_Dialog_registerProblem(parent);
                    checkDialog.callFunction(7,parent);
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            VM_Dialog_registerProblem.dig.dismiss();
                            t.cancel();
                            // ->문제선택 화면으로 다시 전환
                            Intent intent = new Intent(parent, VM_ProblemListActivity.class);
                            intent.putExtra(VM_ENUM.IT_MATCH_SUCCESS, data_default.getGrade());
                            parent.startActivity(intent);
                            parent.finish();

                        }
                    }, 2000);
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
//    public void initData() {
//
//        FirebaseDatabase firebaseDatabase;
//        DatabaseReference reference;
//
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        reference = firebaseDatabase.getReference(VM_ENUM.DB_POSTS);
//        reference = reference.child(post_id);
//        //.child(VM_ENUM.DB_DATA_DEFAULT);
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                vmDataDefault = dataSnapshot.child(VM_ENUM.DB_DATA_DEFAULT).getValue(VM_Data_Default.class);
//                matchSet_student = dataSnapshot.child(VM_ENUM.DB_MATCH_STUDENT).getValue(String.class);
//                upLoadDate = dataSnapshot.child(VM_ENUM.DB_UPLOAD_DATE).getValue(String.class);
//
//                if (vmDataDefault != null) {
//                    textViewProblemGrade.setText(vmDataDefault.getGrade());
//                    textViewTitle.setText(vmDataDefault.getTitle());
//                    if (solveWay.equals(VM_ENUM.VIDEO)) {
//                        textViewSolveWay.setText("[영상 풀이를 원하는 학생의 질문입니다.]");
//                    } else if (solveWay.equals(VM_ENUM.TEXT)) {
//                        textViewSolveWay.setText("[텍스트 풀이를 원하는 학생의 질문입니다.]");
//                    }
//                } else {
//                    Log.i(TAG, "null");
//                }
//
//                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] ValueEventListener : " + dataSnapshot);
//                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] matchSet_student : " + matchSet_student);
//                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] upLoadDate : " + upLoadDate);
//
//                //** 사진 파일 읽기
//                StorageReference storageReference;
//                storageReference = FirebaseStorage.getInstance().getReference();
//                StorageReference pathReference = storageReference.child(vmDataDefault.getProblem());
//                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        //** 사진파일 이미지뷰에 삽입
//
//                        mGlideRequestManager
//                                .load(uri)
//                                .into(problem);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        int errorCode=((StorageException)e).getErrorCode();
//                        if(errorCode==StorageException.ERROR_QUOTA_EXCEEDED){
//                            Log.d(VM_ENUM.TAG,"[ItemProblemDetailFragment]StorageException.ERROR_QUOTA_EXCEEDED");
//                            Toast.makeText( getContext(),"저장소 용량이 초과되었습니다",Toast.LENGTH_SHORT).show();
//                            problem.setImageResource(R.drawable.ic_warning_error_svgrepo_com);
//                        }
//                    }
//                });
//
//
////                //** 프래그먼트 갱신 (가장 마지막에 해야 모든 DB 정보가 들어와서 FullActivity로 이동
////                FragmentTransaction ft = getFragmentManager().beginTransaction();
////                if (Build.VERSION.SDK_INT >= 26) {
////                    ft.setReorderingAllowed(false);
////                }
////                ft.detach(ItemProblemDetailFragment.this).attach(ItemProblemDetailFragment.this).commit();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                ///Toast.makeText(parent.getBaseContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
//                Log.d(VM_ENUM.TAG, "<<<<<<Failed to read value>>>>>>>>", databaseError.toException());
//            }
//        });
//
//    }


    public void initData() {

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

                if (data_default != null) {
                    textViewProblemGrade.setText(data_default.getGrade());
                    textViewTitle.setText(data_default.getTitle());
                    if (solveWay.equals(VM_ENUM.VIDEO)) {
                        textViewSolveWay.setText("[영상 풀이를 원하는 학생의 질문입니다.]");
                    } else if (solveWay.equals(VM_ENUM.TEXT)) {
                        textViewSolveWay.setText("[텍스트 풀이를 원하는 학생의 질문입니다.]");
                    }
                } else {
                    Log.i(TAG, "[선생님 문제 선택/ Detail 뷰] initData() > data_default 가 null");
                }

                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] matchSet_student : " + matchSet_student);
                Log.d(TAG, "[선생님 문제 선택/ Detail 뷰] upLoadDate : " + upLoadDate);

                //** 사진 파일 읽기
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference pathReference = storageReference.child(data_default.getProblem());
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //** 사진파일 이미지뷰에 삽입

                        mGlideRequestManager
                                .load(uri)
                                .into(problem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        int errorCode=((StorageException)e).getErrorCode();
                        if(errorCode==StorageException.ERROR_QUOTA_EXCEEDED){
                            Log.d(VM_ENUM.TAG,"[ItemProblemDetailFragment]StorageException.ERROR_QUOTA_EXCEEDED");
                            Toast.makeText( getContext(),"저장소 용량이 초과되었습니다",Toast.LENGTH_SHORT).show();
                            problem.setImageResource(R.drawable.ic_warning_error_svgrepo_com);
                        }
                    }
                });


//                //** 프래그먼트 갱신 (가장 마지막에 해야 모든 DB 정보가 들어와서 FullActivity로 이동
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                if (Build.VERSION.SDK_INT >= 26) {
//                    ft.setReorderingAllowed(false);
//                }
//                ft.detach(ItemProblemDetailFragment.this).attach(ItemProblemDetailFragment.this).commit();
            }




}
