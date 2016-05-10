package io.voltage.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Set;

import io.voltage.app.R;
import io.voltage.app.activities.ConversationNewActivity;

public class ConversationNewNameFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private Button mCreateButton;
    private EditText mConversationName;

    private Set<String> mRegIds;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_new_name, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCreateButton = (Button) view.findViewById(R.id.conversation_create);
        mCreateButton.setOnClickListener(this);

        mConversationName = (EditText) view.findViewById(R.id.conversation_name);
        mConversationName.addTextChangedListener(this);
    }

    public void setRegistrationIds(final Set<String> regIds) {
        mRegIds = regIds;
        checkRequirements();
    }

    @Override
    public void onClick(final View view) {
        final ConversationNewActivity activity = (ConversationNewActivity) getActivity();
        activity.createThread(mConversationName.getText().toString(), mRegIds);
    }

    @Override
    public void beforeTextChanged(final CharSequence sequence, final int start, final int count, final int after) {

    }

    @Override
    public void onTextChanged(final CharSequence sequence, final int start, final int before, final int count) {

    }

    @Override
    public void afterTextChanged(final Editable editable) {
        checkRequirements();
    }

    private void checkRequirements() {
        final boolean itemsChecked = mRegIds != null && mRegIds.size() > 0;
        mCreateButton.setEnabled(itemsChecked);
    }
}