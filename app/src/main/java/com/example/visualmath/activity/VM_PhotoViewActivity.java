package com.example.visualmath.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.github.chrisbanes.photoview.PhotoView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VM_PhotoViewActivity extends AppCompatActivity {

    private File takeFile;
    private PhotoView imageViewPhoto;

    public static Bitmap photoViewBitmap; //VM_RegiOther <-> VM_PhotoView
    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__photo_view);

        imageViewPhoto = (PhotoView) findViewById(R.id.iv_photo);

        index=getIntent().getIntExtra(VM_ENUM.IT_PHOTO_INDEX, -1);
        Log.d(VM_ENUM.TAG,"[VM_PhotoViewActivity]onCreate 호출, index  "+index);


        if (VM_RegiserOtherThingsActivity.saveData.getFilePathElement(index) != null) {

            Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity] " + VM_RegiserOtherThingsActivity.saveData.getFilePathElement(index));
            try {
                setBitmapFromUri(VM_RegiserOtherThingsActivity.saveData.getFilePathElement(index));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private Bitmap setBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        if (image != null) {
            imageViewPhoto.setImageBitmap(image); //이미지 set
        }

        return image;
    }



    public void deletePhotoButtonEvent(View view) {
        Intent intent = new Intent(this, VM_RegiserOtherThingsActivity.class);
        intent.putExtra(VM_ENUM.IT_DELETE_PHOTO_INDEX, true);
        Log.d(VM_ENUM.TAG, "[사진을 삭제합니다. photoview -> regiother] ");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void takePhotoButtonEvent(View view) {

        Log.d(VM_ENUM.TAG, "[사진을 새로 촬영합니다]. photoview -> regiother] ");
        //** 권한 체크
        tedPermission(VM_ENUM.IT_TAKE_PHOTO);
    }

    public void galleryButtonEvent(View view) {

        Log.d(VM_ENUM.TAG, "[갤러리에서 사진을 새로 선택합니다]. photoview -> regiother] ");
        //** 권한 체크
        tedPermission(VM_ENUM.IT_GALLERY_PHOTO);

    }

    public void getAlbumFile() {

        Intent intent = new Intent(this, VM_RegiserOtherThingsActivity.class);
        intent.putExtra(VM_ENUM.IT_CHANGE_PHOTO_GET_FROM_GALLERY, true);
        setResult(RESULT_OK, intent);
        finish();

    }

    public void takePhoto() {
        Intent intent = new Intent(this, VM_RegiserOtherThingsActivity.class);
        intent.putExtra(VM_ENUM.IT_CHANGE_PHOTO_GET_FROM_TAKE, true);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void tedPermission(final String VIEW) {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 권한 요청 성공
                Toast.makeText(VM_PhotoViewActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                if(VIEW.equals(VM_ENUM.IT_TAKE_PHOTO)){
                    takePhoto();
                }
                if (VIEW.equals(VM_ENUM.IT_GALLERY_PHOTO)) {
                    getAlbumFile();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ** 권한 요청 실패
                Toast.makeText(VM_PhotoViewActivity.this, deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(getResources().getString(R.string.permission_notice))
                .setDeniedCloseButtonText(getResources().getString(R.string.permission_close))
                .setGotoSettingButtonText(getResources().getString(R.string.permission_setting))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    public void cancel(View view){
  finish();
    }
}
