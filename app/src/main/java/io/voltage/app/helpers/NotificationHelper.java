package io.voltage.app.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import io.voltage.app.R;
import io.voltage.app.activities.ConversationActivity;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.Thread;

public interface NotificationHelper {

    void addNotification(final Context context, final String threadId, final String text);
    void cancelNotification(final Context context, final String threadId);

    class Default implements NotificationHelper {

        private static final String CHANNEL_ID = "Voltage";
        private static final String CHANNEL_NAME = "Messages";
        private static final long[] PATTERN = new long[]{100, 100, 100, 250, 100};

        private final FormatHelper mFormatHelper = new FormatHelper.Default();
        private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

        public void addNotification(final Context context, final String threadId, final String text) {
            if (shouldNotify(context, threadId)) {
                final String threadName = mFormatHelper.getThreadName(context, threadId);
                final int notificationId = mFormatHelper.getNotificationId(threadId);

                final PendingIntent intent = createPendingIntent(context, threadId, notificationId);

                addNotification(context, intent, threadName, text, notificationId);
            }
        }

        public void cancelNotification(final Context context, final String threadId) {
            final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(mFormatHelper.getNotificationId(threadId));
        }

        private boolean shouldNotify(final Context context, final String threadId) {
            final Thread thread = mDatabaseHelper.getThread(context, threadId);
            return thread == null || thread.getState() != ThreadTable.State.MUTED;
        }

        private void addNotification(final Context context, final PendingIntent intent, final String title, final String text, final int notificationId) {
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(PATTERN, -1);
            }

            final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context, manager);
            }

            manager.notify(notificationId, createNotification(context, intent, title, text));
        }

        @TargetApi(Build.VERSION_CODES.O)
        private void createNotificationChannel(final Context context, final NotificationManager manager) {
            final NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                manager.createNotificationChannel(createChannel(context));
            }
        }

        @TargetApi(Build.VERSION_CODES.O)
        private NotificationChannel createChannel(final Context context) {
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.LTGRAY);
            channel.enableLights(true);
            return channel;
        }

        private PendingIntent createPendingIntent(final Context context, final String threadId, final int notificationId) {
            final TaskStackBuilder builder = TaskStackBuilder.create(context);
            builder.addParentStack(ConversationActivity.class);
            builder.addNextIntent(ConversationActivity.newIntent(context, threadId));
            return builder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        private Notification createNotification(final Context context, final PendingIntent intent, final String title, final String text) {
            final Notification notification = buildNotification(context, intent, title, text);
            notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            return notification;
        }

        private Notification buildNotification(final Context context, final PendingIntent intent, final String title, final String text) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
            builder.setLights(Color.parseColor(VoltagePreferences.getPrimaryColour(context)), 500, 1000);
            builder.setSmallIcon(R.drawable.ic_voltage_logo);
            builder.setContentIntent(intent);
            builder.setContentTitle(title);
            builder.setContentText(text);
            return builder.build();
        }
    }
}
