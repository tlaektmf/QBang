package com.visualstudy.visualmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.visualstudy.visualmath.R;

public class VM_Dialog_match  implements View.OnClickListener{
    private Context context;
    private VM_DialogListener_matchComplete dialogListener;
    Dialog dig;
    private int returnResult;//네&아니오

    public VM_Dialog_match(Context context){
        returnResult=-1;
        this.context=context;
    }

    public void setDialogListener(VM_DialogListener_matchComplete dialogListener){
        this.dialogListener = dialogListener;
    }

    public int getReturnResult(){
        return returnResult;
    }
    public void setReturnResult(){
        this.returnResult=returnResult;
    }

    public void callFunction(){
        dig = new Dialog(context);
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dig.setContentView(R.layout.layout_dialog_match);
        dig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dig.show();

        final Button yesBtn = dig.findViewById(R.id.solve_yes_btn);
        final Button noBtn = dig.findViewById(R.id.solve_no_btn);

        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.solve_yes_btn:
                returnResult=0;
                dialogListener.onButtonYes();
                dig.dismiss();
                break;
            case R.id.solve_no_btn:
                returnResult=1;
                dialogListener.onButtonNo();
                dig.dismiss();
                break;
        }
    }

}
