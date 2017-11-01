package io.voltage.app.helpers;

import android.content.Context;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.utils.Logger;

public interface ShareHelper {

    String getShareUrl(final Context context);
    String getRegId(final Uri uri);
    String getUserName(final Uri uri);
    String getPublicKey(final Uri uri);


    class Default implements ShareHelper {

        private interface Params {
            String REG_ID = "reg_id";
            String USER_NAME = "user_name";
            String PUBLIC_KEY = "public_key";
        }

        private static final String ENCODING = "UTF-8";
        private static final String SHARE_FORMAT = "http://voltage.io/add_friend?%s=%s&%s=%s&%s=%s";

        public String getShareUrl(final Context context) {
            try {
                final String regId = URLEncoder.encode(VoltagePreferences.getRegId(context), ENCODING);
                final String userName = URLEncoder.encode(VoltagePreferences.getUserName(context), ENCODING);
                final String publicKey = URLEncoder.encode(VoltagePreferences.getPublicKey(context), ENCODING);
                return String.format(SHARE_FORMAT, Params.REG_ID, regId, Params.USER_NAME, userName, Params.PUBLIC_KEY, publicKey);
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

        public String getUserName(final Uri uri) {
            try {
                return URLDecoder.decode(uri.getQueryParameter(Params.USER_NAME), ENCODING);
            } catch (final UnsupportedEncodingException e) {
                Logger.ex(e);
                return null;
            }
        }

        public String getPublicKey(final Uri uri) {
            try {
                return URLDecoder.decode(uri.getQueryParameter(Params.PUBLIC_KEY), ENCODING);
            } catch (final UnsupportedEncodingException e) {
                Logger.ex(e);
                return null;
            }
        }
    }
}
