package com.example.visualmath;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VM_ChatAdapter extends  RecyclerView.Adapter<VM_ChatAdapter.VM_CustomViewHolder> {

    private static final String TAG ="VM_Adapter" ;
    private List<VM_Data_CHAT> chatList;
    private Context context;

    public class VM_CustomViewHolder extends RecyclerView.ViewHolder {

        //위젯
        protected ImageView friendImgView;
        protected TextView friendMsgTxtView;
        protected TextView myMsgTxtView;
        protected View friendChatLayout;
        protected View myChatLayout;

        public VM_CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            //위젯
            this.friendImgView=itemView.findViewById(R.id.friendImgView);
            this.friendMsgTxtView=itemView.findViewById(R.id.friendMsgTxtView);
            this.myMsgTxtView=itemView.findViewById(R.id.myMsgTxtView);
            this.myChatLayout=itemView.findViewById(R.id.myChatLayout);
            this.friendChatLayout= itemView.findViewById(R.id.friendChatLayout);
        }
    }

    public VM_ChatAdapter(List<VM_Data_CHAT> _chatList, Context context) {
        this.chatList = _chatList;
        this.context=context;
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
        int chatSize=chatList.size();

        for(int i=0;i<chatSize;i++){
            Log.d("data",chatList.get(i).getSender());

            if(chatList.get(i).getSender().equals("student")){

                holder.friendChatLayout.setVisibility(View.GONE);
                holder.myChatLayout.setVisibility(View.VISIBLE);
                holder.myMsgTxtView.setText(chatList.get(position).getChatContent());
            }else if(chatList.get(i).getSender().equals("teacher")){

                holder.friendChatLayout.setVisibility(View.VISIBLE);
                holder.myChatLayout.setVisibility(View.GONE);
                holder.friendMsgTxtView.setText(chatList.get(position).getChatContent());

                if(Build.VERSION.SDK_INT >= 21) {
                    holder.friendImgView.setClipToOutline(true);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return (null != this.chatList ? chatList.size() : 0);
    }


}


