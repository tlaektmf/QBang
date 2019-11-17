package com.example.visualmath;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class VM_Dialog_PickGrade implements View.OnClickListener {

    private Context context;
    private VM_DialogLIstener_PickGrade dialogListener;
    Dialog dig;
    private int returnResult;//학년 String 반환

    public VM_Dialog_PickGrade(Context context) {
        returnResult=-1;
        this.context=context;
    }

    public void setDialogListener(VM_DialogLIstener_PickGrade dialogListener){
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
        dig.setContentView(R.layout.layout_dialog_pick_grade);
        dig.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dig.show();

        final Button btn_primary = dig.findViewById(R.id.btn_primary);
        final Button btn_middle = dig.findViewById(R.id.btn_middle);
        final Button btn_high = dig.findViewById(R.id.btn_high);

        btn_primary.setOnClickListener(this);
        btn_middle.setOnClickListener(this);
        btn_high.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_primary:
                returnResult=0;
                dialogListener.onButtonPrimary();
                dig.dismiss();
                break;
            case R.id.btn_middle:
                returnResult=1;
                dialogListener.onButtonMiddle();
                dig.dismiss();
                break;
            case R.id.btn_high:
                returnResult=2;
                dialogListener.onButtonHigh();
                dig.dismiss();
                break;
        }
    }
}
