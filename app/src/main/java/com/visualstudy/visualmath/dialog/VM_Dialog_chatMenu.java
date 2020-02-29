package com.visualstudy.visualmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.visualstudy.visualmath.R;

public class VM_Dialog_chatMenu implements View.OnClickListener {

    private Context context;
    private VM_DialogLIstener_chatMenu digListener;
    Dialog dig;
    private int returnResult;//선택한 버튼 번호 반환

    public VM_Dialog_chatMenu(Context context){
        returnResult=-1;
        this.context=context;
    }

    public void setDialogListener(VM_DialogLIstener_chatMenu digListener){
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
        dig.setContentView(R.layout.layout_dialog_chat);
        dig.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT
        ));
        dig.show();

        final Button btn_camera = dig.findViewById(R.id.dig_btnCamera);
        final Button btn_gallery = dig.findViewById(R.id.dig_btnGallery);
        final Button btn_live = dig.findViewById(R.id.dig_btnLive);
        final Button btn_voice = dig.findViewById(R.id.dig_btnVoice);
        final Button btn_complete = dig.findViewById(R.id.dig_btnComplete);

        btn_camera.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
        btn_live.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
        btn_complete.setOnClickListener(this);
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
                digListener.onButtonLive();
                dig.dismiss();
                break;
            case R.id.dig_btnVoice:
                returnResult=3;
                digListener.onButtonVoice();
                dig.dismiss();
                break;
            case R.id.dig_btnComplete:
                returnResult=4;
                digListener.onButtonComplete();
                dig.dismiss();
                break;
        }
    }
}
