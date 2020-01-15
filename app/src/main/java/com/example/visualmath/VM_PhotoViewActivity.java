package com.example.visualmath;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.io.File;
import java.io.InputStream;

public class VM_PhotoViewActivity extends AppCompatActivity {

    private File photoFile;
    private ImageView imageViewPhoto;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__photo_view);

        imageViewPhoto = (ImageView) findViewById(R.id.iv_photo);
        Uri uri = getIntent().getParcelableExtra(VM_ENUM.PHOTO_URI);
        index=-1;
        getIntent().getIntExtra(VM_ENUM.IT_PHOTO_INDEX,index);
        if (uri != null) {

            Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity] " + uri);
            setImage( uri);
        }
        if(index!=-1){

        }
    }

    private void setImage(Uri _uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(_uri.toString(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경
        if (originalBm != null) {
            imageViewPhoto.setImageBitmap(originalBm); //이미지 set
        }


    }

    public void deletePhoto(View view) {
        Intent intent=new Intent(this,VM_RegiserOtherThingsActivity.class);
        intent.putExtra(VM_ENUM.DELETE_PHOTO, index);
        Log.d(VM_ENUM.TAG,"[사진을 삭제합니다. photoview -> regiother] ");
        startActivity(intent);
       finish();
    }

    public void takePhoto(View view) {
    }

    public void gallery(View view) {
    }
}
