package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class VM_RegiserOtherThingsActivity extends AppCompatActivity {
    private static final String TAG="RegisterOther";
    private static final int PICK_FROM_ALBUM = 1; //onActivityResult 에서 requestCode 로 반환되는 값
    private static final int PICK_FROM_CAMERA = 2;
    private static final int GO_BACK=3;


    private static final String ALL="all";
    public EditText editTextdetail;
    private ImageView[] imageViewsOtherPictureList;

    private int imageviewID;

    private File galleryFile; //갤러리로부터 받아온 이미지를 저장
    VM_Data_ADD vm_data_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__regiser_other_things);

        init();


    }

    public void init(){
        //data_detail=null;
        editTextdetail=findViewById(R.id.tv_detail);
        imageviewID=-1;
        imageViewsOtherPictureList=new ImageView[3];
        imageViewsOtherPictureList[0]=findViewById(R.id.iv_picture1);
        imageViewsOtherPictureList[1]=findViewById(R.id.iv_picture2);
        imageViewsOtherPictureList[2]=findViewById(R.id.iv_picture3);
        vm_data_add=new VM_Data_ADD();

    }

    //** 사용자 카메라, 사진첩에 접근하여 파일 추가
    public void changeAddFile(View view) {
        //** 권한 설정
        imageviewID=view.getId();
        tedPermission(imageviewID);
    }

    //** 추가 정보 등록 버튼 클릭
    public void loadAddInfo(View view) {
        //** vm_data 객체 생성
        String data_detail=editTextdetail.getText().toString();
        vm_data_add.setDetail(data_detail);

        Intent intent= new Intent(this,VM_RegisterProblemActivity.class);
        intent.putExtra(ALL,vm_data_add); //Parcel객체인 vm_data_add를 intent에 추가

        setResult(RESULT_OK, intent);

        finish(); //-> 이렇게 하면 다시 돌아 왔을 때, 정보 유지 안됨


    }

    public void getAlbumFile(final int _imageviewID){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.putExtra(VIEW_ID,_imageviewID);
        startActivityForResult(intent, PICK_FROM_ALBUM); //앨범 화면으로 이동

        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */
    }

    public void takePhoto(final int _imageviewID){

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
                //intent.putExtra(VIEW_ID,_imageviewID);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {
                Uri photoUri = Uri.fromFile(galleryFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //galleryFile 의 Uri경로를 intent에 추가 -> 카메라에서 찍은 사진이 저장될 주소를 의미
                //intent.putExtra(VIEW_ID,_imageviewID);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }

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
//            int viewID=data.getIntExtra(VIEW_ID,-1);
//            Log.i(TAG,"request: "+data.getIntExtra("temp",-1));

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

            createData(photo_problem);
            setImage(imageviewID);
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
            createData(photoUri);
            setImage(imageviewID);
        }

    }

    private void createData(Uri _uri){
        int index=0;
        switch (imageviewID){
            case R.id.iv_picture1:
                index=0;
                break;
            case R.id.iv_picture2:
                index=1;
                break;
            case R.id.iv_picture3:
                index=2;
                break;
        }

        vm_data_add.setFilePathElement(_uri,index);
    }

    private void setImage(final int _imageviewID) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(galleryFile.getAbsolutePath(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경

        int index=0;
        switch (_imageviewID){
            case R.id.iv_picture1:
                index=0;
                break;
            case R.id.iv_picture2:
                index=1;
                break;
            case R.id.iv_picture3:
                index=2;
                break;
        }

        imageViewsOtherPictureList[index].setImageBitmap(originalBm); //이미지 set
    }
    private void showPickDialog(final int _imageviewID){
        //** 다이얼로그 실행
        // 커스텀 다이얼로그를 생성
        final VM_Dialog_PickHowToGetPicture dialog = new VM_Dialog_PickHowToGetPicture(VM_RegiserOtherThingsActivity.this);

        // 커스텀 다이얼로그의 결과를 담을 매개변수로 같이 넘겨준다.

        dialog.setDialogListener(new VM_DialogListener_PickHowToGetPicture() {
            @Override
            public void onButtonTakePhotoClicked() {

                takePhoto(_imageviewID);
            }

            @Override
            public void onButtonGetAlbumFileClicked() {

                getAlbumFile(_imageviewID);
            }
        });
        dialog.callFunction();// 커스텀 다이얼로그를 호출



    }
    private void tedPermission(final int _imageviewID) {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 권한 요청 성공
                Toast.makeText(VM_RegiserOtherThingsActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();

                showPickDialog(_imageviewID);

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ** 권한 요청 실패
                Toast.makeText(VM_RegiserOtherThingsActivity.this,deniedPermissions.toString(),Toast.LENGTH_SHORT).show();
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

    public void cancel(View view) {
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) { //** 액티비티의 상태를 저장
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) { //** 액티비티의 상태를 복원
        super.onRestoreInstanceState(savedInstanceState);
    }
}
