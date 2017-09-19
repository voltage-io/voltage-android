package io.voltage.app.helpers;

import android.content.Context;
import android.text.TextUtils;

import java.util.Random;

import io.voltage.app.models.Participants;

public interface FormatHelper {

    String getUserName(final String name);
    String getThreadName(final String threadName, final String userNames);
    String getThreadName(final Context context, final String threadId);
    int getNotificationId(final String idString);


    class Default implements FormatHelper {
        private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

        public String getUserName(final String name) {
            return !TextUtils.isEmpty(name) ? name : "UNKNOWN";
        }

        public String getThreadName(final String threadName, final String userNames) {
            return !TextUtils.isEmpty(threadName) ? threadName : getUserName(userNames);
        }

        public String getThreadName(final Context context, final String threadId) {
            final Participants participants = mDatabaseHelper.getParticipants(context, threadId);

            if (participants != null) {
                return getThreadName(participants.getThreadName(), participants.getUserNames());
            }

            return "UNKNOWN";
        }

        public int getNotificationId(final String idString) {
            if (idString != null) {
                final String stripped = idString.replaceAll("[^0-9.]", "");
                if (stripped.length() > 7) {
                    return Integer.parseInt(stripped.substring(0, 7));

                } else if (stripped.length() > 0) {
                    return Integer.parseInt(stripped);
                }
            }

            return new Random().nextInt();
        }
    }
}
