package com.example.nick.buzz;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import static com.example.nick.buzz.NotificationUtils.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " +  remoteMessage.getNotification().toString() + " " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String content = remoteMessage.getNotification().getBody();

            Random rn = new Random();
            int id = rn.nextInt();
            Intent intent = new Intent(MyFirebaseMessagingService.this, LoginActivity.class);
            NotificationUtils.notificatePush(MyFirebaseMessagingService.this, id, "Ticker", title,content , intent);
        }
    }
}
