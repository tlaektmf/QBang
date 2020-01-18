package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class VM_RegiserOtherThingsActivity extends AppCompatActivity {
    private static final String TAG = VM_ENUM.TAG;

    private EditText editTextdetail;
    private ImageView[] imageViewsOtherPictureList;

    private int imageviewID;
    private File takeFile;
    private File galleryFile; //갤러리로부터 받아온 이미지를 저장
    private VM_Data_ADD vm_data_add; //<내용추가뷰> -> <문제등록뷰>
    private VM_Data_ADD receiveData;//** <문제등록뷰> -> <내용추가뷰> 기존의 <내용추가뷰>의 항목을 유지시키기 위해서 // 이 뷰에서 변경 될 일 없음


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__regiser_other_things);

        init();

    }

    public void init() {

        editTextdetail = findViewById(R.id.tv_detail);
        imageviewID = -1;
        imageViewsOtherPictureList = new ImageView[3];
        imageViewsOtherPictureList[0] = findViewById(R.id.iv_picture1);
        imageViewsOtherPictureList[1] = findViewById(R.id.iv_picture2);
        imageViewsOtherPictureList[2] = findViewById(R.id.iv_picture3);
        vm_data_add = new VM_Data_ADD();

        //** 데이터 넘어온 게 있는 지 확인
        Intent intent = getIntent();
        if (intent != null) {// 넘어온 데이터가 있는 경우
            receiveData = intent.getParcelableExtra(VM_ENUM.ALL);

            if (receiveData != null) {//혹시 모르니 한번 더 확인
                vm_data_add = receiveData;
                init_set();
            }

        }

    }


    public void init_set() {
        //1. edittext 채우기
        editTextdetail.setText(vm_data_add.getDetail());

        //2. picture 채우기
        for (int i = 0; i < 3; i++) {
            Uri path = vm_data_add.getFilePathElement(i);

            if (path != null) {
                Log.i(VM_ENUM.TAG, "init_set: " + vm_data_add.getFilePathElement(i));
                setImageByIndex(i);
            } else {
                Log.i(VM_ENUM.TAG, "init_set: " + "파일 없음");
            }
        }
    }

    //** 사용자 카메라, 사진첩에 접근하여 파일 추가
    public void changeAddFile(View view) {
        //** 권한 설정
        imageviewID = view.getId();

        int picViewIndex = 0;
        switch (imageviewID) {
            case R.id.iv_picture1:
                picViewIndex = 0;
                break;
            case R.id.iv_picture2:
                picViewIndex = 1;
                break;
            case R.id.iv_picture3:
                picViewIndex = 2;
                break;
        }
        Log.d(TAG,"[pickViewIndex] "+picViewIndex);

        if(vm_data_add!=null){
            if(vm_data_add.getFilePathElement(picViewIndex)!=null){//** imageView에 이미지가 있는 경우
                Intent intent=new Intent(VM_RegiserOtherThingsActivity.this,VM_PhotoViewActivity.class);
                intent.putExtra(VM_ENUM.PHOTO_URI, vm_data_add.getFilePathElement(picViewIndex));
                intent.putExtra(VM_ENUM.IT_PHOTO_INDEX,picViewIndex);
                Log.d(TAG,"[전달할 사진 uri] "+ vm_data_add.getFilePathElement(picViewIndex));
                startActivityForResult(intent, VM_ENUM.RC_REGIOTHER_TO_PHOTOVIEW);

            }else{//** imageView에 이미지가 없는 경우
                tedPermission(imageviewID);
            }
        }else{//** 제일 초기 클릭
            tedPermission(imageviewID);
        }

        ///tedPermission(imageviewID);
    }

    //** 추가 정보 등록 버튼 클릭
    public void loadAddInfo(View view) {

        if(receiveData==null){
            Log.d(TAG, "[VM_RegisterOtherProblemActivity]: receiveData==null");
        }
        if(editTextdetail.getText().toString().replace(" ", "").equals("")){
            Log.d(TAG, "[VM_RegisterOtherProblemActivity]: editTextdetail==공백");
        }
        Log.d(TAG, "[VM_RegisterOtherProblemActivity]: loadAddInfo");


        //** 문제 등록의 최소 요건 확인
//        if (checkAbility()) {
//            Log.d(TAG, "[VM_RegisterOtherProblemActivity]: 문제 등록 요구사항 만족");

            //** vm_data 객체 생성
           if(!editTextdetail.getText().toString().replace(" ", "").equals("")){
                String data_detail = editTextdetail.getText().toString();
                vm_data_add.setDetail(data_detail);
        Log.d(TAG, "[VM_RegisterOtherProblemActivity]: data_detail"+1+data_detail+2);
            }else{
               vm_data_add.setDetail("");
            }

            Intent intent = new Intent(this, VM_RegisterProblemActivity.class);
            intent.putExtra(VM_ENUM.ALL, vm_data_add); //Parcel객체인 vm_data_add를 intent에 추가
            setResult(RESULT_OK, intent);

            finish(); //-> 이렇게 하면 다시 돌아 왔을 때, 정보 유지 안됨

//        } else {
//            Log.d(TAG, "[VM_RegisterProblemActivity]: 문제 등록 요구사항 만족 못함");
//        }

    }

    public void getAlbumFile(final int _imageviewID) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, VM_ENUM.PICK_FROM_ALBUM); //앨범 화면으로 이동

        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */
    }

    public void takePhoto(final int _imageviewID) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent를 통해 카메라 화면으로 이동함

        try {
            takeFile = createImageFile(); //파일 경로가 담긴 빈 이미지 생성
        } catch (IOException e) {
            Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if (takeFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, VM_ENUM.PICK_FROM_CAMERA);

            } else {
                Uri photoUri = Uri.fromFile(takeFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //galleryFile 의 Uri경로를 intent에 추가 -> 카메라에서 찍은 사진이 저장될 주소를 의미
                startActivityForResult(intent, VM_ENUM.PICK_FROM_CAMERA);

            }

        }

    }


    private File createImageFile() throws IOException {

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
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveFile() {
        ///String seconDir = "visual_math";
        ///Log.d(VM_ENUM.TAG,"[VM_RegisterProblem], seconDir "+seconDir);
        ContentValues values = new ContentValues();
        ///values.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, seconDir);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, takeFile.getName());

        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 파일을 write중이라면 다른곳에서 데이터요구를 무시하겠다는 의미.
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        ContentResolver contentResolver = getContentResolver();

        //Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
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

    private void setImageByUri(int index,Uri _uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(_uri.toString(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경
        if (originalBm != null) {
            imageViewsOtherPictureList[index].setImageBitmap(originalBm); //이미지 set
            vm_data_add.setFilePathElement(_uri,index);
        }

    }

    private void deletePhoto(int index){
        imageViewsOtherPictureList[index].setImageResource(R.drawable.add_extra_img);
        vm_data_add.setFilePathElement(null,index);
    }
    private void createData(Uri _uri) {
        int index = 0;
        switch (imageviewID) {
            case R.id.iv_picture1:
                index = 0;
                break;
            case R.id.iv_picture2:
                index = 1;
                break;
            case R.id.iv_picture3:
                index = 2;
                break;
        }

        Log.i(TAG, "createdata: " + index);
        vm_data_add.setFilePathElement(_uri, index);

    }

    private void setImageIDandURI(final int _imageviewID,Uri _uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(_uri.toString(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경

        int index = 0;
        switch (_imageviewID) {
            case R.id.iv_picture1:
                index = 0;
                break;
            case R.id.iv_picture2:
                index = 1;
                break;
            case R.id.iv_picture3:
                index = 2;
                break;
        }
        if(originalBm!=null){
            imageViewsOtherPictureList[index].setImageBitmap(originalBm); //이미지 set
        }

    }


    /***
     * setImage override
     * 이전 Activity인 RegisterProblemAcitivy 에서 RegisterOtherActivity로 다시 넘어오는 경우
     * RegisterOtherActivity의 정보를 지우지 않고 유지시킨다.
     *
     * @param index
     *
     */
    private void setImageByIndex(int index) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(receiveData.getFilePathElement(index).toString(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경
        imageViewsOtherPictureList[index].setImageBitmap(originalBm); //이미지 set

    }

    private void showPickDialog(final int _imageviewID) {
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
                Toast.makeText(VM_RegiserOtherThingsActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                showPickDialog(_imageviewID);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ** 권한 요청 실패
                Toast.makeText(VM_RegiserOtherThingsActivity.this, deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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



    private Bitmap getBitmapFromUri(Uri uri,int imageviewID) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        if (image != null) {
            imageViewsOtherPictureList[imageviewID].setImageBitmap(image); //이미지 set
        }

        return image;
    }

    /**
     * startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
     * 이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(VM_ENUM.TAG,"[VM_RegisterOtherActivity/requestCode] "+requestCode);
        //** 예외 사항 처리
        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "선택이 취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(requestCode==VM_ENUM.PICK_FROM_CAMERA){
                if (takeFile != null) {
                    if (takeFile.exists()) {
                        if (takeFile.delete()) {
                            Log.e(TAG, takeFile.getAbsolutePath() + " 삭제 성공");
                            takeFile = null;
                        }
                    }
                }
            }


            return;
        } else if (requestCode == VM_ENUM.PICK_FROM_ALBUM) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    Log.i(TAG, "Uri: " + uri.toString());
                    ///dumpImageMetaData(uri);
                    try {
                        createData(uri);
                        getBitmapFromUri(uri,imageviewID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

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
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            createData(Uri.parse(galleryFile.getAbsolutePath()));
            ///createData(photo_problem);
            setImageIDandURI(imageviewID,Uri.parse(galleryFile.getAbsolutePath()));
        } else if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
            Uri photoUri;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);

            } else {
                photoUri = Uri.fromFile(takeFile);
            }
            ///createData(photoUri);
            createData(Uri.parse(takeFile.getAbsolutePath()));
            setImageIDandURI(imageviewID,Uri.parse(takeFile.getAbsolutePath()));

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                saveFile();
            }
        }else if (requestCode == VM_ENUM.RC_READ_REQUEST_CODE) {//** 사용 안함
            // The ACTION_OPEN_DOCUMENT intent was sent with the request code
            // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
            // response to some other intent, and the code below shouldn't run at all.

            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                ///dumpImageMetaData(uri);
                try {
                    getBitmapFromUri(uri,imageviewID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode==VM_ENUM.RC_REGIOTHER_TO_PHOTOVIEW){

            int deleteIndex ;
            int photoIndex ;
            Uri newGalleryPhotoURi;
            Uri newTakePhotoURi;


            deleteIndex=data.getIntExtra(VM_ENUM.IT_DELETE_PHOTO_INDEX,-1);
            Log.d(VM_ENUM.TAG,"[VM_RegiOtherActivity] deleteIndex"+     deleteIndex);
            if(deleteIndex!=-1){
                Log.d(VM_ENUM.TAG,"[VM_RegiOtherActivity] deletePhoto");
               deletePhoto(deleteIndex);
            }

            //**
            photoIndex=data.getIntExtra(VM_ENUM.IT_PHOTO_INDEX,-1);
            Log.d(VM_ENUM.TAG,"[VM_RegiOtherActivity] photoIndex"+     photoIndex);
            newGalleryPhotoURi=data.getParcelableExtra(VM_ENUM.IT_GALLERY_PHOTO);
            if(newGalleryPhotoURi!=null&& photoIndex!=-1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    try {
                        getBitmapFromUri(newGalleryPhotoURi,photoIndex);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    setImageByUri(photoIndex,newGalleryPhotoURi);
                }

            }

            //** IT_TAKE_PHOTO
            newTakePhotoURi= data.getParcelableExtra(VM_ENUM.IT_TAKE_PHOTO);
            if(newTakePhotoURi!=null&& photoIndex!=-1){
                Log.d(VM_ENUM.TAG,"[VM_RegiOtherActivity] IT_TAKE_PHOTO");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    try {
                        getBitmapFromUri(newGalleryPhotoURi,photoIndex);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    setImageByUri(photoIndex,newTakePhotoURi);
                }

            }


        }

    }

}
