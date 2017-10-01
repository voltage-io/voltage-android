package io.voltage.app.application;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;

import io.voltage.app.helpers.AccountHelper;

@SuppressLint("CommitPrefEdits")
public class VoltagePreferences {

    public interface Property {
        String REG_ID = "pref_key_reg_id";
        String USER_NAME = "pref_key_user_name";
        String APP_VERSION = "pref_key_app_version";
        String PRIMARY_COLOUR = "primary_colour";
        String SECONDARY_COLOUR = "secondary_colour";
        String SEND_READ_RECEIPTS = "pref_key_send_read_receipts";
        String AUTO_PLAY_GIFS = "pref_key_auto_play_gifs";
        String AUTO_ADD_USERS = "pref_key_auto_add_users";
        String PUBLISH_REG_ID = "pref_key_publish_reg_id";
        String USE_DARK_THEME = "pref_key_use_dark_theme";
    }

    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getUserName(final Context context) {
        final AccountHelper helper = new AccountHelper.Default();
        final Account[] accounts = helper.getAccounts(context);
        return accounts.length > 0 ? accounts[0].name : "";
    }

    public static String getRegId(final Context context) {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public static String getPrimaryColour(final Context context) {
        return getSharedPreferences(context).getString(Property.PRIMARY_COLOUR, "#FFA05C");
    }

    public static String getSecondaryColour(final Context context) {
        final String color = isLightTheme(context) ? "#55000000" : "#FFFFFF";
        return getSharedPreferences(context).getString(Property.SECONDARY_COLOUR, color);
    }

    public static boolean shouldSendReadReceipt(final Context context) {
        return getSharedPreferences(context).getBoolean(Property.SEND_READ_RECEIPTS, false);
    }

    public static boolean shouldAutoPlayGifs(final Context context) {
        return getSharedPreferences(context).getBoolean(Property.AUTO_PLAY_GIFS, false);
    }

    public static boolean shouldAutoAddUsers(final Context context) {
        return getSharedPreferences(context).getBoolean(Property.AUTO_ADD_USERS, false);
    }

    public static boolean shouldPublishRegId(final Context context) {
        return getSharedPreferences(context).getBoolean(Property.PUBLISH_REG_ID, true);
    }

    public static boolean isLightTheme(final Context context) {
        return getSharedPreferences(context).getBoolean(Property.USE_DARK_THEME, false);
    }

    public static void setPrimaryColour(final Context context, final String colour) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Property.PRIMARY_COLOUR, colour);
        editor.commit();
    }
}
