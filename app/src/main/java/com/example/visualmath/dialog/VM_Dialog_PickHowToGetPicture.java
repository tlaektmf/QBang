package com.example.visualmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.visualmath.R;

public class VM_Dialog_PickHowToGetPicture extends Dialog implements View.OnClickListener {
    private Context context;
    private VM_DialogListener_PickHowToGetPicture dialogListener;
    Dialog dlg;
    private int returnResult;
    private static final String TAG="Dialog";
//    private static final int GALLERY=1;
//    private static final int CAMERA=2;

    public VM_Dialog_PickHowToGetPicture(Context context) {
        super(context);
        returnResult=-1;
        this.context = context;
    }

    public void setDialogListener(VM_DialogListener_PickHowToGetPicture dialogListener){
        this.dialogListener = dialogListener;
    }

    public int getReturnResult() {
        return returnResult;
    }

    public void setReturnResult(int returnResult) {
        this.returnResult = returnResult;
    }

    //** 호출할 다이얼로그 함수 정의
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바 숨김
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정
        dlg.setContentView(R.layout.layout_dialog_pick_how_to_get_picture);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // 커스텀 다이얼로그를 노출
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의
        final ImageButton buttonTakePhoto = (ImageButton) dlg.findViewById(R.id.takePhoto);
        final ImageButton buttonGetAlbumFile = (ImageButton) dlg.findViewById(R.id.getAlbumFile);
        buttonGetAlbumFile.setOnClickListener(this);
        buttonTakePhoto.setOnClickListener(this);

//        //** 'buttonTakePhoto' 버튼 클릭시
//        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogListener.onButtonTakePhotoClicked(); // 인터페이스의 메소드를 호출
//                //returnResult=CAMERA;
//
//                // 커스텀 다이얼로그를 종료
//                dlg.dismiss();
//            }
//        });
//
//        //** 'buttonGetAlbumFile' 버튼 클릭시
//        buttonGetAlbumFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogListener.onButtonGetAlbumFileClicked(); // 인터페이스의 메소드를 호출
//                //returnResult=GALLERY;
//
//
//                // 커스텀 다이얼로그를 종료.
//                dlg.dismiss();
//            }
//        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takePhoto:
                Log.i(TAG,"들어옴");
                dialogListener.onButtonTakePhotoClicked(); // 인터페이스의 메소드를 호출
                dlg.dismiss();
                break;
            case R.id.getAlbumFile:
                Log.i(TAG,"들어옴");
                dialogListener.onButtonGetAlbumFileClicked();
                dlg.dismiss();
                break;
        }
    }
}
