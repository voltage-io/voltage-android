package io.voltage.app.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.voltage.app.R;
import io.voltage.app.activities.BroadcastActivity;
import io.voltage.app.activities.UserSearchActivity;
import io.voltage.app.helpers.ShareHelper;

public class UserNewFragment extends Fragment implements View.OnClickListener {

    private Button mUserSearch;
    private Button mUserBroadcast;
    private Button mUserShare;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user_new, container, false);
	}

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserSearch = (Button) view.findViewById(R.id.user_search);
        mUserSearch.setOnClickListener(this);

        mUserBroadcast = (Button) view.findViewById(R.id.user_broadcast);
        mUserBroadcast.setOnClickListener(this);

        mUserShare = (Button) view.findViewById(R.id.user_share);
        mUserShare.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        if (view == mUserSearch) {
            UserSearchActivity.newInstance(getActivity());

        } else if (view == mUserBroadcast) {
            BroadcastActivity.newInstance(getActivity());

        } else if (view == mUserShare) {
            startShareActivity();
        }
    }

    private void startShareActivity() {
        final CharSequence title = getResources().getText(R.string.title_send);
        final String url = new ShareHelper.Default().getShareUrl(getActivity());

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");

        startActivity(Intent.createChooser(intent, title));
    }
}