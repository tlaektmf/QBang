package com.visualstudy.visualmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.visualstudy.visualmath.R;

public class VM_Dialog_Pick_Picture_Or_Video implements View.OnClickListener {

    private Context context;
    private VM_DialogLIstener_Pick_Picture_Or_Video dialogListener;
    Dialog dig;
    private int returnResult;//결과값 반환 (사진 촬영 or 동영상 촬영)

    public VM_Dialog_Pick_Picture_Or_Video(Context context) {
        returnResult=-1;
        this.context=context;
    }

    public void setDialogListener(VM_DialogLIstener_Pick_Picture_Or_Video dialogListener){
        this.dialogListener = dialogListener;
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
        dig.setContentView(R.layout.layout_dialog_pic_or_video);
        dig.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dig.show();

        final Button btn_select_pic = dig.findViewById(R.id.select_pic);
        final Button btn_select_video= dig.findViewById(R.id.select_video);

        btn_select_pic.setOnClickListener(this);
        btn_select_video.setOnClickListener(this);

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.select_pic:
                returnResult=0;
                dialogListener.onButtonPicture();
                dig.dismiss();
                break;
            case R.id.select_video:
                returnResult=1;
                dialogListener.onButtonVideo();
                dig.dismiss();
                break;
        }
    }
}
