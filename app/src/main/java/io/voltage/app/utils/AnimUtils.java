package io.voltage.app.utils;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import io.voltage.app.R;

public class AnimUtils {

    public static void fadeIn(final View view) {
        if (view != null) {
            final Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }
    }

    public static void animate(final View view) {
        if (view != null) {
            if (view instanceof SimpleDraweeView) {
                animate((SimpleDraweeView) view);
            }
        }
    }

    private static void animate(final SimpleDraweeView view) {
        final DraweeController controller = view.getController();
        if (controller != null) {
            final Animatable animatable = controller.getAnimatable();

            toggleAnimatable(view, animatable);
        }
    }

    private static void toggleAnimatable(final View view, final Animatable animatable) {
        if (animatable != null) {
            final boolean running = animatable.isRunning();

            final ViewGroup viewGroup = (ViewGroup) view.getParent();
            final View overlay = viewGroup.findViewById(R.id.message_image_overlay);
            overlay.setVisibility(running ? View.VISIBLE : View.INVISIBLE);

            if (running) {
                animatable.stop();
            } else {
                animatable.start();
            }
        }
    }
}
