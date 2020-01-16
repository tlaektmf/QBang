package com.example.visualmath;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VM_PhotoViewActivity extends AppCompatActivity {

    private File galleryFile; //갤러리로부터 받아온 이미지를 저장
    private Uri newPhotoUri;
//    private ImageView imageViewPhoto;
    private PhotoView imageViewPhoto;

    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__photo_view);

        imageViewPhoto = (PhotoView) findViewById(R.id.iv_photo);
        Uri uri = getIntent().getParcelableExtra(VM_ENUM.PHOTO_URI);
        index=getIntent().getIntExtra(VM_ENUM.IT_PHOTO_INDEX, -1);
        Log.d(VM_ENUM.TAG,"[VM_PhotoViewActivity]onCreate 호출, index  "+index);

        if (uri != null) {

            Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity] " + uri);
            setImage(uri);
        }
        if (index != -1) {

        }
    }

    private void setImage(Uri _uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(_uri.toString(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경
        if (originalBm != null) {
            imageViewPhoto.setImageBitmap(originalBm); //이미지 set
        }


    }

    public void deletePhotoButtonEvent(View view) {
        Intent intent = new Intent(this, VM_RegiserOtherThingsActivity.class);
        intent.putExtra(VM_ENUM.IT_DELETE_PHOTO_INDEX, index);
        Log.d(VM_ENUM.TAG, "[사진을 삭제합니다. photoview -> regiother] "+ index);
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

            Log.d(VM_ENUM.TAG,"[VM_PhotoViewActivity/getAlbumFile] ");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, VM_ENUM.RC_PICK_FROM_ALBUM); //앨범 화면으로 이동
        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */


    }

    public void takePhoto() {

            Log.d(VM_ENUM.TAG,"[VM_PhotoViewActivity/takePhoto] ");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent를 통해 카메라 화면으로 이동함

            try {
                galleryFile = createImageFile(); //파일 경로가 담긴 빈 이미지 생성
            } catch (IOException e) {
                Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }

            if (galleryFile != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity/takePhoto() file provider] 상위버전");
                    Uri photoUri = FileProvider.getUriForFile(this,
                            "com.example.visualmath.provider", galleryFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, VM_ENUM.RC_PICK_FROM_CAMERA);

                } else {
                    Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity/takePhoto() file provider] 하위버전");
                    Uri photoUri = Uri.fromFile(galleryFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //galleryFile 의 Uri경로를 intent에 추가 -> 카메라에서 찍은 사진이 저장될 주소를 의미
                    startActivityForResult(intent, VM_ENUM.RC_PICK_FROM_CAMERA);

                }

            }


    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( {시간})
        String timeStamp = new SimpleDateFormat("HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = timeStamp;

        // 이미지가 저장될 폴더 이름 ( userID )
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity/createImageFile] 현재 로그인한 유저" + userID);
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + userID + "/");

        //** 디렉토리 설정
        if (!storageDir.exists()) storageDir.mkdirs();
        else {
            Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity/createImageFile] 파일의 경로가 이미 존재");
        }
        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;

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

    /**
     * startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
     * 이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { ... PICK_FROM_ALBUM ...} 등이 requestCode 로 반환됨
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //** 예외 사항 처리
        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "선택이 취소 되었습니다.", Toast.LENGTH_SHORT).show();

            //** create로 만들어둔 경로는 삭제
            if (galleryFile != null) {
                if (galleryFile.exists()) {
                    if (galleryFile.delete()) {
                        Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity ]" + galleryFile.getAbsolutePath() + " 삭제 성공");
                        galleryFile = null;
                    }
                }
            }

            return;
        } else if (requestCode == VM_ENUM.RC_PICK_FROM_ALBUM) {
            Uri photo_problem = data.getData();// data.getData() 를 통해 갤러리에서 선택한 이미지의 Uri 를 받아 옴
            Cursor cursor = null;

            //**  cursor 를 통해 스키마를 content:// 에서 file:// 로 변경 -> 사진이 저장된 절대경로를 받아오는 과정
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photo_problem != null;
                cursor = getContentResolver().query(photo_problem, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                galleryFile = new File(cursor.getString(column_index));
                newPhotoUri = Uri.parse(galleryFile.getAbsolutePath());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            Intent intent = new Intent(this, VM_RegiserOtherThingsActivity.class);
            intent.putExtra(VM_ENUM.IT_GALLERY_PHOTO, newPhotoUri);
            intent.putExtra(VM_ENUM.IT_PHOTO_INDEX, index);
            setResult(RESULT_OK, intent);
            finish();

        } else if (requestCode == VM_ENUM.RC_PICK_FROM_CAMERA) {
            Uri photoUri;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity] 상위버전");
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", galleryFile);

            } else {
                Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity] 하위버전");
                photoUri = Uri.fromFile(galleryFile);
            }

            newPhotoUri=Uri.parse(galleryFile.getAbsolutePath());
            Intent intent = new Intent(this, VM_RegiserOtherThingsActivity.class);
            intent.putExtra(VM_ENUM.IT_TAKE_PHOTO, newPhotoUri);
            intent.putExtra(VM_ENUM.IT_PHOTO_INDEX, index);
            Log.d(VM_ENUM.TAG, "[VM_PhotoViewActivity] newPhotoUri  "+newPhotoUri);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
    public void cancel(View view){finish();}
}
