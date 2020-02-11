package com.example.visualmath.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileDescriptor;
import java.io.IOException;

public class VM_ChatItemViewActivity extends AppCompatActivity {

    private PhotoView imageViewPhoto;
    private VideoView videoView;
    private Button playButton;
    private Button stopButton;

    private String filePath;
    private String fileType;

    private StorageReference pathReference;

    //** Glide Library Exception 처리
    public RequestManager mGlideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__chat_item_view);

        init();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(VM_ENUM.TAG,"동영상 재생준비 완료");
                playButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);

            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(VM_ENUM.TAG,"동영상 재생 완료");
                playButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            }
        });
    }

    private void init(){
        playButton=findViewById(R.id.btn_play);
        stopButton=findViewById(R.id.btn_stop);
        imageViewPhoto = (PhotoView) findViewById(R.id.iv_photo);
        videoView=findViewById(R.id.iv_video);
        mGlideRequestManager = Glide.with(this);

        filePath=getIntent().getStringExtra(VM_ENUM.IT_CHAT_ITEM_URI);
        Log.d(VM_ENUM.TAG,"[VM_ChatItemViewActivity]onCreate 호출, filePath  "+filePath);

        fileType=getIntent().getStringExtra(VM_ENUM.IT_CHAT_ITEM_TYPE);
        Log.d(VM_ENUM.TAG,"[VM_ChatItemViewActivity]onCreate 호출, fileType  "+fileType);

         pathReference = FirebaseStorage.getInstance().getReference().child(filePath);

         if(fileType.equals(VM_ENUM.CHAT_IMAGE)){
             pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     //** 사진파일 이미지뷰에 삽입
                     imageViewPhoto.setVisibility(View.VISIBLE);
                     mGlideRequestManager
                             .load(uri)
                             .into(imageViewPhoto);
                     Log.d(VM_ENUM.TAG,"사진 로드 성공 > "+uri);
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.d(VM_ENUM.TAG,"사진 로드 실패");
                 }
             });
         }else if(fileType.equals(VM_ENUM.CHAT_VIDEO)){
             pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     videoView.setVisibility(View.VISIBLE);
                     //** 사진파일 이미지뷰에 삽입
                   videoView.setVideoURI(uri);
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {

                 }
             });
         }



    }


    public void cancel(View view) {
        finish();
    }

    public void videoStart(View view) {
        playButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        videoView.start();
    }

    public void vidoStop(View view) {
        playButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        videoView.pause();
    }
}
