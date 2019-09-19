package com.example.adopy.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.adopy.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String TAG = "MyFirebaseMessaging";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            Intent intent = new Intent("massage_received");
            intent.putExtra("massage",remoteMessage.getData().get("massage"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this);
            if(Build.VERSION.SDK_INT>=26) {
                NotificationChannel channel = new NotificationChannel("id_1", "name_1", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);

                builder.setChannelId("id_1");

            }
            builder.setContentTitle("New massage from Adopy!");
            builder.setSmallIcon(R.drawable.adopy_logo);
            builder.setContentText(remoteMessage.getData().get("massage"));
            notificationManager.notify(1,builder.build());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
