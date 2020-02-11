package com.example.visualmath.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.VM_RegisterProblemActivity;
import com.example.visualmath.dialog.VM_Dialog_registerProblem;
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

public class VM_ViewActivity extends AppCompatActivity {

    private File takeFile;
    private PhotoView imageViewPhoto;
    private VideoView videoView;
    private String PICK_FLAG;
    private Button repickButton;
    private Button playButton;
    private Button stopButton;

    private String pick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm_view);

        imageViewPhoto = (PhotoView) findViewById(R.id.iv_photo);
        videoView=findViewById(R.id.iv_video);
        repickButton=findViewById(R.id.ib_repick);
        playButton=findViewById(R.id.btn_play);
        stopButton=findViewById(R.id.btn_stop);

        PICK_FLAG=getIntent().getStringExtra(VM_ENUM.IT_PICK_FLAG);
        Log.d(VM_ENUM.TAG,"[VM_ViewActivity]onCreate 호출, PICK_FLAG  "+PICK_FLAG);


        if(PICK_FLAG.equals(VM_ENUM.IT_TAKE_PHOTO)){
            repickButton.setBackgroundResource(R.drawable.pv_camera_btn);
        }else if(PICK_FLAG.equals(VM_ENUM.IT_GALLERY_PHOTO)){
            repickButton.setBackgroundResource(R.drawable.pv_gallery_btn);
        }

        switch (PICK_FLAG){
            case VM_ENUM.IT_TAKE_PHOTO:
                Log.d(VM_ENUM.TAG, "[사진을 새로 촬영합니다]. photoview -> regiother] ");
                //** 권한 체크
                tedPermission(VM_ENUM.IT_TAKE_PHOTO);
                break;
            case VM_ENUM.IT_GALLERY_PHOTO:
                Log.d(VM_ENUM.TAG, "[갤러리에서 사진을 새로 선택합니다]. photoview -> regiother] ");
                //** 권한 체크
                tedPermission(VM_ENUM.IT_GALLERY_PHOTO);
                break;
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


    public void getAlbumFile() {
        pick=VM_ENUM.IT_GALLERY_PHOTO;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, VM_ENUM.PICK_FROM_ALBUM); //앨범 화면으로 이동

    }

    public void takePhoto() {
        pick=VM_ENUM.IT_TAKE_PHOTO;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent를 통해 카메라 화면으로 이동함

        try {
            takeFile = createImageFile(); //파일 경로가 담긴 빈 이미지 생성(아직 content provider이 입혀져 있지 않음)

        } catch (IOException e) {
            Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }


        if (takeFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//provider을 씌워서 보냄
                // : photoUri를 intent로 넘겨주고, 이를 다시 onActivity result에서 받기 위함이 아니라,가서 쓰라는 의미

                startActivityForResult(intent, VM_ENUM.PICK_FROM_CAMERA);

            } else {
                Uri photoUri = Uri.fromFile(takeFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //takeFile 의 Uri경로를 intent에 추가 -> 카메라에서 찍은 사진이 저장될 주소를 의미
                startActivityForResult(intent, VM_ENUM.PICK_FROM_CAMERA);

            }

        }
    }

    private File createImageFile() throws IOException {//** 파일 경로를 이미 생성(시간으로 중복문제 해결)

        // 이미지 파일 이름 ( {시간})
        String timeStamp = new SimpleDateFormat("HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = timeStamp;

        // 이미지가 저장될 폴더 이름 ( userID )
        File storageDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Log.d(VM_ENUM.TAG, "[Q 상위 버전] " + storageDir);
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory() + "/" + "visual_math" + "/");
            Log.d(VM_ENUM.TAG, "[Q 하위 버전] " + storageDir);
        }


        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(
                imageFileName,
                ".jpg"
                , storageDir);


        return image;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveFile() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, takeFile.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 파일을 write중이라면 다른곳에서 데이터요구를 무시하겠다는 의미.
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        ContentResolver contentResolver = getContentResolver();

        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri item = contentResolver.insert(collection, values);

        try {
            assert item != null;
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
            if (pdf == null) {

            } else {
                InputStream inputStream = getImageInputStram();
                byte[] strToByte = getBytes(inputStream);
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                fos.write(strToByte);
                fos.close();
                inputStream.close();
                pdf.close();
                values.clear();
                // 파일을 모두 write하고 다른곳에서 사용할 수 있도록 0으로 업데이트를 해줍니다.
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                contentResolver.update(item, values, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private InputStream getImageInputStram() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ByteArrayInputStream bs = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(takeFile.getAbsolutePath(), options);// galleryFile or takeFile의 경로를 불러와 bitmap 파일로 변경
        if (originalBm != null) {

            originalBm.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();
            bs = new ByteArrayInputStream(bitmapData);
        }

        return bs;
    }

    private void tedPermission(final String VIEW) {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 권한 요청 성공
                Toast.makeText(VM_ViewActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(VM_ViewActivity.this, deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

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

    public void send(View view) {
        //** 데이터베이스 저장
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(VM_ENUM.TAG,"[ViewActivity]"+requestCode);

        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "선택이 취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
                if (takeFile != null) {//->이때는 무조건 "사진 촬영" 하려다가 취소한 경우에 해당함
                    if (takeFile.exists()) {
                        if (takeFile.delete()) {
                            Log.d(VM_ENUM.TAG, takeFile.getAbsolutePath() + " 삭제 성공");
                            takeFile = null;

                        }
                    }
                }

            }

            finish();

        }else if(resultCode== Activity.RESULT_OK){
            if(requestCode==VM_ENUM.PICK_FROM_ALBUM){
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    if(uri.toString().contains("video")){
                        Log.d(VM_ENUM.TAG,"[ViewActivity], video");
                        imageViewPhoto.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(uri);
                        ///videoView.start();

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

                    }else {
                        Log.d(VM_ENUM.TAG,"[ViewActivity], image");
                        try {
                            imageViewPhoto.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            setBitmapFromUri(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    Log.d(VM_ENUM.TAG,"[ViewActivity] data null임");
                }
            }
        }else if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
            Uri photoUri;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);
                Log.d(VM_ENUM.TAG, "[VM_VieActivity /PICK_FROM_CAMERA]: photoUri " + photoUri);
            } else {
                photoUri = Uri.fromFile(takeFile);
            }

            //** 비디오, 이미지 판단
            if(photoUri.toString().contains("video")){
                Log.d(VM_ENUM.TAG,"[ViewActivity], video");
                imageViewPhoto.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(photoUri);
                videoView.start();

            }else{
                Log.d(VM_ENUM.TAG,"[ViewActivity], image");
                try {
                    imageViewPhoto.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    setBitmapFromUri(photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFile();
            }
           //>>>>>>>>>

        }
    }

    public void rePick(View view) {
        if(pick.equals(VM_ENUM.IT_TAKE_PHOTO)){
            takePhoto();

        }else if(pick.equals(VM_ENUM.IT_GALLERY_PHOTO)){
            getAlbumFile();
        }
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
