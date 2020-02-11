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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.VM_RegisterProblemActivity;
import com.example.visualmath.data.VM_Data_ADD;
import com.example.visualmath.dialog.VM_DialogListener_PickHowToGetPicture;
import com.example.visualmath.dialog.VM_Dialog_PickHowToGetPicture;
import com.example.visualmath.dialog.VM_Dialog_registerProblem;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
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

public class VM_RegiserOtherThingsActivity extends AppCompatActivity {
    private static final String TAG = VM_ENUM.TAG;
    public static VM_RegiserOtherThingsActivity activity = null;    //액티비티 변수 선언

    private EditText editTextdetail;
    private ImageView[] imageViewsOtherPictureList;

    public static VM_Data_ADD saveData;
    private int picViewIndex;
    private int imageviewID;
    private File takeFile;
    ///private File galleryFile; //갤러리로부터 받아온 이미지를 저장


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__regiser_other_things);
        activity=this;
        Log.d(VM_ENUM.TAG,"[RegiOtherActivity onCreate]");
        init();

    }

    private Bitmap getBitmap(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    public void resetWidget() throws IOException {
        //기존의 데이터와 save 가 다른 경우에만 원래의 데이터로 바꾼다.
        Log.d(VM_ENUM.TAG,"[RegiOther/ add.getDetail()]"+VM_RegisterProblemActivity.add.getDetail());
        for(int i=0;i<3;i++){
            Log.d(VM_ENUM.TAG,"[RegiOther/ add.getFilePathElement "+i+","+VM_RegisterProblemActivity.add.getFilePathElement(i));
        }

        if (VM_RegisterProblemActivity.add.getDetail()!=null){
            editTextdetail.setText(VM_RegisterProblemActivity.add.getDetail());
            saveData.setDetail(VM_RegisterProblemActivity.add.getDetail());
        }else{
            editTextdetail.setText("");
            saveData.setDetail(null);
        }


        for(int i=0;i<3;i++){
            if (VM_RegisterProblemActivity.add.getFilePathElement(i)!=null){
                Bitmap image=getBitmap(VM_RegisterProblemActivity.add.getFilePathElement(i));
                if (image != null) {
                    imageViewsOtherPictureList[i].setImageBitmap(image); //이미지 set
                    saveData.setFilePathElement(VM_RegisterProblemActivity.add.getFilePathElement(i),i);//이미지 set
                }
            }else{
                imageViewsOtherPictureList[i].setImageResource(R.drawable.add_extra_img);
                saveData.setFilePathElement(null,i);
            }
        }

    }
    public void cancel(View view) {
        try {
            resetWidget();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, VM_RegisterProblemActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        try {
            resetWidget();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, VM_RegisterProblemActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void init()  {

        editTextdetail = findViewById(R.id.tv_detail);
        imageviewID = -1;
        imageViewsOtherPictureList = new ImageView[3];
        imageViewsOtherPictureList[0] = findViewById(R.id.iv_picture1);
        imageViewsOtherPictureList[1] = findViewById(R.id.iv_picture2);
        imageViewsOtherPictureList[2] = findViewById(R.id.iv_picture3);
        saveData=new VM_Data_ADD();


    }



    //** 사용자 카메라, 사진첩에 접근하여 파일 추가
    public void changeAddFile(View view) {
        //** 권한 설정
        imageviewID = view.getId();

        picViewIndex = -1;
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
        Log.d(TAG, "[pickViewIndex 선택] " + picViewIndex);

        if (saveData!= null) {
            if (saveData.getFilePathElement(picViewIndex) != null) {//** imageView에 이미지가 있는 경우
                Intent intent = new Intent(VM_RegiserOtherThingsActivity.this, VM_PhotoViewActivity.class);
                intent.putExtra(VM_ENUM.IT_PHOTO_INDEX, picViewIndex);
                startActivityForResult(intent, VM_ENUM.RC_REGIOTHER_TO_PHOTOVIEW);

            } else {//** imageView에 이미지가 없는 경우
                tedPermission();
            }
        } else {//** 제일 초기 클릭
            tedPermission();
        }

    }

    //** 추가 정보 등록 버튼 클릭
    public void loadAddInfo(View view) {


        Log.d(VM_ENUM.TAG,"[RegiOther/ saveData.getDetail()]"+editTextdetail.getText().toString());
        for(int i=0;i<3;i++){
            Log.d(VM_ENUM.TAG,"[RegiOther/ saveData.getFilePathElement "+i+","+saveData.getFilePathElement(i));
        }

        //** 이때만 데이터 저장
        VM_RegisterProblemActivity.add.setDetail(editTextdetail.getText().toString());

        for(int i=0;i<3;i++){
            VM_RegisterProblemActivity.add.setFilePathElement(saveData.getFilePathElement(i),i);
        }

        Intent intent = new Intent(this, VM_RegisterProblemActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    public void getAlbumFile() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, VM_ENUM.PICK_FROM_ALBUM); //앨범 화면으로 이동

        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */
    }

    public void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent를 통해 카메라 화면으로 이동함

        try {
            takeFile = createImageFile(); //파일 경로가 담긴 빈 이미지 생성
            Log.d(VM_ENUM.TAG, "[RegiOther]+content provider 전 getAbsolutePath" + takeFile.getAbsolutePath());
            Log.d(VM_ENUM.TAG, "[RegiOther]+content provider 전 fromFile" + Uri.fromFile(takeFile));

        } catch (IOException e) {
            Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if (takeFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);
                Log.d(VM_ENUM.TAG, "[RegiOther]+content provider 후" + photoUri);
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


    private void deletePhoto(int index) {
        imageViewsOtherPictureList[index].setImageResource(R.drawable.add_extra_img);
        saveData.setFilePathElement(null, index);
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

        Log.i(TAG, "[RegiOther] createdata: " + index+","+_uri);

        //** 데이터 등록
        saveData.setFilePathElement(_uri,index);


    }

    private void setImageByUri(Uri _uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(_uri.toString(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경

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
        if (originalBm != null) {
            imageViewsOtherPictureList[index].setImageBitmap(originalBm); //이미지 set
        }

    }


    private void showPickDialog() {
        //** 다이얼로그 실행

        // 커스텀 다이얼로그를 생성
        final VM_Dialog_PickHowToGetPicture dialog = new VM_Dialog_PickHowToGetPicture(VM_RegiserOtherThingsActivity.this);

        // 커스텀 다이얼로그의 결과를 담을 매개변수로 같이 넘겨준다.

        dialog.setDialogListener(new VM_DialogListener_PickHowToGetPicture() {
            @Override
            public void onButtonTakePhotoClicked() {

                takePhoto();
            }

            @Override
            public void onButtonGetAlbumFileClicked() {

                getAlbumFile();
            }
        });
        dialog.callFunction();// 커스텀 다이얼로그를 호출


    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 권한 요청 성공
                Toast.makeText(VM_RegiserOtherThingsActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                showPickDialog();
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



    private void setBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        int idx = 0;
        switch (imageviewID) {
            case R.id.iv_picture1:
                idx = 0;
                break;
            case R.id.iv_picture2:
                idx = 1;
                break;
            case R.id.iv_picture3:
                idx = 2;
                break;
        }

        if (image != null) {
            imageViewsOtherPictureList[idx].setImageBitmap(image); //이미지 set
            Log.d(VM_ENUM.TAG, "[RegiOther],setBitmapFromUri-> Gallery uri" +uri );

        }

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

        Log.d(VM_ENUM.TAG, "[VM_RegisterOtherActivity/requestCode] " + requestCode);
        //** 예외 사항 처리
        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "선택이 취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
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

 //           if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    Log.i(TAG, "[RegiOther] onActivityResult Uri: " + uri.toString());

                    if(uri.toString().contains("video")){
                        final VM_Dialog_registerProblem checkDialog =
                                new VM_Dialog_registerProblem(VM_RegiserOtherThingsActivity.this);
                        checkDialog.callFunction(11);
                    }else{
                        try {
                            setBitmapFromUri(uri);
                            createData(uri);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }


  //          }
//            else {
//                Uri photo_problem = data.getData();// data.getData() 를 통해 갤러리에서 선택한 이미지의 Uri 를 받아 옴
//                Cursor cursor = null;
//
//                //**  cursor 를 통해 스키마를 content:// 에서 file:// 로 변경 -> 사진이 저장된 절대경로를 받아오는 과정
//                try {
//                    String[] proj = {MediaStore.Images.Media.DATA};
//                    assert photo_problem != null;
//                    cursor = getContentResolver().query(photo_problem, proj, null, null, null);
//                    assert cursor != null;
//                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    cursor.moveToFirst();
//                    galleryFile = new File(cursor.getString(column_index));
//                    createData(photo_problem);
//                    setImageByUri(Uri.parse(galleryFile.getAbsolutePath()));
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//            }

        }
        else if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
            Uri photoUri;
            Log.d(TAG, "[RegiOther/PICK_FROM_CAMERA]: getAbsolutePath "
                    + Uri.parse(takeFile.getAbsolutePath()));
            Log.d(TAG, "[RegiOther/PICK_FROM_CAMERA]:  Uri.fromFile(takeFile) "
                    + Uri.fromFile(takeFile));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);

            } else {
                photoUri = Uri.fromFile(takeFile);
            }
            createData(photoUri);//provider 가 씌워진 파일을 DB에 저장함
            setImageByUri( Uri.parse(takeFile.getAbsolutePath()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFile();
            }
        }
        else if (requestCode == VM_ENUM.RC_REGIOTHER_TO_PHOTOVIEW) {
            boolean isDeleteEvent=data.getBooleanExtra(VM_ENUM.IT_DELETE_PHOTO_INDEX,false);
            if(isDeleteEvent){
                //** 삭제 진행
                Log.d(VM_ENUM.TAG, "[VM_RegiOtherActivity] deleteIndex" + picViewIndex);
                deletePhoto(picViewIndex);
            }

            boolean isChangePhotoFromGallery=data.getBooleanExtra(VM_ENUM.IT_CHANGE_PHOTO_GET_FROM_GALLERY,false);
            if(isChangePhotoFromGallery){
                //** 위젯에 있는 사진 변경
                Log.d(VM_ENUM.TAG, "[VM_RegiOtherActivity] changePhoto-> getAlbumFile()" + picViewIndex);
                getAlbumFile();
            }

            boolean isChangePhotoFromTakePhoto=data.getBooleanExtra(VM_ENUM.IT_CHANGE_PHOTO_GET_FROM_TAKE,false);
            if(isChangePhotoFromTakePhoto){
                //** 위젯에 있는 사진 변경
                Log.d(VM_ENUM.TAG, "[VM_RegiOtherActivity] changePhoto -> takePhoto()" + picViewIndex);
                takePhoto();
            }

        }

    }

}
