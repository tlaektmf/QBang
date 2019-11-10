package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class VM_RegisterProblemActivity extends AppCompatActivity {

    private static final String TAG="RegisterProblem";
    private static final int PICK_FROM_ALBUM = 1; //onActivityResult 에서 requestCode 로 반환되는 값
    private static final int PICK_FROM_CAMERA = 2;
    private static final int OTHER_DATA_LOAD=3;
    private static final String DETAIL="detail";
    private static final String ALL="all";

    private int returnResult;
    private static final int GALLERY=1;
    private static final int CAMERA=2;
    private static final int NOTHING=-1;

    private File galleryFile; //갤러리로부터 받아온 이미지를 저장
    private final CharSequence[] gradeItems = {"초등", "중등", "고등"};

    private EditText editTextTitle;
    private Button buttonGrade;
    private Button buttonGoOther;
    private ImageView imageViewProblem;
    private VM_Data_ADD receiveData;
    private VM_Data_ADD sendData;
    private VM_Data_BASIC vmDataBasic;


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__register_problem);

        //** ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        init();


    }

    public void init(){
        buttonGrade=findViewById(R.id.btn_grade);
        imageViewProblem=findViewById(R.id.iv_file_problem);
        buttonGoOther=findViewById(R.id.btn_goOther);
        editTextTitle=findViewById(R.id.et_title);
        returnResult=NOTHING;
        firebaseAuth = FirebaseAuth.getInstance();//파이어베이스 인증 객체 선언
        storageReference = FirebaseStorage.getInstance().getReference(); //파이어베이스 저장소
        firebaseDatabase = FirebaseDatabase.getInstance();//파이어베이스 데이터 베이스
        vmDataBasic=new VM_Data_BASIC();

    }


    //** 사용자 갤러리에 접근 or 카메라에 접근
    public void changeProblemFile(View view) {
        //** 권한 설정
        tedPermission();

    }

    public void getAlbumFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM); //앨범 화면으로 이동
        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */
    }
    public void takePhoto(){
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

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", galleryFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {
                Uri photoUri = Uri.fromFile(galleryFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //galleryFile 의 Uri경로를 intent에 추가 -> 카메라에서 찍은 사진이 저장될 주소를 의미
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }


        }

    }
    public void changeGrade(View view) {
        //**초등, 중등, 고등 선택
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        ///oDialog.setPositiveButton("선택",null);
        ///oDialog.setNeutralButton("취소",null);
        oDialog.setTitle("학년을 선택해 주세요.");

        oDialog.setItems(gradeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonGrade.setText(gradeItems[which]);
               String grade=gradeItems[which].toString();
               vmDataBasic.setGrade(grade);
            }
        }).setCancelable(true)
                .show();
    }

    public void addOther(View view) {
        Intent intent;
        intent = new Intent(VM_RegisterProblemActivity.this, VM_RegiserOtherThingsActivity.class);
        sendData=receiveData;
        intent.putExtra(ALL,sendData); //Parcel객체인 sendData intent에 추가
        startActivityForResult(intent,OTHER_DATA_LOAD);
    }


    private void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(galleryFile.getAbsolutePath(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경
        imageViewProblem.setImageBitmap(originalBm); //이미지 set
        if(originalBm==null){
            Log.i(TAG,"null");
        }
    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( {시간})
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = timeStamp ;

        // 이미지가 저장될 폴더 이름 ( userID )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/"+"userID"+"/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;

    }

    /**
     *  startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
     *  이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
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

            if(galleryFile != null) {
                if (galleryFile.exists()) {
                    if (galleryFile.delete()) {
                        Log.e(TAG, galleryFile.getAbsolutePath() + " 삭제 성공");
                        galleryFile = null;
                    }
                }
            }

            return;
        }

        else if(requestCode==PICK_FROM_ALBUM){
            Uri photo_problem=data.getData();// data.getData() 를 통해 갤러리에서 선택한 이미지의 Uri 를 받아 옴
            Cursor cursor=null;

            //**  cursor 를 통해 스키마를 content:// 에서 file:// 로 변경 -> 사진이 저장된 절대경로를 받아오는 과정
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                assert photo_problem != null;
                cursor = getContentResolver().query(photo_problem, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                galleryFile = new File(cursor.getString(column_index));
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            vmDataBasic.setProblem(photo_problem);

            //데이터 등록
            setImage();
        }
        else if (requestCode == PICK_FROM_CAMERA) {
            Uri photoUri;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", galleryFile);

            }
            else {
                photoUri = Uri.fromFile(galleryFile);
            }
            vmDataBasic.setProblem(photoUri);
            setImage();
        }
        else if(requestCode==OTHER_DATA_LOAD){
            if(resultCode==RESULT_OK){
                String notice="";
                //VM_Data_ADD receiveData=data.getParcelableExtra(ALL);
                receiveData=data.getParcelableExtra(ALL);

                if(!receiveData.getDetail().equals("")){
                    notice="추가 설명 등록.";
                    Log.i(TAG,receiveData.getDetail()+"떠야돼");
                }
                int count=0;
                for(int i=0;i<3;i++){

                    if(receiveData.getFilePathElement(i)!=null){
                        count++;
                        Log.i(TAG,receiveData.getFilePathElement(i).toString()+"사진?");
                    }
                }
                if(count!=0){
                    notice+="사진 "+count+"개 추가.";
                }

                //** 버튼에 정보 표시
                if(notice!=""){
                    buttonGoOther.setText(notice);
                }else if (notice==""){
                    buttonGoOther.setText("본인 풀이 또는 질문 내용 추가");
                }

                Log.i(TAG,count+"");
            }

//
//            try {
//                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), receiveData.getFilePathElement(0));
//                imageViewProblem.setImageBitmap(bm);
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }




    }

    private void showPickDialog(){
        //** 다이얼로그 실행
        // 커스텀 다이얼로그를 생성
        final VM_Dialog_PickHowToGetPicture dialog = new VM_Dialog_PickHowToGetPicture(VM_RegisterProblemActivity.this);

        // 커스텀 다이얼로그의 결과를 담을 매개변수로 같이 넘겨준다.

        dialog.setDialogListener(new VM_DialogListener_PickHowToGetPicture() {
            @Override
            public void onButtonTakePhotoClicked() {
                returnResult=CAMERA;
                dialog.setReturnResult(CAMERA);
                takePhoto();
            }

            @Override
            public void onButtonGetAlbumFileClicked() {
                returnResult=GALLERY;
                dialog.setReturnResult(GALLERY);
                getAlbumFile();
            }
        });
        dialog.callFunction();// 커스텀 다이얼로그를 호출
//        Log.i(TAG,dialog.getReturnResult()+"");
//        Log.i(TAG,returnResult+"");

//                if(dialog.getReturnResult()==GALLERY){
//                    getAlbumFile();
//
//                }else if(dialog.getReturnResult()==CAMERA){
//                    takePhoto();
//
//                }else if(dialog.getReturnResult()==NOTHING){
//
//                }


    }
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 권한 요청 성공
                Toast.makeText(VM_RegisterProblemActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();

                showPickDialog();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ** 권한 요청 실패
                Toast.makeText(VM_RegisterProblemActivity.this,deniedPermissions.toString(),Toast.LENGTH_SHORT).show();
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

    public void registerProblem(View view) {
        //** 데이터베이스에 저장 && storage에 upload
        vmDataBasic.setTitle(editTextTitle.getText().toString());
        VM_DBHandler vmDbHandler=new VM_DBHandler("POSTS");
        vmDbHandler.newPost(receiveData,vmDataBasic);

        finish();
    }

    public void cancel(View view) {
        finish();
    }


}
