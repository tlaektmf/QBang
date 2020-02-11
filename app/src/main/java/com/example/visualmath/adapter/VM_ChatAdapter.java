package com.example.visualmath.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.data.VM_Data_CHAT;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VM_ChatAdapter extends RecyclerView.Adapter<VM_ChatAdapter.VM_CustomViewHolder> {

    private static final String TAG = VM_ENUM.TAG;
    private List<VM_Data_CHAT> chatList;
    private Context context;
    private String userType;
    private String matchSet_student;
    private String matchSet_teacher;

    ///Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

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
        private VideoView myMsgVideoView;

        //** 내 채팅창 이미지 버젼
        private View myChatLayoutImageVersion;
        private PhotoView myMsgPhotoView;

        //** 상대 채팅창 동영상 버전
        private VideoView friendMsgVideoView;
        //** 상대 채팅창 이미지 버젼
        private PhotoView friendMsgPhotoView;


        public VM_CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            //위젯
            this.friendImgView = itemView.findViewById(R.id.friendImgView);
            this.friendChatLayout = itemView.findViewById(R.id.friendChatLayout);
            this.friendName=itemView.findViewById(R.id.friend_name_tv);

            //** 내 채팅창 텍스트 버젼
            this.myMsgTxtView = itemView.findViewById(R.id.myMsgTxtView);
            this.myChatLayout = itemView.findViewById(R.id.myChatLayout);

            //** 내 채팅창 동영상 버젼
            this.myChatLayoutVideoVersion=itemView.findViewById(R.id.myChatLayoutVideoVersion);
            this.myMsgVideoView=itemView.findViewById(R.id.myMsgVideoView);

            //** 내 채팅창 동영상 버젼
            this.myChatLayoutImageVersion=itemView.findViewById(R.id.myChatLayoutImageVersion);
            this.myMsgPhotoView=itemView.findViewById(R.id.myMsgImageView);

            //** 상대 채팅창 동영상 버전
            this.friendMsgVideoView=itemView.findViewById(R.id.friendMsgVideoView);

            //** 상대 채팅창 이미지 버전
            this.friendMsgPhotoView=itemView.findViewById(R.id.friendMsgImageView);

            //** 상대 채팅창 텍스트 버젼
            this.friendMsgTxtView = itemView.findViewById(R.id.friendMsgTxtView);

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
        mGlideRequestManager=Glide.with(context);
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
    public void onBindViewHolder(@NonNull final VM_ChatAdapter.VM_CustomViewHolder holder, int position) {
        int chatSize = chatList.size();

        Log.d(TAG, "[VM_ChatAdapter] chatsize" + chatSize + "");

        if(chatList==null){
            Log.d(TAG, "[VM_ChatAdapter]: chatList가 null임");
        }

        if(userType.equals(VM_ENUM.TEACHER)){ //유저가 선생인 경우
            if (chatList.get(position).getSender().equals(VM_ENUM.TEACHER)) { //보내는 이가 선생(나)

                holder.friendChatLayout.setVisibility(View.GONE);

                String flag=chatList.get(position).getType();
                Log.d(TAG, "[VM_ChatAdapter] Flag>> "+flag);

                StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());

                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.myChatLayout.setVisibility(View.VISIBLE);
                        holder.myMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        holder.myChatLayoutImageVersion.setVisibility(View.VISIBLE);
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //** 사진파일 이미지뷰에 삽입
                                mGlideRequestManager
                                        .load(uri)
                                        .into(holder.myMsgPhotoView);
                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(VM_ENUM.TAG,"사진 로드 실패");
                            }
                        });

                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        holder.myChatLayoutVideoVersion.setVisibility(View.VISIBLE);
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //** 사진파일 이미지뷰에 삽입
                               holder.myMsgVideoView.setVideoURI(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        break;
                }


            } else if (chatList.get(position).getSender().equals(VM_ENUM.STUDENT)) {//보내는 이가 학생인 경우

                String flag=chatList.get(position).getType();
                holder.friendChatLayout.setVisibility(View.VISIBLE);
                Log.d(TAG, "[VM_ChatAdapter] Flag>> "+flag);

                holder.friendName.setText(this.matchSet_student);
                holder.friendImgView.setImageResource(R.drawable.student);

                StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());

                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.friendMsgTxtView.setVisibility(View.VISIBLE);
                        holder.friendMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        holder.friendImgView.setVisibility(View.VISIBLE);
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //** 사진파일 이미지뷰에 삽입
                                mGlideRequestManager
                                        .load(uri)
                                        .into(holder.myMsgPhotoView);
                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(VM_ENUM.TAG,"사진 로드 실패");
                            }
                        });

                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        holder.friendImgView.setVisibility(View.VISIBLE);
                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //** 사진파일 이미지뷰에 삽입
                                holder.myMsgVideoView.setVideoURI(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        break;
                }


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


