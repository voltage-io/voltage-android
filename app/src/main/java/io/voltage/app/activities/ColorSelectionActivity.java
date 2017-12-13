package io.voltage.app.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.voltage.app.R;
import io.voltage.app.activities.ColorSelectionActivity.ColorSelectionFragment.OnColorSelectedListener;

public class ColorSelectionActivity extends FragmentActivity {

    private interface Extras {
        String COLOR = "color";
    }

    public static void newInstance(final Activity activity, final int requestCode) {
        final Intent intent = new Intent(activity, ColorSelectionActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static String extractColor(final Intent intent) {
        return intent.getStringExtra(Extras.COLOR);
    }

    private static Intent newResult(final String color) {
        final Intent intent = new Intent();
        intent.putExtra(Extras.COLOR, color);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);
        setTitle(R.string.title_color_select);

        findColorFragment().setOnColorSelectedListener(new ColorListener());
    }

    private ColorSelectionFragment findColorFragment() {
        final FragmentManager manager = getFragmentManager();
        return (ColorSelectionFragment) manager.findFragmentById(R.id.fragment_color_selection);
    }

    public class ColorListener implements OnColorSelectedListener {

        @Override
        public void onColorSelected(final String color) {
            setResult(RESULT_OK, newResult(color));
            finish();
        }
    }

    public static class ColorSelectionFragment extends Fragment implements View.OnClickListener {

        public interface OnColorSelectedListener {
            void onColorSelected(String color);
        }

        private OnColorSelectedListener mListener;

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_color_selection, container, false);
        }

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            setOnClickListenerForChildren(view);
        }

        public void setOnColorSelectedListener(final OnColorSelectedListener listener) {
            mListener = listener;
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

            if (mListener != null) {
                mListener.onColorSelected(hexColor);
            }
        }
    }
}