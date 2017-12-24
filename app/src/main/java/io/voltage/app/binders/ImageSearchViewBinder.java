package io.voltage.app.binders;

import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ViewBinder;
import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;

public class ImageSearchViewBinder implements ViewBinder {

    @Override
    public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
        switch (view.getId()) {

            case R.id.search_image:
                return setMessageImage((SimpleDraweeView) view, cursor, binding);

            default:
                return false;
        }
    }

    private boolean setMessageImage(final SimpleDraweeView view, final Cursor cursor, final Binding binding) {
        final String url = cursor.getString(binding.getColumnIndex());

        final DraweeController controller = Fresco.newDraweeControllerBuilder()
            .setControllerListener(new OverlayListener(view))
            .setUri(Uri.parse(url))
            .build();

        view.setController(controller);

        return true;
    }

    private static final class OverlayListener extends BaseControllerListener<ImageInfo> {

        private final View mView;

        public OverlayListener(final View view) {
            mView = view;
        }

        @Override
        public void onFinalImageSet(final String id, final ImageInfo imageInfo, final Animatable animatable) {
            final boolean autoPlayGifs = VoltagePreferences.shouldAutoPlayGifs(mView.getContext());
            final boolean showPlayOverlay = animatable != null && !autoPlayGifs;

            final ViewGroup viewGroup = (ViewGroup) mView.getParent();
            final View overlay = viewGroup.findViewById(R.id.search_image_overlay);
            overlay.setVisibility(showPlayOverlay ? View.VISIBLE : View.INVISIBLE);

            if (animatable != null && autoPlayGifs) {
                animatable.start();
            }
        }
    }
}
