package com.example.visualmath.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.activity.HomeActivity;
import com.example.visualmath.activity.VM_ProblemListActivity;

import java.util.Timer;
import java.util.TimerTask;

// tmpNum 11까지 함
public class VM_Dialog_registerProblem {
    private Context context;
    public static Dialog dig;

    public VM_Dialog_registerProblem(Context context){
        this.context=context;
    }

    public void callFunction(final int tmpNum, final Activity parent){
        dig = new Dialog(context);
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(tmpNum==5){
            //매치 완료 다이얼로그
            dig.setContentView(R.layout.layout_dialog_match_complete);
        }
        else if(tmpNum==6){
            //문제 풀이 완료. 질문 노트로 이동 알람
            dig.setContentView(R.layout.layout_dialog_alarm_problem_complete);
        }
        else if(tmpNum==7){
            //이미 매치 완료 된 문제 알람
            dig.setContentView(R.layout.layout_dialog_alarm_already_matched);
        } else if(tmpNum==8){
            dig.setContentView(R.layout.layout_dialog_alarm_already_complete);
        }
        else{
            Log.d("registerProblem 오류","다이얼로그 if문 조건 범위 오류");
        }

        dig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dig.show();

    }
    public void callFunction(final int tmpNum){
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
        }else if(tmpNum==9){
            dig.setContentView(R.layout.layout_dialog_success_join);
        }else if(tmpNum==10){
            //로그인 실패
            dig.setContentView(R.layout.layout_dialog_alarm_login_fail);
        }else if(tmpNum==11){
            //비디오 불가
            dig.setContentView(R.layout.layout_dialog_alarm_no_video);
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
