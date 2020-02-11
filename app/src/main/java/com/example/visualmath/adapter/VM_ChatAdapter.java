package com.example.visualmath.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.data.VM_Data_CHAT;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VM_ChatAdapter extends RecyclerView.Adapter<VM_ChatAdapter.VM_CustomViewHolder> {

    private static final String TAG = VM_ENUM.TAG;
    private List<VM_Data_CHAT> chatList;
    private Context context;
    private String userType;
    private String matchSet_student;
    private String matchSet_teacher;

    public class VM_CustomViewHolder extends RecyclerView.ViewHolder {

        //위젯
        private ImageView friendImgView;
        private TextView friendMsgTxtView;
        private TextView myMsgTxtView;
        private View friendChatLayout;
        private View myChatLayout;
        private TextView friendName;

        //** 내 채팅창 동영상 버젼
        private View myChatLayoutVideoVersion;

        public VM_CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            //위젯
            this.friendImgView = itemView.findViewById(R.id.friendImgView);
            this.friendMsgTxtView = itemView.findViewById(R.id.friendMsgTxtView);
            this.myMsgTxtView = itemView.findViewById(R.id.myMsgTxtView);
            this.myChatLayout = itemView.findViewById(R.id.myChatLayout);
            this.friendChatLayout = itemView.findViewById(R.id.friendChatLayout);
            this.friendName=itemView.findViewById(R.id.friend_name_tv);

            //** 내 채팅창 동영상 버젼
            this.myChatLayoutVideoVersion=itemView.findViewById(R.id.myChatLayoutVideoVersion);
        }
    }

    public VM_ChatAdapter(List<VM_Data_CHAT> _chatList, Context context,String userType,String matchSet_student, String matchSet_teacher) {
        this.chatList = _chatList;

        if(chatList!=null){
            if(chatList.size()==0){
                Log.d(TAG, "[VM_ChatAdapter/VM_ChatAdapter()]: chatList가 비어있음");
            }
        }

        this.context = context;
        this.userType=userType;
        this.matchSet_student=matchSet_student;
        this.matchSet_teacher=matchSet_teacher;
        Log.d(TAG, "[VM_ChatAdapter/VM_ChatAdapter()]:"+matchSet_student+","+matchSet_teacher);
    }

    @NonNull
    @Override
    public VM_ChatAdapter.VM_CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);

        VM_CustomViewHolder viewHolder = new VM_CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VM_ChatAdapter.VM_CustomViewHolder holder, int position) {
        int chatSize = chatList.size();

        Log.d(TAG, "[VM_ChatAdapter] chatsize" + chatSize + "");

        if(chatList==null){
            Log.d(TAG, "[VM_ChatAdapter]: chatList가 null임");
        }

        if(userType.equals(VM_ENUM.TEACHER)){ //유저가 선생인 경우
            if (chatList.get(position).getSender().equals(VM_ENUM.TEACHER)) { //보내는 이가 학생

                holder.friendChatLayout.setVisibility(View.GONE);

                String flag=chatList.get(position).getType();
                Log.d(TAG, "[VM_ChatAdapter] Flag"+flag);
                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.myChatLayout.setVisibility(View.VISIBLE);
                        holder.myMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        break;
                }


            } else if (chatList.get(position).getSender().equals(VM_ENUM.STUDENT)) {//보내는 이가 나(선생)인 경우

                String flag=chatList.get(position).getType();
                holder.myChatLayout.setVisibility(View.GONE);
                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.friendChatLayout.setVisibility(View.VISIBLE);
                        holder.friendMsgTxtView.setText(chatList.get(position).getChatContent());

                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        break;
                }
                holder.friendName.setText(this.matchSet_student);
                holder.friendImgView.setImageResource(R.drawable.student);

            }
        }else if(userType.equals(VM_ENUM.STUDENT)){//유저가 학생인 경우
            if (chatList.get(position).getSender().equals(VM_ENUM.TEACHER)) { //보내는 이가 선생인 경우
                holder.myChatLayout.setVisibility(View.GONE);
                String flag=chatList.get(position).getType();
                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.friendChatLayout.setVisibility(View.VISIBLE);
                        holder.friendMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        break;
                }


                holder.friendName.setText(this.matchSet_teacher);
                holder.friendImgView.setImageResource(R.drawable.teacher);

            } else if (chatList.get(position).getSender().equals(VM_ENUM.STUDENT)) {//보내는 이가 나(학생)인 경우
                holder.friendChatLayout.setVisibility(View.GONE);

                String flag=chatList.get(position).getType();
                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.myChatLayout.setVisibility(View.VISIBLE);
                        holder.myMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        break;
                }





            }
        }



    }

    @Override
    public int getItemCount() {
        return (null != this.chatList ? chatList.size() : 0);
    }


}


