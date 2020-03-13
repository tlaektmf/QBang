package com.visualstudy.visualmath.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.visualstudy.visualmath.R;
import com.visualstudy.visualmath.VM_DBHandler;
import com.visualstudy.visualmath.VM_ENUM;
import com.visualstudy.visualmath.data.VM_Data_CHAT;
import com.visualstudy.visualmath.dialog.VM_Dialog_registerProblem;
import com.visualstudy.visualmath.fragment.ProblemFragment;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class VM_ViewActivity extends AppCompatActivity {

    private Activity parent;

    private File takeFile;
    private PhotoView imageViewPhoto;
    private VideoView videoView;
    private String PICK_FLAG;
    private Button repickButton;
    private Button playButton;
    private Button stopButton;
    private Button startRecordButton;
    private Button stopRecordButton;

    private Uri db_save_uri;
    private String pick;

    //** 넘어온 인텐트
    private String post_id;
    private String user_type;
    private String user_id;
    private String post_title;
    private String matchset_student;
    private String matchset_teacher;
    private String index;
    //>>>>>>>>>>

    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm_view);

        parent = VM_ViewActivity.this;

        imageViewPhoto = (PhotoView) findViewById(R.id.iv_photo);
        videoView = findViewById(R.id.iv_video);
        repickButton = findViewById(R.id.ib_repick);
        playButton = findViewById(R.id.btn_play);
        stopButton = findViewById(R.id.btn_stop);
        startRecordButton = findViewById(R.id.btn_start_record);
        stopRecordButton = findViewById(R.id.btn_finish_record);
        surfaceView = findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();


        //** 인텐트 확인
        PICK_FLAG = getIntent().getStringExtra(VM_ENUM.IT_PICK_FLAG);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, PICK_FLAG  " + PICK_FLAG);
        post_id = getIntent().getStringExtra(VM_ENUM.IT_POST_ID);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, post_id  " + post_id);
        user_type = getIntent().getStringExtra(VM_ENUM.IT_USER_TYPE);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, user_type  " + user_type);
        user_id = getIntent().getStringExtra(VM_ENUM.IT_USER_ID);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, user_id  " + user_id);
        post_title = getIntent().getStringExtra(VM_ENUM.IT_POST_TITLE);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, post_title  " + post_title);
        matchset_student = getIntent().getStringExtra(VM_ENUM.IT_MATCHSET_STD);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, matchset_student  " + matchset_student);
        matchset_teacher = getIntent().getStringExtra(VM_ENUM.IT_MATCHSET_TEA);
        Log.d(VM_ENUM.TAG, "[VM_ViewActivity]onCreate 호출, matchset_teacher  " + matchset_teacher);
        //>>>>

        if (PICK_FLAG.equals(VM_ENUM.IT_TAKE_PHOTO)) {
            repickButton.setBackgroundResource(R.drawable.pv_camera_btn);
        } else if (PICK_FLAG.equals(VM_ENUM.IT_GALLERY_PHOTO)) {
            repickButton.setBackgroundResource(R.drawable.pv_gallery_btn);
        } else if (PICK_FLAG.equals(VM_ENUM.IT_TAKE_VIDEO)) {
            repickButton.setBackgroundResource(R.drawable.pv_camera_btn);
        }

        switch (PICK_FLAG) {
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
            case VM_ENUM.IT_TAKE_VIDEO:
                Log.d(VM_ENUM.TAG, "[동영상을 새로 촬영합니다]. photoview -> regiother] ");
                //** 권한 체크
                tedPermission(VM_ENUM.IT_TAKE_VIDEO);
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
        pick = VM_ENUM.IT_GALLERY_PHOTO;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(intent, VM_ENUM.PICK_FROM_ALBUM); //앨범 화면으로 이동


    }

    public void takePhoto() {
        pick = VM_ENUM.IT_TAKE_PHOTO;

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
                        "com.visualstudy.visualmath.provider", takeFile);

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


    public void takeVideo() {
        pick = VM_ENUM.IT_TAKE_VIDEO;

        try {
            takeFile = createVideoFile(); //파일 경로가 담긴 빈 이미지 생성(아직 content provider이 입혀져 있지 않음)

        } catch (IOException e) {
            Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }


        if (takeFile != null) {
            surfaceView.setVisibility(View.VISIBLE);
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            ///mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            mediaRecorder.setOutputFile(takeFile.getAbsolutePath());
            try {
                startRecordButton.setVisibility(View.VISIBLE);
                mediaRecorder.prepare();


            } catch (IOException e) {
                e.printStackTrace();
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

    private File createVideoFile() throws IOException {//** 파일 경로를 이미 생성(시간으로 중복문제 해결)

        // 이미지 파일 이름 ( {시간})
        String timeStamp = new SimpleDateFormat("HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = timeStamp;

        // 이미지가 저장될 폴더 이름 ( userID )
        File storageDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            Log.d(VM_ENUM.TAG, "[Q 상위 버전] " + storageDir);
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory() + "/" + "visual_math" + "/");
            Log.d(VM_ENUM.TAG, "[Q 하위 버전] " + storageDir);
        }


        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File video = File.createTempFile(
                imageFileName,
                ".mp4"
                , storageDir);


        return video;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveVideoFile() {


        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "VisualMath");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, takeFile.getName());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 파일을 write중이라면 다른곳에서 데이터요구를 무시하겠다는 의미.
            values.put(MediaStore.Video.Media.IS_PENDING, 1);
        }

        ContentResolver contentResolver = getContentResolver();

        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri item = contentResolver.insert(collection, values);

        try {
            assert item != null;
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
            if (pdf == null) {

            } else {

                InputStream inputStream = new FileInputStream(takeFile);
//                byte[] strToByte = getBytes(inputStream);
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
//                fos.write(strToByte);

                byte[] buf = new byte[8192];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveFile() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, takeFile.getName());
        values.put(MediaStore.Video.Media.RELATIVE_PATH, "Pictures/" + "VisualMath");
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
                Toast.makeText(VM_ViewActivity.this, "사용자 권한 요청 완료", Toast.LENGTH_SHORT).show();

                if (VIEW.equals(VM_ENUM.IT_TAKE_PHOTO)) {
                    takePhoto();
                } else if (VIEW.equals(VM_ENUM.IT_GALLERY_PHOTO)) {
                    getAlbumFile();
                } else if (VIEW.equals(VM_ENUM.IT_TAKE_VIDEO)) {
                    takeVideo();
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
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA) // ** 오디오 권한을 넣기 위한 권한 추가 Manifest.permission.RECORD_AUDIO
                .check();


    }

    public void cancel(View view) {
        finish();
    }

    public void send(View view) {
        //** 데이터베이스 저장

        String mimType = findMimeType(db_save_uri);
        if (mimType.contains("video")) { //비디오인 경우 //////db_save_uri.toString().contains("video")
            Log.d(VM_ENUM.TAG, "비디오 데이터 채팅에 추가");
            VM_Data_CHAT data = new VM_Data_CHAT(user_type, db_save_uri.toString(), VM_ENUM.CHAT_VIDEO);
            loadDatabase(post_id, data);
        } else {//이미지 타입인 경우
            Log.d(VM_ENUM.TAG, "이미지 데이터 채팅에 추가");
            VM_Data_CHAT data = new VM_Data_CHAT(user_type, db_save_uri.toString(), VM_ENUM.CHAT_IMAGE);
            loadDatabase(post_id, data);
        }


    }

    public String findMimeType(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        Log.w(VM_ENUM.TAG, "[VM_RegisterActivity] mimetype:" + mimeType);


        return mimeType;

    }

    public void loadDatabase(final String post_id, final VM_Data_CHAT chatItem) {

        //** 유효한 데이터인지 검사
        if (user_type.equals(VM_ENUM.TEACHER)) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(VM_ENUM.DB_TEACHERS)
                    .child(user_id)
                    .child(VM_ENUM.DB_TEA_POSTS)
                    .child(VM_ENUM.DB_TEA_UNSOLVED);
            ref.orderByKey().equalTo(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() == null) {
                        Log.d(VM_ENUM.TAG, "문제가 이미 완료됨");

                        final VM_Dialog_registerProblem checkDialog =
                                new VM_Dialog_registerProblem(parent);
                        checkDialog.callFunction(8, parent);

                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                VM_Dialog_registerProblem.dig.dismiss();
                                t.cancel();
                                Intent intent = new Intent(VM_ViewActivity.this, TeacherHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();//***** 종료
                            }
                        }, 2000);
                    } else {
                        //** 프로그래스바 설정
                        Log.w(VM_ENUM.TAG, "[VM_ViewActivity] ProblemFragment에 프로그래스바 설정");
                        ProblemFragment.chat_loading_bar.setVisibility(View.VISIBLE);

                        VM_DBHandler dbHandler = new VM_DBHandler();
                        dbHandler.newChatMediaItem(post_id, chatItem, user_id);
                        Log.d(VM_ENUM.TAG, "문제가 유효함. 채팅을 추가");
                        dbHandler.newMessageAlarm(post_id, post_title, user_type, matchset_student, VM_ENUM.ALARM_NEW);
                        Log.d(VM_ENUM.TAG, "문제가 유효함. 학생 알람을 추가" + user_type + "," + matchset_student);
                        setResult(RESULT_OK);
                        finish();//***** 종료

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (user_type.equals(VM_ENUM.STUDENT)) {
            //학생이면 검사 없이 그냥 채팅 추가

            //** 프로그래스바 설정
            Log.w(VM_ENUM.TAG, "[VM_ViewActivity] ProblemFragment에 프로그래스바 설정");
            ProblemFragment.chat_loading_bar.setVisibility(View.VISIBLE);

            VM_DBHandler dbHandler = new VM_DBHandler();
            dbHandler.newChatMediaItem(post_id, chatItem, user_id);
            Log.d(VM_ENUM.TAG, "문제가 유효함. 채팅을 추가");
            dbHandler.newMessageAlarm(post_id, post_title, user_type, matchset_teacher, VM_ENUM.ALARM_NEW);
            Log.d(VM_ENUM.TAG, "문제가 유효함. 선생님 알람을 추가" + user_type + "," + matchset_teacher);

            setResult(RESULT_OK);
            finish();//***** 종료

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(VM_ENUM.TAG, "[ViewActivity]" + requestCode);

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
        } else {
            if (requestCode == VM_ENUM.PICK_FROM_ALBUM) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    db_save_uri = uri;

                    //** 비디오, 이미지 판단
                    String mimeType = findMimeType(uri);
                    if (mimeType != null) {
                        if (mimeType.contains("video")) {
                            Log.d(VM_ENUM.TAG, "[ViewActivity], video");
                            imageViewPhoto.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(uri);
                            ///videoView.start();

                            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    Log.d(VM_ENUM.TAG, "동영상 재생준비 완료");
                                    playButton.setVisibility(View.VISIBLE);
                                    stopButton.setVisibility(View.GONE);

                                }
                            });
                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    Log.d(VM_ENUM.TAG, "동영상 재생 완료");
                                    playButton.setVisibility(View.VISIBLE);
                                    stopButton.setVisibility(View.GONE);
                                }
                            });

                        } else if (mimeType.contains("image")) { //gif 파일도 가능하긴 함...
                            Log.d(VM_ENUM.TAG, "[ViewActivity], image");
                            try {
                                imageViewPhoto.setVisibility(View.VISIBLE);
                                videoView.setVisibility(View.GONE);
                                setBitmapFromUri(uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                } else {
                    Log.d(VM_ENUM.TAG, "[ViewActivity] data null임");
                }
            } else if (requestCode == VM_ENUM.PICK_FROM_CAMERA) { //카메라로 촬영
                Uri photoUri;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoUri = FileProvider.getUriForFile(this,
                            "com.visualstudy.visualmath.provider", takeFile);
                    Log.d(VM_ENUM.TAG, "[VM_VieActivity /PICK_FROM_CAMERA]: photoUri " + photoUri);
                } else {
                    photoUri = Uri.fromFile(takeFile);
                }

                //** 데이터베이스에 저장할 uri 생성
                db_save_uri = photoUri;

                //** 비디오, 이미지 판단 (카메라로 촬영)
                String flag = null;
                String mimeType = findMimeType(photoUri);
                if (mimeType != null) {
                    if (mimeType.contains("video")) {
                        Log.d(VM_ENUM.TAG, "[ViewActivity], video");
                        flag = VM_ENUM.IT_TAKE_VIDEO;
                        imageViewPhoto.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(photoUri);
                        videoView.start();

                    } else if (mimeType.contains("image")) {
                        Log.d(VM_ENUM.TAG, "[ViewActivity], image");
                        flag = VM_ENUM.IT_TAKE_PHOTO;
                        try {
                            imageViewPhoto.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            setBitmapFromUri(photoUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

//                if(photoUri.toString().contains("video")){
//                    Log.d(VM_ENUM.TAG,"[ViewActivity], video");
//                    flag=VM_ENUM.IT_TAKE_VIDEO;
//                    imageViewPhoto.setVisibility(View.GONE);
//                    videoView.setVisibility(View.VISIBLE);
//                    videoView.setVideoURI(photoUri);
//                    videoView.start();
//
//                }else{
//                    Log.d(VM_ENUM.TAG,"[ViewActivity], image");
//                    flag=VM_ENUM.IT_TAKE_PHOTO;
//                    try {
//                        imageViewPhoto.setVisibility(View.VISIBLE);
//                        videoView.setVisibility(View.GONE);
//                        setBitmapFromUri(photoUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }

                //Q 이상이면 디렉토리 내 파일 다시 저장
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (flag.equals(VM_ENUM.IT_TAKE_VIDEO)) {
                        saveVideoFile();
                    } else if (flag.equals(VM_ENUM.IT_TAKE_PHOTO)) {
                        saveFile();
                    }

                }
                //>>>>>>>>>

            }
        }
    }

    public void rePick(View view) {

        playButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

        if (pick.equals(VM_ENUM.IT_TAKE_PHOTO)) {
            db_save_uri = null;
            imageViewPhoto.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            takePhoto();

        } else if (pick.equals(VM_ENUM.IT_GALLERY_PHOTO)) {
            db_save_uri = null;
            imageViewPhoto.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            getAlbumFile();
        } else if (pick.equals(VM_ENUM.IT_TAKE_VIDEO)) {
            db_save_uri = null;
            imageViewPhoto.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            takeVideo();
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

    //** 비디오 촬영 시작
    public void startRecord(View view) {
        mediaRecorder.start();   // Recording is now started
        startRecordButton.setVisibility(View.GONE);
        stopRecordButton.setVisibility(View.VISIBLE);
    }

    //** 비디오 촬영 종료
    public void finishRecord(View view) {

        startRecordButton.setVisibility(View.GONE);
        stopRecordButton.setVisibility(View.GONE);

        mediaRecorder.stop();
        mediaRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
        mediaRecorder.release();

    }
}
