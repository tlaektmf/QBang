package com.example.visualmath;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProblemFragment extends Fragment {

    private static final String TAG = "ProblemFragment";
    ViewGroup rootView;
    private Vector<VM_Data_CHAT> chats;
    private VM_ChatAdapter adapter;
    int count = -1;

    private EditText msgEditText;
    private Button sendMsgBtn;
    private Button showActionDialog;

    private int returnResult;
    private static final int GALLERY=1;
    private static final int CAMERA=2;
    private static final int NOTHING=-1;

    private static final int PICK_FROM_ALBUM = 1; //onActivityResult 에서 requestCode 로 반환되는 값
    private static final int PICK_FROM_CAMERA = 2;
    private static final int OTHER_DATA_LOAD=3;

    private File galleryFile; //갤러리로부터 받아온 이미지를 저장
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( {시간})
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = timeStamp ;

        // 이미지가 저장될 폴더 이름 ( userID )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/"+"userID"+"/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;

    }
    public void getAlbumFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM); //앨범 화면으로 이동
        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */
    }
    public void takePhoto(){
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_problem, container, false);
        View view=inflater.inflate(R.layout.fragment_problem, container, false);

        RecyclerView recyclerView=(RecyclerView)rootView.findViewById(R.id.chatRoomListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        chats = new Vector<>();

       ceateData();

        adapter = new VM_ChatAdapter( chats,getActivity());
        recyclerView.setAdapter(adapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);

        msgEditText=rootView.findViewById(R.id.msgEditText);
        sendMsgBtn=rootView.findViewById(R.id.sendMsgBtn);
        showActionDialog=rootView.findViewById(R.id.showActionDialog);

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"okay");
                count++;

                VM_Data_CHAT data=new VM_Data_CHAT("근육몬", msgEditText.getText().toString(),0, 0);
                chats.add(data);
                adapter.notifyDataSetChanged();
            }
        });

        showActionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** 다이얼로그 위치
                //Toast.makeText(getActivity(),"다이얼로그 생성 위치",Toast.LENGTH_LONG).show();

                final VM_Dialog_PickHowToGetPicture dialog = new VM_Dialog_PickHowToGetPicture(getContext());

                // 커스텀 다이얼로그의 결과를 담을 매개변수로 같이 넘겨준다.

                dialog.setDialogListener(new VM_DialogListener_PickHowToGetPicture() {
                    @Override
                    public void onButtonTakePhotoClicked() {
                        returnResult=CAMERA;
                        dialog.setReturnResult(CAMERA);
                        takePhoto();
                    }

                    @Override
                    public void onButtonGetAlbumFileClicked() {
                        returnResult=GALLERY;
                        dialog.setReturnResult(GALLERY);
                        getAlbumFile();
                    }
                });
                dialog.callFunction();// 커스텀 다이얼로그를 호출
            }
        });

        return rootView;
        //return inflater.inflate(R.layout.fragment_problem, container, false);
    }


    public void ceateData(){

        //** 더미 데이터
        String fName="근육몬";
        switch (fName) {
            case "근육몬":
                chats.add(new VM_Data_CHAT("근육몬", "어떤 문제를 도와줄까요?",0, 0));
                chats.add(new VM_Data_CHAT("근육몬", "안녕~",1, R.drawable.img_video));
                chats.add(new VM_Data_CHAT("근육몬", "반가워요.",0, 0));
                chats.add(new VM_Data_CHAT("근육몬", "이렇게 푸세용~",1, R.drawable.img_video));
                break;
            case "괴력몬":
                chats.add(new VM_Data_CHAT("괴력몬", "안녕하세요",0 ,0));
                chats.add(new VM_Data_CHAT("괴력몬", "이문제를 잘 모르겠어요~",1,R.drawable.img_contract));
                chats.add(new VM_Data_CHAT("괴력몬", "감사합니다",0,0));
                break;
        }

    }


}
