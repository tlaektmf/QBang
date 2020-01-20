package com.example.visualmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.visualmath.R;

public class VM_Dialog_chatMenu_teacher implements View.OnClickListener {

    private Context context;
    private VM_DialogLIstener_chatMenu_teacher digListener;
    Dialog dig;
    private int returnResult;//선택한 버튼 번호 반환

    public VM_Dialog_chatMenu_teacher(Context context){
        returnResult=-1;
        this.context=context;
    }

    public void setDialogListener(VM_DialogLIstener_chatMenu_teacher digListener){
        this.digListener=digListener;
    }

    public int getReturnResult(){
        return returnResult;
    }

    public void setReturnResult(int returnResult){
        this.returnResult=returnResult;
    }

    public void callFunction(){
        dig = new Dialog(context);
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dig.setContentView(R.layout.layout_dialog_chat_teacher);
        dig.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT
        ));
        dig.show();

        final Button btn_camera = dig.findViewById(R.id.dig_btnCamera_teacher);
        final Button btn_gallery = dig.findViewById(R.id.dig_btnGallery_teacher);
        final Button btn_settime = dig.findViewById(R.id.dig_btnSettime_teacher);
        final Button btn_voice = dig.findViewById(R.id.dig_btnVoice_teacher);

        btn_camera.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
        btn_settime.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dig_btnCamera:
                returnResult=0;
                digListener.onButtonCamera();
                dig.dismiss();
                break;
            case R.id.dig_btnGallery:
                returnResult=1;
                digListener.onButtonGallery();
                dig.dismiss();
                break;
            case R.id.dig_btnLive:
                returnResult=2;
                digListener.onButtonSetTime();
                dig.dismiss();
                break;
            case R.id.dig_btnVoice:
                returnResult=3;
                digListener.onButtonVoice();
                dig.dismiss();
                break;
        }
    }
}
