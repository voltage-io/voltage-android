package io.voltage.app.fragments;

import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.voltage.app.R;
import io.voltage.app.helpers.AccountHelper;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private final AccountHelper mHelper = new AccountHelper.Default();

    private TextView mTextView;
    private Button mButton;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextView = (TextView) view.findViewById(R.id.account_name);

        mButton = (Button) view.findViewById(R.id.account_button);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        if (view == mButton) {
            setUsername(mTextView.getText().toString());
        }
    }

    private void setUsername(final String username) {
        final Activity activity = getActivity();

        if (!TextUtils.isEmpty(username)) {
            mHelper.setUsername(activity, username);
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
    }

    public void finishIfExists(final boolean finish) {
        final Activity activity = getActivity();
        final Account account = mHelper.getAccount(activity);

        if (account != null) {
            if (finish) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            } else {
                mTextView.setText(account.name);
            }
        }
    }
}
