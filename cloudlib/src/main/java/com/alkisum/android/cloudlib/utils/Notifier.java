package com.alkisum.android.cloudlib.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.Random;

/**
 * Class used by cloud operators to send notifications.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.2
 */
public class Notifier {

    /**
     * Notification manager.
     */
    private final NotificationManager notificationManager;

    /**
     * Notification builder.
     */
    private final NotificationCompat.Builder builder;

    /**
     * Notification id.
     */
    private final int id;

    /**
     * Notifier constructor.
     *
     * @param context     Context
     * @param channelId   Channel id
     * @param channelName Channel name
     */
    public Notifier(final Context context, final String channelId,
                    final String channelName) {
        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(
                        notificationChannel);
            }
        }
        builder = new NotificationCompat.Builder(context, channelId);
        id = new Random().nextInt(100);
    }

    /**
     * Show notification.
     */
    public void show() {
        notificationManager.notify(id, builder.build());
    }

    /**
     * Set Intent to notification.
     *
     * @param context Context
     * @param intent  Intent for notification
     */
    public void setIntent(final Context context, final Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
    }

    /**
     * Set icon to notification.
     *
     * @param icon Icon
     */
    public void setIcon(final int icon) {
        builder.setSmallIcon(icon);
    }

    /**
     * Set progress to notification.
     *
     * @param progress Progress in percentage
     */
    public void setProgress(final int progress) {
        boolean indeterminate = false;
        if (progress == -1) {
            indeterminate = true;
        }
        builder.setProgress(100, progress, indeterminate);
    }

    /**
     * Set title to notification.
     *
     * @param title Title
     */
    public void setTitle(final String title) {
        builder.setContentTitle(title);
    }

    /**
     * Set text to notification.
     *
     * @param text Text
     */
    public void setText(final String text) {
        builder.setContentText(text);
    }

    /**
     * Set auto-cancel to notification.
     *
     * @param autoCancel true to enable auto-cancel, false otherwise
     */
    public void setAutoCancel(final boolean autoCancel) {
        builder.setAutoCancel(autoCancel);
    }
}
