package io.voltage.app.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.MessageSendMonitor;
import io.voltage.app.requests.MessageInsert;

public class MessageSendFragment extends Fragment implements View.OnClickListener {

    private ArcaDispatcher mDispatcher;

    private View mSendButton;
    private View mEmojiButton;
    private TextView mMessageView;
    private String mThreadId;

    @Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message_send, container, false);
	}

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSendButton = view.findViewById(R.id.message_send_action);
        mSendButton.setOnClickListener(this);

        mEmojiButton = view.findViewById(R.id.message_emojis);
        mEmojiButton.setOnClickListener(this);

        mMessageView = (TextView) view.findViewById(R.id.message_send_text);

        mDispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        mDispatcher.setRequestMonitor(new MessageSendMonitor());
    }

    public void setThreadId(final String threadId) {
        mThreadId = threadId;
    }

    @Override
    public void onClick(final View view) {
        if (view == mSendButton) {
            sendMessage();

        } else if (view == mEmojiButton) {
            showEmojis();
        }
    }

    private void sendMessage() {
        if (mMessageView.getText().length() > 0) {
            final String text = mMessageView.getText().toString();
            final String senderId = VoltagePreferences.getRegId(getActivity());
            final Insert insert = new MessageInsert(senderId, mThreadId, text, null, GcmPayload.Type.MESSAGE);

            mDispatcher.execute(insert);
            mMessageView.setText("");
        }
    }

    public void showEmojis() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.emojicons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final String[] emojis = getResources().getStringArray(R.array.emojicons);
                final String text = mMessageView.getText().toString();
                mMessageView.setText(text + emojis[which]);
            }
        });
        builder.create().show();
    }
}