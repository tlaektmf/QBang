package com.example.visualmath;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProblemFragment extends Fragment {

    //**
    private static final String TAG = VM_ENUM.TAG;
    private static final int CAMERA = 1;
    private static final int GALLERY = 2;
    private static final int LIVE = 3;
    private static final int VIDEO = 4;
    private static final int COMPLETE = 5;
    private static final int NOTHING = -1;

    private static final int PICK_FROM_ALBUM = 1; //onActivityResult 에서 requestCode 로 반환되는 값
    private static final int PICK_FROM_CAMERA = 2;
    private static final int OTHER_DATA_LOAD = 3;


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


    private ViewGroup rootView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private VM_ChatAdapter adapter;

    private File galleryFile; //갤러리로부터 받아온 이미지를 저장

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( {시간})
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = timeStamp;

        // 이미지가 저장될 폴더 이름 ( userID )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + "userID" + "/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;

    }

    public void getAlbumFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM); //앨범 화면으로 이동
        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent를 통해 카메라 화면으로 이동함

        try {
            galleryFile = createImageFile(); //파일 경로가 담긴 빈 이미지 생성
        } catch (IOException e) {
            //Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            //finish();
            e.printStackTrace();
        }

        if (galleryFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(getContext(),
                        "com.example.visualmath.provider", galleryFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {
                Uri photoUri = Uri.fromFile(galleryFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //galleryFile 의 Uri경로를 intent에 추가 -> 카메라에서 찍은 사진이 저장될 주소를 의미
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }

        }

    }

    public ProblemFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        post_id = getArguments().getString(ItemDetailFragment.ARG_ITEM_ID);
        post_title = getArguments().getString(VM_FullViewActivity.ARG_ITEM_TITLE);

        firebaseDatabase= FirebaseDatabase.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        reference=firebaseDatabase.getReference("POSTS");
        reference=reference.child(post_id)
                .child("chatList");


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

        //** chat 데이터 생성

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<VM_Data_CHAT>> tmp = new GenericTypeIndicator<List<VM_Data_CHAT>>() {};

                Log.d(TAG, "[VM_ProblemFragment] ValueEventListener : " +dataSnapshot );
                if(dataSnapshot.getValue(tmp)!=null){ //** chatList에 데이터가 있는 경우
                    chatList=dataSnapshot.getValue(tmp);
                    Log.d(TAG, "[VM_ProblemFragment]: chatList가 null아님");
                    adapter = new VM_ChatAdapter(chatList, getActivity());
                    recyclerView.setAdapter(adapter);
                }else{
                    //** chatList에 데이터가 없는 초기 상태의 경우
                    Log.d(TAG, "[VM_ProblemFragment]: chatList가 null임<초기상태>");
                }


                //** 리스트뷰에 반영
                ///adapter.notifyDataSetChanged();

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
                        Toast.makeText(getActivity(), "완료버튼", Toast.LENGTH_LONG).show();

                    }
                });
                dig.callFunction();
            }
        });

        return rootView;
    }

public void loadDatabase(String post_id,List<VM_Data_CHAT> chatItem){
        VM_DBHandler dbHandler=new VM_DBHandler();
        dbHandler.newChat(post_id,chatItem);
}

}
