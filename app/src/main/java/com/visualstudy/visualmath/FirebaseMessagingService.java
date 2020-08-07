package com.visualstudy.visualmath;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService  extends com.google.firebase.messaging.FirebaseMessagingService
{

    /**
     * 푸시 알림 정보를 받아서 메소드로 넘긴다.
     *
     * @param remoteMessage
     */

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);

    }
    /**
     * 푸시 알림 정보를 받아서 앱에서 알림을 띄워준다.
     *
     * @param body
     */
    private void sendNotification(String title, String body)
    {
        Intent intent = new Intent(this, TempActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String chId = "test";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 알림 왔을때 사운드.

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, chId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* 안드로이드 오레오 버전 대응 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String chName = "ch name";
            NotificationChannel channel = new NotificationChannel(chId, chName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, notiBuilder.build());

    }

}
