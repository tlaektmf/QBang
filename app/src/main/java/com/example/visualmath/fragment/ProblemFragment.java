package com.example.visualmath.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    private boolean needToBlock;
    private ViewGroup rootView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private VM_ChatAdapter adapter;



    public ProblemFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        needToBlock=false;
        needToBlock=getArguments().getBoolean(VM_ENUM.IT_ARG_BLOCK);
        post_id = getArguments().getString(ItemDetailFragment.ARG_ITEM_ID);
        post_title = getArguments().getString(VM_FullViewActivity.ARG_ITEM_TITLE);

        firebaseDatabase= FirebaseDatabase.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        reference=firebaseDatabase.getReference("POSTS").child(post_id);

        mGlideRequestManager = Glide.with(this);
        chatList =new ArrayList<>();
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

        //** chat 데이터 생성

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<VM_Data_CHAT>> t = new GenericTypeIndicator<List<VM_Data_CHAT>>() {};

                Log.d(TAG, "[VM_ProblemFragment] ValueEventListener : " +dataSnapshot );
                List<VM_Data_CHAT> chats=dataSnapshot.child(VM_ENUM.DB_chatList).getValue(t);
                matchset_teacher=dataSnapshot.child(VM_ENUM.DB_MATCH_TEACHER).getValue().toString();
                Log.d(TAG, "[VM_ProblemFragment]: matchset_teacher: "+matchset_teacher);
                vmDataDefault=dataSnapshot.child(VM_ENUM.DB_DATA_DEFAULT).getValue(VM_Data_Default.class);
                Log.d(TAG, "[VM_ProblemFragment]: vmDataDefault: "+vmDataDefault);

                if(chats!=null){ //** chatList에 데이터가 있는 경우
                    chatList=chats;
                    Log.d(TAG, "[VM_ProblemFragment]: chatList가 null아님");
                    adapter = new VM_ChatAdapter(chatList, getActivity());
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



        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!msgEditText.getText().toString().replace(" ","").equals("")){

                    Log.d(TAG, "[VM_ProblemFragment]: 보내는 텍스트가 공백 아닌 경우");



                    if(chatList==null){
                        //** 초기 상태의 경우(chatList에 데이터가 하나도 없는 경우
                        Log.d(TAG, "[VM_ProblemFragment]: chatList에 데이터가 하나도 없는 경우");
                        List<VM_Data_CHAT> data = new ArrayList<>();
                        data.add(new VM_Data_CHAT("student", msgEditText.getText().toString()));
                        loadDatabase(post_id,data);

                    }else{
                        //** chatList에 데이터가 하나라도 있는 경우
                        chatList.add(new VM_Data_CHAT("student", msgEditText.getText().toString()));
                        loadDatabase(post_id,chatList);
                    }
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

                dig.setDialogListener(new VM_DialogLIstener_chatMenu() {
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
                        Toast.makeText(getActivity(), "문제풀이가 완료 되었습니다. \\n <질문 노트>에서 확인 가능합니다.", Toast.LENGTH_LONG).show();
                        Log.d(VM_ENUM.TAG,"[완료된 포스트 ID ] "+post_id);
                        long time = System.currentTimeMillis();//시스템 시간
                        Date date = new Date(time);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);//사용할 포맷 정의
                        String doneTime = dateFormat.format(date);//문제 완료 시간

                        //   public PostCustomData(String p_id,String p_title,String grade,String problem,String time)
                        PostCustomData postCustomData=new PostCustomData(post_id,vmDataDefault.getTitle(),vmDataDefault.getGrade(),vmDataDefault.getProblem(),doneTime);

                        //** teacher unsolved에서 done으로 이동
                        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS)
                                .child(matchset_teacher)
                                .child(VM_ENUM.DB_STU_POSTS)
                                .child(VM_ENUM.DB_STU_DONE)
                                .child(post_id)
                                .setValue(postCustomData);
                        Log.d(TAG,"[teacher done에 저장]");

                        //** student unsolved에서 done으로 이동
                        FirebaseDatabase.getInstance().getReference().child(VM_ENUM.DB_STUDENTS)
                                .child(user_id)
                                .child(VM_ENUM.DB_STU_POSTS)
                                .child(VM_ENUM.DB_STU_DONE)
                                .child(post_id)
                                .setValue(postCustomData);
                        Log.d(TAG,"[student done에 저장]");


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

                        Log.d(TAG,"[student unsolved에서 삭제]");
                    }
                });
                dig.callFunction();
            }
        });

        //** neeToBlock 이 true면 chat창 텍스트뷰를 막는다
        if(needToBlock==true){
            ConstraintLayout layout = (ConstraintLayout)rootView.findViewById(R.id.chat_bottom_lay);
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(false);
            }
        }
        return rootView;
    }

public void loadDatabase(String post_id,List<VM_Data_CHAT> chatItem){
        VM_DBHandler dbHandler=new VM_DBHandler();
        dbHandler.newChat(post_id,chatItem);
}

}
