package com.example.visualmath;


import android.content.Intent;
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
import android.widget.ListView;

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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        msgEditText=rootView.findViewById(R.id.msgEditText);
        sendMsgBtn=rootView.findViewById(R.id.sendMsgBtn);
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
