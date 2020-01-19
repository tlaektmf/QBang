package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
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

public class VM_RegisterProblemActivity extends AppCompatActivity {

    private static final String TAG = VM_ENUM.TAG;
    private int returnResult;

    private File galleryFile; //갤러리로부터 받아온 이미지를 저장
    private File takeFile;

    private final CharSequence[] gradeItems = {"초       등", "중       등", "고       등"};

    private EditText editTextTitle;
    private Button buttonGrade;
    private Button buttonGoOther;
    private ImageView imageViewProblem;

    //** 데이터
    private VM_Data_ADD receiveData; //<내용추가뷰> -> <문제등록뷰> : "문제등록"버튼 클릭시, 현 액티비티에서 DB를 저장하기 위함
    private VM_Data_ADD sendData;// <문제등록뷰> -> <내용추가뷰> : 바로 이전에 온 <내용추가뷰>에서의 기록을 보관하기 위함

    //** DB에 저장될 데이터
    private VM_Data_BASIC vmDataBasic;

    //** 풀이법 선택 변수 받기
    Intent intent;
    String solveWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__register_problem);

        //** ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Log.d(TAG, "VM_RegisterProblemActivity OnCreate 호출");
        init();


    }

    public void init() {
        buttonGrade = findViewById(R.id.btn_grade);
        imageViewProblem = findViewById(R.id.iv_file_problem);
        buttonGoOther = findViewById(R.id.btn_goOther);
        editTextTitle = findViewById(R.id.et_title);
        returnResult = VM_ENUM.NOTHING;
        vmDataBasic = new VM_Data_BASIC();
        intent = getIntent();
        solveWay = intent.getStringExtra(VM_ENUM.SOLVE_WAY);
        Log.d(TAG, "[문제등록뷰로 넘어온 Intent 확인]" + solveWay);
    }


    //** 사용자 갤러리에 접근 or 카메라에 접근
    public void changeProblemFile(View view) {
        //** 권한 설정
        tedPermission();

    }


    public void getAlbumFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, VM_ENUM.PICK_FROM_ALBUM); //앨범 화면으로 이동
        // -> 사진촬영과는 다르게, 경로를 넘겨줄 필요없으나 선택한 파일을 반환하므로 onActivity result에서 데이터를 받아야됨
        /*
        startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작함.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 { PICK_FROM_ALBUM }이 requestCode 로 반환됨
         */


    }




    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, VM_ENUM.RC_READ_REQUEST_CODE);
    }

    /**
     * 내장드라이브에서 미디어 스토어 주소를 찾고, 이를 통해 실제 경로를 가져온다 (disply name을 가져오기 위한 method)
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent를 통해 카메라 화면으로 이동함

        try {
            takeFile = createImageFile(); //파일 경로가 담긴 빈 이미지 생성(아직 content provider이 입혀져 있지 않음)
            Log.d(VM_ENUM.TAG, "[VM_RegisterProblem]+content provider 전 getAbsolutePath" + takeFile.getAbsolutePath());
            Log.d(VM_ENUM.TAG, "[VM_RegisterProblem]+content provider 전 fromFile" + Uri.fromFile(takeFile));

        } catch (IOException e) {
            Toast.makeText(this, "처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }


        if (takeFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);

                Log.d(VM_ENUM.TAG, "[VM_RegisterProblem]+content provider 후" + photoUri);

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

    public void changeGrade(View view) {
        //**초등, 중등, 고등 선택

        //**초,중,고 선택 _ 커스텀 다이얼로그
        final VM_Dialog_PickGrade gradeDialog = new VM_Dialog_PickGrade(VM_RegisterProblemActivity.this);

        gradeDialog.setDialogListener(new VM_DialogLIstener_PickGrade() {
            @Override
            public void onButtonPrimary() {
                returnResult = 0;
                buttonGrade.setText(gradeItems[returnResult]);
                String grade = gradeItems[returnResult].toString();
                vmDataBasic.setGrade(grade);
            }

            @Override
            public void onButtonMiddle() {
                returnResult = 1;
                buttonGrade.setText(gradeItems[returnResult]);
                String grade = gradeItems[returnResult].toString();
                vmDataBasic.setGrade(grade);
            }

            @Override
            public void onButtonHigh() {
                returnResult = 2;
                buttonGrade.setText(gradeItems[returnResult]);
                String grade = gradeItems[returnResult].toString();
                vmDataBasic.setGrade(grade);
            }
        });

        gradeDialog.callFunction();
    }

    public void addOther(View view) {
        Intent intent;
        intent = new Intent(VM_RegisterProblemActivity.this, VM_RegiserOtherThingsActivity.class);
        sendData = receiveData;//<문제등록뷰>에서 <내용추가뷰>로 보내는 데이터는 바로 이전에 <내용추가뷰>로부터 받은 데이터
        intent.putExtra(VM_ENUM.ALL, sendData); //Parcel객체인 sendData intent에 추가
        startActivityForResult(intent, VM_ENUM.OTHER_DATA_LOAD);
    }


//    private void setImage() {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap originalBm = BitmapFactory.decodeFile(galleryFile.getAbsolutePath(), options);// galleryFile의 경로를 불러와 bitmap 파일로 변경
//        imageViewProblem.setImageBitmap(originalBm); //이미지 set
//        if (originalBm == null) {
//            Log.d(TAG, "[VM_RegisterProblemActivity]: set할 이미지가 없음");
//        }
//    }

    private void setImageByUri(Uri _uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(_uri.toString(), options);// galleryFile or takeFile의 경로를 불러와 bitmap 파일로 변경
        if (originalBm != null) {
            imageViewProblem.setImageBitmap(originalBm); //이미지 set
        }

    }

    private File createImageFile() throws IOException {//** 파일 경로를 이미 생성(시간으로 중복문제 해결)
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    private void showPickDialog() {
        //** 다이얼로그 실행
        // 커스텀 다이얼로그를 생성
        final VM_Dialog_PickHowToGetPicture dialog = new VM_Dialog_PickHowToGetPicture(VM_RegisterProblemActivity.this);

        // 커스텀 다이얼로그의 결과를 담을 매개변수로 같이 넘겨준다.

        dialog.setDialogListener(new VM_DialogListener_PickHowToGetPicture() {
            @Override
            public void onButtonTakePhotoClicked() {
                returnResult = VM_ENUM.CAMERA;
                dialog.setReturnResult(VM_ENUM.CAMERA);
                takePhoto();
            }

            @Override
            public void onButtonGetAlbumFileClicked() {
                returnResult = VM_ENUM.GALLERY;
                dialog.setReturnResult(VM_ENUM.GALLERY);
                getAlbumFile();//내부 저장소 접근
                ///performFileSearch();//내,외부 저장소 모두 접근 가능

            }
        });
        dialog.callFunction();// 커스텀 다이얼로그를 호출

    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //** 권한 요청 성공
                Toast.makeText(VM_RegisterProblemActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                showPickDialog();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ** 권한 요청 실패
                Toast.makeText(VM_RegisterProblemActivity.this, deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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

        //** 데이터베이스에 저장


        //** 문제 등록의 최소 요건 확인
        if (checkAbility()) {
            Log.d(TAG, "[VM_RegisterProblemActivity]: 문제 등록 요구사항 만족");
            //** 데이서 생성 VM_Data_ADD, VM_Data_Basic
            vmDataBasic.setTitle(editTextTitle.getText().toString());

            //** <내용추가>뷰에서 받아온 값들 (content provider가 벗겨져 있기 때문에, 이를 씌워서 DB에 올린다
            //** Firebase 정책
            wrapContentProvider();

            //** 데이터베이스 생성 및 저장 && storage에 파일 업로드
            VM_DBHandler vmDbHandler = new VM_DBHandler();
            vmDbHandler.newPost(receiveData, vmDataBasic, solveWay);//** DB에는 (바로 이전에 <내용추가뷰>에서 받은 VM_Data_ADD , VM_Data_Basic)
            //** 액티비티 종료
            finish();
        } else {
            Log.d(TAG, "[VM_RegisterProblemActivity]: 문제 등록 요구사항 만족 못함");
        }


    }

    public void wrapContentProvider() {
        Log.d(TAG, "[VM_RegisterProblemActivity]: 내용추가 버튼에서 넘어온 값들 검사");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            if (receiveData != null) {
                for (int i = 0; i < 3; i++) {
                    if (receiveData.getFilePathElement(i) != null) {
                        Uri photoUri = FileProvider.getUriForFile(this,
                                "com.example.visualmath.provider", new File(receiveData.getFilePathElement(i).toString()));
                        receiveData.setFilePathElement(photoUri, i);
                    }
                }
            }

        } else { //** content provider 불필요함


        }
    }

    public void cancel(View view) {
        finish();
    }

    /***
     * 문제 등록 최소 요구조건 확인
     * - 문제 제목
     * - 학년
     * - 문제 사진
     *
     * @return
     */
    public boolean checkAbility() {

        if (editTextTitle.getText() == null ||
                editTextTitle.getText().toString().replace(" ", "").equals("")) {
            //** title란이 아예 쓰여진게 없거나 공백들로 이루어진 경우
//            AlertDialog.Builder alert = new AlertDialog.Builder(this);
//            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();     //닫기
//                }
//            });
//            alert.setMessage("문제 제목은 필수 입력사항입니다.");
//            alert.show();


            Toast toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_register_check_title,null));
            toast.show();
            return false;
        }
        if (vmDataBasic.getProblem() == null) {
            //** 문제 사진란이 공백
//            AlertDialog.Builder alert = new AlertDialog.Builder(this);
//            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();     //닫기
//                }
//            });
//            alert.setMessage("문제 사진 첨부는 필수 사항입니다.");
//            alert.show();
//            return false;

            Toast toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_register_check_picture,null));
            toast.show();
            return false;
        }
        if (vmDataBasic.getGrade() == null) {
            //** 학년 선택을 하지 않은 경우
//            AlertDialog.Builder alert = new AlertDialog.Builder(this);
//            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();     //닫기
//                }
//            });
//            alert.setMessage("학년 선택은 필수 사항입니다.");
//            alert.show();
//            return false;

            Toast toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_register_check_grade,null));
            toast.show();
            return false;
        }
        return true;
    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        if (image != null) {
            imageViewProblem.setImageBitmap(image); //이미지 set
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

        Log.d(VM_ENUM.TAG, "[VM_RegisterProblem] " + requestCode + " ," + resultCode);
        //** 예외 사항 처리
        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "선택이 취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
                if (takeFile != null) {//->이때는 무조건 "사진 촬영" 하려다가 취소한 경우에 해당함
                    if (takeFile.exists()) {
                        if (takeFile.delete()) {
                            Log.d(TAG, takeFile.getAbsolutePath() + " 삭제 성공");
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
                        getBitmapFromUri(uri);
                        vmDataBasic.setProblem(uri);//provider 가 씌워진 파일을 DB에 저장함
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                assert data != null;
                Uri photo_problem = data.getData();// data.getData() 를 통해 갤러리에서 선택한 이미지의 Uri 를 받아 옴

                Log.d(TAG, "[VM_RegisterProblemActivity/PICK_FROM_ALBUM]: 커서 전 photo_problem " + photo_problem);

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

                Log.d(TAG, "[VM_RegisterProblemActivity/PICK_FROM_ALBUM]: 원본 photo_problem " + photo_problem);
                Log.d(TAG, "[VM_RegisterProblemActivity/PICK_FROM_ALBUM]: 커서 후 photo_problem "
                        + Uri.parse(galleryFile.getAbsolutePath()));

                vmDataBasic.setProblem(photo_problem);//provider 가 씌워진 파일을 DB에 저장함
                setImageByUri(Uri.parse(galleryFile.getAbsolutePath()));

                //데이터 등록
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                    try {
//                        getGalleryImages();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    setImageByUri(Uri.parse(galleryFile.getAbsolutePath()));
//                }

            }


        } else if (requestCode == VM_ENUM.PICK_FROM_CAMERA) {
            Uri photoUri;

            Log.d(TAG, "[VM_RegisterProblemActivity/PICK_FROM_CAMERA]: getAbsolutePath "
                    + Uri.parse(takeFile.getAbsolutePath()));
            Log.d(TAG, "[VM_RegisterProblemActivity/PICK_FROM_CAMERA]:  Uri.fromFile(takeFile) "
                    + Uri.fromFile(takeFile));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.visualmath.provider", takeFile);

                Log.d(TAG, "[VM_RegisterProblemActivity/PICK_FROM_CAMERA]:content provider photoUri " + photoUri);

            } else {
                photoUri = Uri.fromFile(takeFile);
            }

            vmDataBasic.setProblem(photoUri);//provider 가 씌워진 파일을 DB에 저장함
            setImageByUri(Uri.parse(takeFile.getAbsolutePath()));//이미지 set의 경우는 provider가 벗겨진 파일을 set

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFile();
            }

        } else if (requestCode == VM_ENUM.OTHER_DATA_LOAD) {
            Log.d(TAG, "[VM_RegisterProblemActivity]: <내용추가뷰> -> <문제등록뷰>로 이동됨");

            if (resultCode == RESULT_OK) {
                String notice = "";

                //** <내용추가뷰>로부터 받은 객체를 <문제등록뷰>에서 관리하기 위해
                //receiveDatat 변수에 저장함
                assert data != null;
                receiveData = data.getParcelableExtra(VM_ENUM.ALL);

                assert receiveData != null;
                if (receiveData.getDetail() == null) {
                    Log.d(TAG, "[VM_RegisterProblemActivity]: receiveData.getDetail()==null");
                }

                if (!receiveData.getDetail().equals("")) {
                    notice = "추가 설명 등록.";

                }
                int count = 0;

                for (int i = 0; i < 3; i++) {
                    if (receiveData.getFilePathElement(i) != null) {
                        count++;
                        Log.d(TAG, "[VM_RegisterProblemActivity] receiveDatacheck: " + i + ",," + receiveData.getFilePathElement(i).toString());
                    }
                }
                if (count != 0) {
                    notice += "사진 " + count + "개 추가.";
                }

                //** 버튼에 정보 표시
                if (!notice.equals("")) {
                    buttonGoOther.setText(notice);
                } else if (notice.equals("")) {
                    buttonGoOther.setText("본인 풀이 또는 질문 내용 추가");
                }

            }
        }


    }


}
