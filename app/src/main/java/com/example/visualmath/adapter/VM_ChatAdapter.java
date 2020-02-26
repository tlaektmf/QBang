package com.example.visualmath.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import com.example.visualmath.activity.VM_ChatItemViewActivity;
import com.example.visualmath.data.VM_Data_CHAT;
import com.example.visualmath.fragment.ProblemFragment;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VM_ChatAdapter extends RecyclerView.Adapter<VM_ChatAdapter.VM_CustomViewHolder> {

    private static final String TAG = VM_ENUM.TAG;
    private List<VM_Data_CHAT> chatList;
    private List<Bitmap> bitmaps;
    private Context context;
    private Activity parent;
    private String userType;
    private String matchSet_student;
    private String matchSet_teacher;

    ///Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    public class VM_CustomViewHolder extends RecyclerView.ViewHolder {

        //위젯
        private View friendChatLayout; //상대방 채팅창 전체 레이아웃
        private ImageView friendImgView;
        private TextView friendName;

        //** 상대 채팅창 동영상 버전
        ///private VideoView friendMsgVideoView;
        private ImageView friendMsgVideoView;
        private View friendChatLayoutVideoVersion;

        //** 상대 채팅창 이미지 버젼
        private View friendChatLayoutImageVersion;
        //private PhotoView friendMsgPhotoView;
        private ImageView friendMsgPhotoView;


        //** 상대 채팅창 텍스트 버젼
        private View friendChatLayoutTextVersion;
        private TextView friendMsgTxtView;

        //** 위젯
        private View myChatLayout; //내 채팅창 전체 레이아웃

        //** 내 채팅창 텍스트 버젼
        private TextView myMsgTxtView;
        private View myChatLayoutTextVersion;

        //** 내 채팅창 동영상 버젼
        private View myChatLayoutVideoVersion;
        ///private VideoView myMsgVideoView;
        private ImageView myMsgVideoView;

        //** 내 채팅창 이미지 버젼
        private View myChatLayoutImageVersion;
        //private PhotoView myMsgPhotoView;
        private ImageView myMsgPhotoView;

        ///private RequestManager mGlideRequestManager;

        public VM_CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            //위젯
            this.friendImgView = itemView.findViewById(R.id.friendImgView);
            this.friendChatLayout = itemView.findViewById(R.id.friendChatLayout);
            this.friendName=itemView.findViewById(R.id.friend_name_tv);


            this.myChatLayout = itemView.findViewById(R.id.myChatLayout);

            //** 내 채팅창 텍스트 버젼
            this.myMsgTxtView = itemView.findViewById(R.id.myMsgTxtView);
            this.myChatLayoutTextVersion=itemView.findViewById(R.id.myChatLayoutTextVersion);


            //** 내 채팅창 동영상 버젼
            this.myChatLayoutVideoVersion=itemView.findViewById(R.id.myChatLayoutVideoVersion);
            this.myMsgVideoView=itemView.findViewById(R.id.myMsgVideoView);

            //** 내 채팅창 이미지 버젼
            this.myChatLayoutImageVersion=itemView.findViewById(R.id.myChatLayoutImageVersion);
            this.myMsgPhotoView=itemView.findViewById(R.id.myMsgImageView);

            //** 상대 채팅창 동영상 버전
            this.friendChatLayoutVideoVersion=itemView.findViewById(R.id.myFriendLayoutVideoVersion);
            this.friendMsgVideoView=itemView.findViewById(R.id.friendMsgVideoView);

            //** 상대 채팅창 이미지 버전
            this.friendChatLayoutImageVersion=itemView.findViewById(R.id.myFriendChatLayoutImageVersion);
            this.friendMsgPhotoView=itemView.findViewById(R.id.friendMsgImageView);

            //** 상대 채팅창 텍스트 버젼
            this.friendChatLayoutTextVersion=itemView.findViewById(R.id.myFriendLayoutTextVersion);
            this.friendMsgTxtView = itemView.findViewById(R.id.friendMsgTxtView);


            ///this.mGlideRequestManager=Glide.with(itemView.getContext());

            //** 아이템 클릭 이벤트 (my layout widget)
            myMsgVideoView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(VM_ENUM.TAG,"[VM_chatAdapter 클릭리스너] myMsgVideoView" );
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환

                        String uri = chatList.get(pos).getChatContent();
                        String type = chatList.get(pos).getType();

                        if (type.equals(VM_ENUM.CHAT_TEXT)) {
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 안함 타입이 text");

                        }else{
                            Intent intent=new Intent(context, VM_ChatItemViewActivity.class);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_URI,uri);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_TYPE,type);
                            parent.startActivity(intent);
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 , uri : "+uri+"| type"+type);
                        }


                    }
                }
            });

            myMsgPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(VM_ENUM.TAG,"[VM_chatAdapter 클릭리스너] myMsgPhotoView" );
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환

                        String uri = chatList.get(pos).getChatContent();
                        String type = chatList.get(pos).getType();

                        if (type.equals(VM_ENUM.CHAT_TEXT)) {
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 안함 타입이 text");

                        }else{
                            Intent intent=new Intent(context, VM_ChatItemViewActivity.class);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_URI,uri);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_TYPE,type);
                            parent.startActivity(intent);
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 , uri : "+uri+"| type"+type);
                        }


                    }
                }
            });

            //** 아이템 클릭 이벤트 (friend layout widget)
            friendMsgVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(VM_ENUM.TAG,"[VM_chatAdapter 클릭리스너] friendMsgVideoView" );

                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환

                        String uri = chatList.get(pos).getChatContent();
                        String type = chatList.get(pos).getType();

                        if (type.equals(VM_ENUM.CHAT_TEXT)) {
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 안함 타입이 text");

                        }else{
                            Intent intent=new Intent(context, VM_ChatItemViewActivity.class);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_URI,uri);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_TYPE,type);
                            parent.startActivity(intent);
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 , uri : "+uri+"| type"+type);
                        }


                    }
                }
            });

            friendMsgPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(VM_ENUM.TAG,"[VM_chatAdapter 클릭리스너] friendMsgPhotoView" );

                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환

                        String uri = chatList.get(pos).getChatContent();
                        String type = chatList.get(pos).getType();

                        if (type.equals(VM_ENUM.CHAT_TEXT)) {
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 안함 타입이 text");

                        }else{
                            Intent intent=new Intent(context, VM_ChatItemViewActivity.class);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_URI,uri);
                            intent.putExtra(VM_ENUM.IT_CHAT_ITEM_TYPE,type);
                            parent.startActivity(intent);
                            Log.d(VM_ENUM.TAG,"[VM_chatAdapter] -> ChatItemView 전환 , uri : "+uri+"| type"+type);
                        }


                    }
                }
            });
            //>>> 아이템 클릭 이벤트 종료

        }
    }

    public VM_ChatAdapter(List<VM_Data_CHAT> _chatList, Context context,String userType,String matchSet_student, String matchSet_teacher, Activity parent,List<Bitmap> _bitmaps) {
        this.chatList = _chatList;
        this.bitmaps=_bitmaps;
        if(chatList!=null){
            if(chatList.size()==0){
                Log.d(TAG, "[VM_ChatAdapter/VM_ChatAdapter()]: chatList가 비어있음");
            }
        }

        this.parent=parent;
        this.context = context;
        this.mGlideRequestManager=Glide.with(context);
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

        Log.d(TAG, "[VM_ChatAdapter] chatsize" + chatSize + ", 현재 position"+position+", chat item"+chatList.get(position).getChatContent());

        if(chatList==null){
            Log.d(TAG, "[VM_ChatAdapter]: chatList가 null임");
        }

        //** 아이템 뷰의 visibility 모두 초기화
        holder.friendChatLayout.setVisibility(View.GONE);
        holder.myChatLayout.setVisibility(View.GONE);

        holder.myChatLayoutTextVersion.setVisibility(View.GONE);
        holder.myChatLayoutImageVersion.setVisibility(View.GONE);
        holder.myChatLayoutVideoVersion.setVisibility(View.GONE);

        holder.friendChatLayoutTextVersion.setVisibility(View.GONE);
        holder.friendChatLayoutImageVersion.setVisibility(View.GONE);
        holder.friendChatLayoutVideoVersion.setVisibility(View.GONE);
        //>>>>>>

        //** 아이템 뷰의 리소스 모두 초기화
        holder.myMsgPhotoView.setImageResource(R.drawable.my_chat_back);
        holder.myMsgVideoView.setImageResource(R.drawable.my_chat_back);
        holder.friendMsgPhotoView.setImageResource(R.drawable.your_chat_back);
        holder.friendMsgVideoView.setImageResource(R.drawable.your_chat_back);
        //>>>

        if(userType.equals(VM_ENUM.TEACHER)){ //유저가 선생인 경우
            if (chatList.get(position).getSender().equals(VM_ENUM.TEACHER)) { //보내는 이가 선생(나)

                holder.friendChatLayout.setVisibility(View.GONE);
                holder.myChatLayout.setVisibility(View.VISIBLE);

                String flag=chatList.get(position).getType();
                Log.d(TAG, "[VM_ChatAdapter] Flag>> "+flag);

                ///StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());

                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.myChatLayoutTextVersion.setVisibility(View.VISIBLE);
                        holder.myMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        holder.myChatLayoutImageVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//
//                                //Glide.with(holder.myMsgPhotoView.getContext())
//                                mGlideRequestManager
//                                        .load(uri)
//                                        .placeholder(R.drawable.camera)
//                                        .into(holder.myMsgPhotoView);
//
//                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"사진 로드 실패");
//                            }
//                        });

                        if(bitmaps.get(position)!=null){
                            holder.myMsgPhotoView.setImageBitmap(bitmaps.get(position));
                        }else{
                            StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //** 사진파일 이미지뷰에 삽입

                                //Glide.with(holder.myMsgPhotoView.getContext())
                                mGlideRequestManager
                                        .load(uri)
                                        .placeholder(R.drawable.camera)
                                        .into(holder.myMsgPhotoView);

                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);

                                //** 프로그래스 바 셋팅
                                progressBarSetting();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(VM_ENUM.TAG,"사진 로드 실패");

                                int errorCode=((StorageException)e).getErrorCode();
                                if(errorCode==StorageException.ERROR_QUOTA_EXCEEDED){
                                    Log.d(VM_ENUM.TAG,"[ChatAdapter]StorageException.ERROR_QUOTA_EXCEEDED");
                                    holder.myMsgPhotoView.setImageResource(R.drawable.ic_warning_error_svgrepo_com);
                                }

                            }
                        });

                        }


                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        holder.myChatLayoutVideoVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//                               holder.myMsgVideoView.setVideoURI(uri);
//                                Log.d(VM_ENUM.TAG,"동영상 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"동영상 로드 실패");
//                            }
//                        });

                        holder.myMsgVideoView.setImageResource(R.drawable.ic_video_call_svgrepo_com);

                        //** 프로그래스 바 셋팅
                        progressBarSetting();
                        break;
                }


            } else if (chatList.get(position).getSender().equals(VM_ENUM.STUDENT)) {//보내는 이가 학생인 경우

                String flag=chatList.get(position).getType();
                holder.friendChatLayout.setVisibility(View.VISIBLE);
                holder.myChatLayout.setVisibility(View.GONE);

                Log.d(TAG, "[VM_ChatAdapter] Flag>> "+flag+", contents"+chatList.get(position).getChatContent());

                holder.friendName.setText(this.matchSet_student);
                holder.friendImgView.setImageResource(R.drawable.student);

                ///StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());

                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.friendChatLayoutTextVersion.setVisibility(View.VISIBLE);
                        holder.friendMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        holder.friendChatLayoutImageVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//
//                                //Glide.with(holder.friendMsgPhotoView.getContext())
//                                mGlideRequestManager
//                                        .load(uri)
//                                        .placeholder(R.drawable.camera)
//                                        .into(holder.friendMsgPhotoView);
//                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"사진 로드 실패");
//                            }
//                        });

                        if(bitmaps.get(position)!=null){
                            holder.friendMsgPhotoView.setImageBitmap(bitmaps.get(position));
                        }else{
                            StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //** 사진파일 이미지뷰에 삽입

                                    //Glide.with(holder.myMsgPhotoView.getContext())
                                    mGlideRequestManager
                                            .load(uri)
                                            .placeholder(R.drawable.camera)
                                            .into(holder.friendMsgPhotoView);

                                    Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(VM_ENUM.TAG,"사진 로드 실패");

                                    int errorCode=((StorageException)e).getErrorCode();
                                    if(errorCode==StorageException.ERROR_QUOTA_EXCEEDED){
                                        Log.d(VM_ENUM.TAG,"[ChatAdapter]StorageException.ERROR_QUOTA_EXCEEDED");
                                        holder.friendMsgPhotoView.setImageResource(R.drawable.ic_warning_error_svgrepo_com);
                                    }

                                }
                            });

                        }



                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        holder.friendChatLayoutVideoVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//                                holder.friendMsgVideoView.setVideoURI(uri);
//                                Log.d(VM_ENUM.TAG,"동영상 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"동영상 로드 실패");
//                            }
//                        });
                        holder.friendMsgVideoView.setImageResource(R.drawable.ic_video_call_svgrepo_com);
                        break;
                }


            }
        }else if(userType.equals(VM_ENUM.STUDENT)){//유저가 학생인 경우
            if (chatList.get(position).getSender().equals(VM_ENUM.TEACHER)) { //보내는 이가 선생인 경우

                String flag=chatList.get(position).getType();
                holder.friendChatLayout.setVisibility(View.VISIBLE);
                holder.myChatLayout.setVisibility(View.GONE);
                holder.friendName.setText(this.matchSet_teacher);
                holder.friendImgView.setImageResource(R.drawable.teacher);

                Log.d(TAG, "[VM_ChatAdapter] Flag>> "+flag);



                //StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());

                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.friendChatLayoutTextVersion.setVisibility(View.VISIBLE);
                        holder.friendMsgTxtView.setText(chatList.get(position).getChatContent());

                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        holder.friendChatLayoutImageVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//
//                                //Glide.with(holder.friendMsgPhotoView.getContext())
//                                mGlideRequestManager
//                                        .load(uri)
//                                        .placeholder(R.drawable.camera)
//                                        .into(holder.friendMsgPhotoView);
//                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"사진 로드 실패");
//                            }
//                        });

                        if(bitmaps.get(position)!=null){
                            holder.friendMsgPhotoView.setImageBitmap(bitmaps.get(position));
                        }else{
                            StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //** 사진파일 이미지뷰에 삽입

                                    //Glide.with(holder.myMsgPhotoView.getContext())
                                    mGlideRequestManager
                                            .load(uri)
                                            .placeholder(R.drawable.camera)
                                            .into(holder.friendMsgPhotoView);

                                    Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(VM_ENUM.TAG,"사진 로드 실패");

                                    int errorCode=((StorageException)e).getErrorCode();
                                    if(errorCode==StorageException.ERROR_QUOTA_EXCEEDED){
                                        Log.d(VM_ENUM.TAG,"[ChatAdapter]StorageException.ERROR_QUOTA_EXCEEDED");
                                        holder.friendMsgPhotoView.setImageResource(R.drawable.ic_warning_error_svgrepo_com);
                                    }

                                }
                            });

                        }



                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        holder.friendChatLayoutVideoVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//                                holder.friendMsgVideoView.setVideoURI(uri);
//                                Log.d(VM_ENUM.TAG,"동영상 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"동영상 로드 실패");
//                            }
//                        });

                        holder.friendMsgVideoView.setImageResource(R.drawable.ic_video_call_svgrepo_com);
                        break;
                }




            } else if (chatList.get(position).getSender().equals(VM_ENUM.STUDENT)) {//보내는 이가 나(학생)인 경우
                holder.friendChatLayout.setVisibility(View.GONE);
                holder.myChatLayout.setVisibility(View.VISIBLE);
                String flag=chatList.get(position).getType();

                Log.d(TAG, "[VM_ChatAdapter] Flag>> "+flag);

                ///StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());

                switch (flag){
                    case VM_ENUM.CHAT_TEXT:
                        holder.myChatLayoutTextVersion.setVisibility(View.VISIBLE);
                        holder.myMsgTxtView.setText(chatList.get(position).getChatContent());
                        break;
                    case VM_ENUM.CHAT_IMAGE:
                        holder.myChatLayoutImageVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//
//                                //Glide.with(holder.myMsgPhotoView.getContext())
//                                mGlideRequestManager
//                                        .load(uri)
//                                        .placeholder(R.drawable.camera)
//                                        .into(holder.myMsgPhotoView);
//                                Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(VM_ENUM.TAG,"사진 로드 실패");
//                            }
//                        });

                        if(bitmaps.get(position)!=null){
                            holder.myMsgPhotoView.setImageBitmap(bitmaps.get(position));
                        }else{
                            StorageReference pathReference = FirebaseStorage.getInstance().getReference().child(chatList.get(position).getChatContent());
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //** 사진파일 이미지뷰에 삽입

                                    //Glide.with(holder.myMsgPhotoView.getContext())
                                    mGlideRequestManager
                                            .load(uri)
                                            .placeholder(R.drawable.camera)
                                            .into(holder.myMsgPhotoView);

                                    Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);

                                    //** 프로그래스 바 셋팅
                                    progressBarSetting();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(VM_ENUM.TAG,"사진 로드 실패");

                                    int errorCode=((StorageException)e).getErrorCode();
                                    if(errorCode==StorageException.ERROR_QUOTA_EXCEEDED){
                                        Log.d(VM_ENUM.TAG,"[ChatAdapter]StorageException.ERROR_QUOTA_EXCEEDED");
                                        holder.myMsgPhotoView.setImageResource(R.drawable.ic_warning_error_svgrepo_com);
                                    }

                                }
                            });

                        }


                        break;
                    case VM_ENUM.CHAT_VIDEO:
                        holder.myChatLayoutVideoVersion.setVisibility(View.VISIBLE);

//                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                //** 사진파일 이미지뷰에 삽입
//                                holder.myMsgVideoView.setVideoURI(uri);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });

                        holder.myMsgVideoView.setImageResource(R.drawable.ic_video_call_svgrepo_com);

                        //** 프로그래스 바 셋팅
                        progressBarSetting();

                        break;
                }


            }
        }



    }



    @Override
    public int getItemCount() {
        return (null != this.chatList ? chatList.size() : 0);
    }

    public void progressBarSetting(){
        if(ProblemFragment.isLoadingBarAvailable){
            ProblemFragment.chat_loading_bar.setVisibility(View.GONE);
            ProblemFragment.isLoadingBarAvailable=false;
            Log.d(VM_ENUM.TAG,"[VM_ChatAdapter] 프로그래스 바 삭제, isLoadingBarAvailable false로 초기화");

            Log.d(VM_ENUM.TAG, "[ProblemFragment] 데이터 로딩 완료됐으므로 chat_bottom_layout 클릭 열어둠");
            for (int i = 0; i < ProblemFragment.chat_bottom_layout.getChildCount(); i++) {
                View child = ProblemFragment.chat_bottom_layout.getChildAt(i);
                child.setEnabled(true);
            }

        }
    }


}


