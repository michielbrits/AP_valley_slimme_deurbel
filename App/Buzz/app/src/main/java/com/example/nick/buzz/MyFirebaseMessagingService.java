package com.example.nick.buzz;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
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
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Message Notification Body: " +  remoteMessage.getData().toString());
            String title = remoteMessage.getData().get("title");
            String content = remoteMessage.getData().get("body");

            Random rn = new Random();
            int id = rn.nextInt();
            PowerManager pm = (PowerManager)MyFirebaseMessagingService.this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            Log.e("screen on....", ""+isScreenOn);
            if(isScreenOn==false)
            {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

                wl_cpu.acquire(10000);
            }
            Intent intent = new Intent(MyFirebaseMessagingService.this, LoginActivity.class);
            NotificationUtils.notificatePush(MyFirebaseMessagingService.this, id, "Ticker", title,content , intent);
        }
    }
}
