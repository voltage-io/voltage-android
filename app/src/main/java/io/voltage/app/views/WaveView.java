package io.voltage.app.views;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import io.voltage.app.R;

public class WaveView extends FrameLayout {

    private static final int WAVE_DISTANCE = 450;
    private static final int WAVE_DURATION = 5000;

    public WaveView(final Context context) {
        super(context);
        init(context);
    }

    public WaveView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaveView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        final Animation animation = new WaveAnimation();
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(WAVE_DURATION);
        startAnimation(animation);
    }

    private class WaveAnimation extends Animation {

        @Override
        protected void applyTransformation(final float time, final Transformation trans) {
            super.applyTransformation(time, trans);

            final int childCount = getChildCount();
            final View lastChild = getChildAt(childCount - 1);
            final float waveDistance = getMeasuredHeight() - WAVE_DISTANCE;

            if (childCount == 0 || lastChild.getY() < waveDistance) {
                final View child = createChildWave();
                addView(child);

                createAnimator(WAVE_DURATION, child).start();
            }
        }

        private View createChildWave() {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            return inflater.inflate(R.layout.list_item_wave, WaveView.this, false);
        }

        private ObjectAnimator createAnimator(final int duration, final View view) {
            final ObjectAnimator animator = createAnimator(view);
            animator.setInterpolator(new LinearInterpolator());
            animator.addListener(new RemoveListener(view));
            animator.setDuration(duration);
            return animator;
        }

        private ObjectAnimator createAnimator(final View view) {
            return ObjectAnimator.ofFloat(view, View.Y, getMeasuredHeight(), -WAVE_DISTANCE);
        }

        private class RemoveListener implements AnimatorListener {

            private View mView;

            public RemoveListener(final View view) {
                mView = view;
            }

            @Override
            public void onAnimationStart(final Animator animation) {}

            @Override
            public void onAnimationRepeat(final Animator animation) {}

            @Override
            public void onAnimationCancel(final Animator animation) {
                removeView(mView);
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                removeView(mView);
            }
        }
    }
}