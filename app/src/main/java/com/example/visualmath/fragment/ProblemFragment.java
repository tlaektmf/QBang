package com.example.visualmath.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.R;
import com.example.visualmath.VM_RegisterProblemActivity;
import com.example.visualmath.activity.HomeActivity;
import com.example.visualmath.activity.TeacherHomeActivity;
import com.example.visualmath.activity.VM_ProblemBoxActivity;
import com.example.visualmath.adapter.VM_ChatAdapter;
import com.example.visualmath.VM_DBHandler;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.example.visualmath.data.PostCustomData;
import com.example.visualmath.data.VM_Data_CHAT;
import com.example.visualmath.data.VM_Data_Default;
import com.example.visualmath.dialog.VM_DialogLIstener_chatMenu;
import com.example.visualmath.dialog.VM_Dialog_chatMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProblemFragment extends Fragment {

    //**
    private static final String TAG = VM_ENUM.TAG;

    private  String user_id;
    private String user_type;
    private String matchset_teacher;
    private String matchset_student;
    private VM_Data_Default vmDataDefault;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    //** DB
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private List<VM_Data_CHAT> chatList;

    //** 액티비티 넘어온 번들
    private String post_id;
    private String post_title;

    //** 위젯
    private TextView textViewChatRoomTitle;
    private EditText msgEditText;
    private Button sendMsgBtn;
    private Button showActionDialog;

    //>>>>>

//DashBoardFragment
// VM_ProblemBoxActivity
// 에서 ProblemFragment로 오는 경우, 채팅창 라인 비활성화
    private boolean needToBlock;

    //**학생의 unmatched 에서 오는 경우, matchset_teacher를 dB에서 찾지 않기 위한 예외처리
    private boolean fromStudentUnmatched;


    private ViewGroup rootView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private VM_ChatAdapter adapter;

    private Activity parent;

    public ProblemFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent=getActivity();
        needToBlock=false;
        fromStudentUnmatched=false;

        assert getArguments() != null;
        needToBlock=getArguments().getBoolean(VM_ENUM.IT_ARG_BLOCK);
        fromStudentUnmatched=getArguments().getBoolean(VM_ENUM.IT_FROM_UNMATCHED);
        post_id = getArguments().getString(ItemDetailFragment.ARG_ITEM_ID);
        post_title = getArguments().getString(VM_FullViewActivity.ARG_ITEM_TITLE);

        firebaseDatabase= FirebaseDatabase.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        reference=firebaseDatabase.getReference(VM_ENUM.DB_POSTS).child(post_id);

        mGlideRequestManager = Glide.with(this);
//        chatList =new ArrayList<>();

        Log.d(VM_ENUM.TAG,"[ProblemFragment] onCreate 호출");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_problem, container, false);
        msgEditText = rootView.findViewById(R.id.msgEditText);
        sendMsgBtn = rootView.findViewById(R.id.sendMsgBtn);
        showActionDialog = rootView.findViewById(R.id.showActionDialog);
        textViewChatRoomTitle = rootView.findViewById(R.id.chat_problem_title);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.chatRoomListView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        //chatList =new ArrayList<>();

        //** 채팅창 제목 설정
        textViewChatRoomTitle.setText(post_title);

        //** 유저 정보 설정
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert currentUserEmail != null;
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        user_id = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가

        if(mailDomain.equals(VM_ENUM.PROJECT_EMAIL)){
            //선생님
            user_type=VM_ENUM.TEACHER;
        }else{
            user_type=VM_ENUM.STUDENT;
        }
        Log.d(VM_ENUM.TAG,"[ProblemFragment] onCreateView 호출");
        chatList =new ArrayList<>();

        //initProblemFragment();

//** chat 데이터 생성
///*** open with addvalueListenr <<<<<<<<<<<

         reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<VM_Data_CHAT>> t = new GenericTypeIndicator<List<VM_Data_CHAT>>() {};

                Log.d(TAG, "[VM_ProblemFragment] ValueEventListener : " +dataSnapshot );
                List<VM_Data_CHAT> chats=dataSnapshot.child(VM_ENUM.DB_chatList).getValue(t);

                if(!fromStudentUnmatched){
                    matchset_student= Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_MATCH_STUDENT).getValue()).toString();
                    matchset_teacher= Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_MATCH_TEACHER).getValue()).toString();
                    Log.d(TAG, "[VM_ProblemFragment]: matchset_teacher: "+matchset_teacher+", matchset_student: "+matchset_student);
                }else{
                    Log.d(TAG, "[VM_ProblemFragment]: 매치 미완료이므로 matchset_teacher을 찾지 않음");
                }


                vmDataDefault=dataSnapshot.child(VM_ENUM.DB_DATA_DEFAULT).getValue(VM_Data_Default.class);
                Log.d(TAG, "[VM_ProblemFragment]: vmDataDefault: "+vmDataDefault);

                if(chats!=null){ //** chatList에 데이터가 있는 경우
                    chatList=chats;
                    Log.d(TAG, "[VM_ProblemFragment]: chatList가 null아님");
                    adapter = new VM_ChatAdapter(chatList, getActivity(),user_type,matchset_student,matchset_teacher);
                    recyclerView.setAdapter(adapter);
                }else{
                    //** chatList에 데이터가 없는 초기 상태의 경우
                    Log.d(TAG, "[VM_ProblemFragment]: chatList가 null임<초기상태>");
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
///*** open with addvalueListenr //>>>>>>>>>>



        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!msgEditText.getText().toString().replace(" ","").equals("")){

                    Log.d(TAG, "[VM_ProblemFragment]: 보내는 텍스트가 공백 아닌 경우");

                    //VM_Data_CHAT item=new VM_Data_CHAT(user_type, msgEditText.getText().toString());
                    //loadDatabase(post_id,item);

//                    Map<String, Object> objectMap = new HashMap<String, Object>();
//
//                    objectMap.put("chatContent", item.getChatContent());
//                    objectMap.put("sender", item.getSender());
//
//                    reference.child(VM_ENUM.DB_chatList).push().updateChildren(objectMap);

///*** open with addvalueListenr >>>>>>
                    if(chatList==null){
                        //** 초기 상태의 경우(chatList에 데이터가 하나도 없는 경우
                        Log.d(TAG, "[VM_ProblemFragment]: chatList에 데이터가 하나도 없는 경우");
                        List<VM_Data_CHAT> data = new ArrayList<>();
                        data.add(new VM_Data_CHAT(user_type, msgEditText.getText().toString()));
                        loadDatabase(post_id,data);

                    }else{
                        //** chatList에 데이터가 하나라도 있는 경우
                        ///*** open with addvalueListenr
                        chatList.add(new VM_Data_CHAT(user_type, msgEditText.getText().toString()));
                        loadDatabase(post_id,chatList);
                    }
///*** open with addvalueListenr <<<<<<<<<<<<<

                    ///chatList.add(data);
                   /// adapter.notifyDataSetChanged();
                }
                msgEditText.setText("");//채팅창 초기화

            }
        });

        showActionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** 다이얼로그 위치

                final VM_Dialog_chatMenu dig = new VM_Dialog_chatMenu(getContext());

                dig.setDialogListener(new VM_DialogLIstener_chatMenu() { //chats 에 추가 되는 내용이기 때문에, 동시 접근에 대해서 허용가능, 별도의 이벤트 처리를 하지 않음
                    @Override
                    public void onButtonCamera() {
                        Toast.makeText(getActivity(), "카메라버튼", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onButtonGallery() {
                        Toast.makeText(getActivity(), "갤러리버튼", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onButtonLive() {
                        Toast.makeText(getActivity(), "라이브버튼", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onButtonVoice() {
                        Toast.makeText(getActivity(), "음성버튼", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onButtonComplete() {
                        // 여기는 학생만 누를 수 있으므로  user_id 는 matchset_student 와 동일함 => 따라서 코드에, matchset_student대신 user_id를 그대로 사용함
                       if(problemSolveButtonEvent()){
                           Log.d(TAG,"problemSolveButtonEvent 완료");
                       }


                        Log.d(TAG,"다이얼로그 호출 시작");
                       //** 문제 완료 하면 학생은 홈화면으로 액티비티를 전환함
                        AlertDialog.Builder alert = new AlertDialog.Builder(parent);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기

                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                //parent.setResult(VM_ENUM.RC_PROBLEM_SOLVE, intent);
                                parent.startActivity(intent);
                                parent.finish();
                            }
                        });
                        alert.setMessage("문제풀이가 완료 되었습니다. \\n <질문 노트>에서 확인 가능합니다.");
                        alert.show();

                    }
                });
                dig.callFunction();
            }
        });

        //** neeToBlock 이 true면 chat창 텍스트뷰를 막는다
        if(needToBlock){
            ConstraintLayout layout = (ConstraintLayout)rootView.findViewById(R.id.chat_bottom_lay);
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(false);
            }
        }
        return rootView;
    }

    public boolean problemSolveButtonEvent(){

        Log.d(VM_ENUM.TAG,"[완료된 포스트 ID ] "+post_id);
        long time = System.currentTimeMillis();//시스템 시간
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);//사용할 포맷 정의
        String doneTime = dateFormat.format(date);//문제 완료 시간

        //   public PostCustomData(String p_id,String p_title,String grade,String problem,String time)
        PostCustomData postCustomData=new PostCustomData(post_id,vmDataDefault.getTitle(),vmDataDefault.getGrade(),vmDataDefault.getProblem(),doneTime);

        //** teacher unsolved에서 done으로 이동
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_TEACHERS)
                .child(matchset_teacher)
                .child(VM_ENUM.DB_TEA_POSTS)
                .child(VM_ENUM.DB_TEA_DONE)
                .child(post_id)
                .setValue(postCustomData);
        Log.d(TAG,matchset_teacher+"[teacher done에 저장]");

        //** student unsolved에서 done으로 이동
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS)
                .child(user_id)
                .child(VM_ENUM.DB_STU_POSTS)
                .child(VM_ENUM.DB_STU_DONE)
                .child(post_id)
                .setValue(postCustomData);
        Log.d(TAG,user_id+"[student done에 저장]");


        //** teacher unsolved에서 삭제
        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_TEACHERS)
                .child(matchset_teacher)
                .child(VM_ENUM.DB_TEA_POSTS)
                .child(VM_ENUM.DB_TEA_UNSOLVED)
                .child(post_id)
                .removeValue();
        Log.d(TAG,"[ "+ matchset_teacher+ "teacher unsolved 에서 삭제]");

        //** student unsolved에서 삭제
        FirebaseDatabase.getInstance().getReference()
                .child(VM_ENUM.DB_STUDENTS)
                .child(user_id)
                .child(VM_ENUM.DB_STU_POSTS)
                .child(VM_ENUM.DB_STU_UNSOLVED)
                .child(post_id).removeValue();

        Log.d(TAG,user_id+ "[student unsolved에서 삭제]");

        //** 완료 한 문제수 증가
        if(onSolveProblemIncrease(matchset_teacher,user_id)){
            Log.d(TAG,"[onSolveProblemIncrease 성공]");
        }
        return true;
    }
///*** open with addvalueListenr     <<<<<<<
public void loadDatabase(final String post_id, final List<VM_Data_CHAT> chatItem){

        //** 유효한 데이터인지 검사
    if(user_type.equals(VM_ENUM.TEACHER)){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference(VM_ENUM.DB_TEACHERS)
                .child(user_id)
                .child(VM_ENUM.DB_TEA_POSTS)
                .child(VM_ENUM.DB_TEA_UNSOLVED);
        ref.orderByKey().equalTo(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    Log.d(VM_ENUM.TAG,"문제가 이미 완료됨");
                    AlertDialog.Builder alert = new AlertDialog.Builder(parent);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                            Intent intent = new Intent(getContext(), TeacherHomeActivity.class);

                            parent.startActivity(intent);
                            parent.finish();
                        }
                    });
                    alert.setMessage("이미 완료된 문제입니다. 질문노트에서 확인 가능합니다.");
                    alert.show();
                }
                else{
                    VM_DBHandler dbHandler=new VM_DBHandler();
                    dbHandler.newChat(post_id,chatItem);
                    Log.d(VM_ENUM.TAG,"문제가 유효함. 채팅을 추가");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    else if(user_type.equals(VM_ENUM.STUDENT)){
        //학생이면 검사 없이 그냥 채팅 추가
        VM_DBHandler dbHandler=new VM_DBHandler();
        dbHandler.newChat(post_id,chatItem);
        Log.d(VM_ENUM.TAG,"문제가 유효함. 채팅을 추가");
    }


}
///*** open with addvalueListenr <<<<<<<

//public void loadDatabase(String post_id,VM_Data_CHAT chatItem){
//        VM_DBHandler dbHandler=new VM_DBHandler();
//        dbHandler.newChat(post_id,chatItem);
//}

private void initProblemFragment(){//** 한번만 호출
    Log.d(TAG, "[VM_ProblemFragment]:initProblemFragment 시작 ");
             reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "[VM_ProblemFragment] ValueEventListener : " +dataSnapshot );

                if(!fromStudentUnmatched){
                    matchset_student= Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_MATCH_STUDENT).getValue()).toString();
                    matchset_teacher= Objects.requireNonNull(dataSnapshot.child(VM_ENUM.DB_MATCH_TEACHER).getValue()).toString();
                    Log.d(TAG, "[VM_ProblemFragment]: matchset_teacher: "+matchset_teacher+", matchset_student: "+matchset_student);
                }else{
                    Log.d(TAG, "[VM_ProblemFragment]: 매치 미완료이므로 matchset_teacher을 찾지 않음");
                }


                vmDataDefault=dataSnapshot.child(VM_ENUM.DB_DATA_DEFAULT).getValue(VM_Data_Default.class);
                Log.d(TAG, "[VM_ProblemFragment]: vmDataDefault: "+vmDataDefault);


//                for(DataSnapshot ds : dataSnapshot.child(VM_ENUM.DB_chatList).getChildren()) {
//                    chatList.add(ds.getValue(VM_Data_CHAT.class));
//                    Log.d(TAG, "[VM_ProblemFragment]: chatList: "+ds.getValue(VM_Data_CHAT.class).getChatContent());
//                }
//                adapter = new VM_ChatAdapter(chatList, parent,user_type,matchset_student,matchset_teacher);
//                recyclerView.setAdapter(adapter);

                //** chat 데이터 생성
                reference.child(VM_ENUM.DB_chatList).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d(TAG, "<<<<<<[VM_ProblemFragment]: onChildAdded 호출>>>>>>: "+dataSnapshot);
                        String key=dataSnapshot.getKey();
                        Log.d(TAG, "[VM_ProblemFragment]: dataSnapshot.getValue(): "+ dataSnapshot.getValue());

                       // dataSnapshot.child(key).getValue();
//                        chatList.add(new VM_Data_CHAT(dataSnapshot.getValue()..toString(),
//                                dataSnapshot.child(key).child("sender").getValue().toString()));
//
//                        adapter = new VM_ChatAdapter(chatList, parent,user_type,matchset_student,matchset_teacher);
//                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}

private boolean onSolveProblemIncrease(String matchset_teacher, String user_id) {


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS).
                child(user_id).child(VM_ENUM.DB_INFO).child(VM_ENUM.DB_SOLVE_PROBLEM);

        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                if ( mutableData.getValue()== null) {

                    return Transaction.success(mutableData);
                }

                int origin_count = mutableData.getValue(Integer.class);
                // Set value and report transaction success
                mutableData.setValue(origin_count+1);
                Log.d(TAG,"[student 완료 문제수 증가]");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

        reference=FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_TEACHERS).
                child(matchset_teacher).child(VM_ENUM.DB_INFO).child(VM_ENUM.DB_SOLVE_PROBLEM);

        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                if ( mutableData.getValue()== null) {
                    Log.d(TAG,"[mutableData null]");
                    return Transaction.success(mutableData);
                }

                int origin_count = mutableData.getValue(Integer.class);
                // Set value and report transaction success
                mutableData.setValue(origin_count+1);
                Log.d(TAG,"[teacher 완료 문제수 증가]");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    return true;
    }


}
