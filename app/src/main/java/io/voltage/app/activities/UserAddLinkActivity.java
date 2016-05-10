package io.voltage.app.activities;

import android.content.Intent;
import android.net.Uri;

import io.voltage.app.helpers.ShareHelper;

public class UserAddLinkActivity extends UserAddActivity {

    private final ShareHelper mShareHelper = new ShareHelper();

    @Override
    public void onResume() {
        super.onResume();

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

            final Uri uri = getIntent().getData();
            final String userName = mShareHelper.getUserName(uri);
            final String regId = mShareHelper.getRegId(uri);

            setUserInfo(userName, regId);
        }
    }
}