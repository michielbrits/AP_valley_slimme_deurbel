package com.example.nick.buzz;

/**
 * Created by Nick on 1/12/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaActionSound;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Nick on 1/12/2017.
 */

public class NotificationUtils {

    public static final String TAG = "NotificationUtils";
    private static final int NOTIFICATION_DEFAULT_ON = 1000;
    private static final int NOTIFICATION_DEFAULT_OFF = 4000;
    private static final int NOTIFICATION_DEFAULT_COLOR = Color.YELLOW;

    public static void notificatePush(Context context, int notificationId, String tickerText, String contentTitle, String contentText, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.small_logo)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(tickerText)
                .setPriority(Notification.PRIORITY_MAX);


        // Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(false);

        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        // Gets an instance of the NotificationManager service
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        notifyMgr.notify(notificationId, note);
    }
}