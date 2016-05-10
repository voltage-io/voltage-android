package io.voltage.app.fragments;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;

public class UserColorFragment extends Fragment implements View.OnClickListener {

    @Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user_color, container, false);
	}

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnClickListenerForChildren(view);
    }

    private void setOnClickListenerForChildren(final View view) {
        if (view instanceof ViewGroup) {
            iterateChildrenForViewGroup((ViewGroup) view);
        } else {
            view.setOnClickListener(this);
        }
    }

    private void iterateChildrenForViewGroup(final ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            final View child = group.getChildAt(i);
            setOnClickListenerForChildren(child);
        }
    }

    @Override
    public void onClick(final View view) {
        final ColorDrawable drawable = (ColorDrawable) view.getBackground();
        final String hexColor = String.format("#%06X", (0xFFFFFF & drawable.getColor()));
        VoltagePreferences.setPrimaryColour(getActivity(), hexColor);
    }
}