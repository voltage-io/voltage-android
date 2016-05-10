package io.voltage.app.helpers;

import android.content.Context;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.utils.Logger;

public class ShareHelper {

    private interface Params {
        String USER_NAME = "user_name";
        String REG_ID = "reg_id";
    }

    private static final String ENCODING = "UTF-8";
    private static final String SHARE_FORMAT = "http://voltage.io/add_friend?%s=%s&%s=%s";

    public String getShareUrl(final Context context) {
        try {
            final String userName = URLEncoder.encode(VoltagePreferences.getUserName(context), ENCODING);
            final String regId = URLEncoder.encode(VoltagePreferences.getRegId(context), ENCODING);
            return String.format(SHARE_FORMAT, Params.USER_NAME, userName, Params.REG_ID, regId);
        } catch (final UnsupportedEncodingException e) {
            Logger.ex(e);
            return null;
        }
    }

    public String getUserName(final Uri uri) {
        try {
            return URLDecoder.decode(uri.getQueryParameter(Params.USER_NAME), ENCODING);
        } catch (final UnsupportedEncodingException e) {
            Logger.ex(e);
            return null;
        }
    }

    public String getRegId(final Uri uri) {
        try {
            return URLDecoder.decode(uri.getQueryParameter(Params.REG_ID), ENCODING);
        } catch (final UnsupportedEncodingException e) {
            Logger.ex(e);
            return null;
        }
    }
}
