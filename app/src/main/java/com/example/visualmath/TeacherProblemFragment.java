package com.example.visualmath;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherProblemFragment extends Fragment {
    private static final String TAG = "TeacherProblemFragment";
    ViewGroup rootView;
    private Vector<VM_Data_CHAT> chats;
    private VM_ChatAdapter adapter;
    int count = -1;

    private EditText msgEditText;
    private Button sendMsgBtn;
    private Button showActionDialog;

    public TeacherProblemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_teacher_problem, container, false);

        RecyclerView recyclerView=(RecyclerView)rootView.findViewById(R.id.chatRoomListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        chats = new Vector<>();

        ceateData();

        adapter = new VM_ChatAdapter( chats,getActivity());
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        msgEditText=rootView.findViewById(R.id.teacher_msgEditText);
        sendMsgBtn=rootView.findViewById(R.id.teacher_sendMsgBtn);
        showActionDialog=rootView.findViewById(R.id.teacher_showActionDialog);

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"okay");
                count++;

                VM_Data_CHAT data=new VM_Data_CHAT("괴력몬", msgEditText.getText().toString(),0, 0);
                chats.add(data);
                adapter.notifyDataSetChanged();
            }
        });

        showActionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** 다이얼로그 위치

                final  VM_Dialog_chatMenu_teacher dig = new VM_Dialog_chatMenu_teacher(getContext());

                dig.setDialogListener(new VM_DialogLIstener_chatMenu_teacher() {

                    @Override
                    public void onButtonCamera() {
                        Toast.makeText(getActivity(),"카메라버튼",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onButtonGallery() {
                        Toast.makeText(getActivity(),"갤러리버튼",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onButtonSetTime() {
                        Toast.makeText(getActivity(),"시간정하기버튼",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onButtonVoice() {
                        Toast.makeText(getActivity(),"음성버튼",Toast.LENGTH_LONG).show();
                    }
                });
                dig.callFunction();

            }
        });

        return rootView;

    }

    public void ceateData(){

        //** 더미 데이터
        String fName="괴력몬";
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
                chats.add(new VM_Data_CHAT("괴력몬", "푸는 방법을 알려드릴게요",0,0));
                chats.add(new VM_Data_CHAT("괴력몬", "감사합니다~",1,R.drawable.img_contract));
                break;
        }

    }
}
