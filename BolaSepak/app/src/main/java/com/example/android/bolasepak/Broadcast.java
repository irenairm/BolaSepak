package com.example.android.bolasepak;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class Broadcast extends BroadcastReceiver {
    private String team_Name;

    @Override
    public void onReceive(Context context, Intent intent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifySubscribe")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Your favorite team is playing!")
                .setContentText("Your favorite team is playing today!")
                .setChannelId("primary_notification_channel")
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());

    }
}
