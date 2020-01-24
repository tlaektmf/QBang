package com.example.visualmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.example.visualmath.R;

import java.util.Timer;
import java.util.TimerTask;

public class VM_Dialog_registerProblem {
    private Context context;
    Dialog dig;

    public VM_Dialog_registerProblem(Context context){
        this.context=context;
    }

    public void callFunction(int tmpNum){
        dig = new Dialog(context);
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(tmpNum==0){
            dig.setContentView(R.layout.layout_dialog_register_check_title);
        }else if(tmpNum==1){
            dig.setContentView(R.layout.layout_dialog_register_check_picture);
        }else if(tmpNum==2){
            dig.setContentView(R.layout.layout_dialog_register_check_grade);
        }
        else if(tmpNum==4){
            //이메일 인증 확인
            dig.setContentView(R.layout.layout_dialog_check_email);
        }
        else if(tmpNum==5){
            //매치 완료 다이얼로그
            dig.setContentView(R.layout.layout_dialog_match_complete);
        }
        else{
            Log.d("registerProblem 오류","다이얼로그 if문 조건 범위 오류");
        }

        dig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dig.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dig.dismiss();
                t.cancel();
            }
        }, 2000);
    }
}
