package io.voltage.app.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import io.voltage.app.R;
import io.voltage.app.activities.ConversationActivity;
import io.voltage.app.activities.UserEditActivity;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.Message;
import io.voltage.app.models.Thread;
import io.voltage.app.models.User;

public class NotificationHelper {

    private static final long[] PATTERN = new long[]{100, 100, 100, 250, 100};

    private interface Messages {
        String FRIEND_ADDED = "Friend Added";
        String MESSAGE_NOT_SENT = "Message Not Sent: ";
    }

    private final FormatHelper mFormatHelper = new FormatHelper();

    public void addNewMessageNotification(final Context context, final Message message) {
        if (message != null && shouldNotify(context, message.getThreadId())) {
            final String threadId = message.getThreadId();
            final String threadName = mFormatHelper.getThreadName(context, threadId);
            final int notificationId = mFormatHelper.getNotificationId(threadId);

            final PendingIntent intent = createConversationPendingIntent(context, threadId, notificationId);

            addNotification(context, intent, threadName, message.getText(), notificationId);
        }
    }

    public void addMessageNotSentNotification(final Context context, final Message message) {
        if (message != null && shouldNotify(context, message.getThreadId())) {
            final String threadId = message.getThreadId();
            final String threadName = mFormatHelper.getThreadName(context, threadId);
            final int notificationId = mFormatHelper.getNotificationId(threadId);

            final PendingIntent intent = createConversationPendingIntent(context, threadId, notificationId);

            addNotification(context, intent, threadName, Messages.MESSAGE_NOT_SENT + message.getText(), notificationId);
        }
    }

    public void addNewFriendNotification(final Context context, final User user) {
        if (user != null) {
            final String name = mFormatHelper.getUserName(user.getName());
            final int notificationId = mFormatHelper.getNotificationId(user.getRegId());

            final PendingIntent intent = createUserEditPendingIntent(context, user.getRegId(), notificationId);

            addNotification(context, intent, Messages.FRIEND_ADDED, name, notificationId);
        }
    }

    private boolean shouldNotify(final Context context, final String threadId) {
        final Thread thread = new DatabaseHelper().getThread(context, threadId);
        return thread.getState() != ThreadTable.State.MUTED;
    }

    private void addNotification(final Context context, final PendingIntent intent, final String title, final String text, final int notificationId) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(PATTERN, -1);
        }

        final Notification notification = createNotification(context, intent, title, text);
        notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, notification);
    }

    private PendingIntent createConversationPendingIntent(final Context context, final String threadId, final int notificationId) {
        final TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addParentStack(ConversationActivity.class);
        builder.addNextIntent(ConversationActivity.newIntent(context, threadId));
        return builder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createUserEditPendingIntent(final Context context, final String regId, final int notificationId) {
        final TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addParentStack(UserEditActivity.class);
        builder.addNextIntent(UserEditActivity.newIntent(context, regId));
        return builder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Notification createNotification(final Context context, final PendingIntent intent, final String title, final String text) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        builder.setLights(Color.parseColor(VoltagePreferences.getPrimaryColour(context)), 500, 1000);
        builder.setSmallIcon(R.drawable.ic_voltage_logo);
        builder.setContentIntent(intent);
        builder.setContentTitle(title);
        builder.setContentText(text);
        return builder.build();
    }
}
