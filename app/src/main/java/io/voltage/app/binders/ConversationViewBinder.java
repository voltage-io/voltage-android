package io.voltage.app.binders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ViewBinder;
import io.pivotal.arca.utils.StringUtils;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.ConversationView;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.handlers.MarkAsReadHandler;
import io.voltage.app.models.GcmPayload;

public class ConversationViewBinder implements ViewBinder {

    private static final String FORMAT = "h:mm aa MMM dd, yyyy";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(FORMAT, Locale.getDefault());

    private static final SparseArray<TextStyle> TEXT_STYLES = createStyleMap();

    private static SparseArray<TextStyle> createStyleMap() {
        final SparseArray<TextStyle> map = new SparseArray<TextStyle>();
        map.put(MessageTable.State.SENDING, new TextStyle(Typeface.NORMAL, Color.GRAY));
        map.put(MessageTable.State.UNREAD, new TextStyle(Typeface.BOLD, Color.BLACK));
        map.put(MessageTable.State.ERROR, new TextStyle(Typeface.BOLD, Color.RED));
        return map;
    }

    private MarkAsReadHandler mHandler;

    public ConversationViewBinder(final Context context) {
        mHandler = new MarkAsReadHandler(context);
    }

    @Override
    public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
        switch (view.getId()) {
            case R.id.message_letter:
                return setMessageLetter((TextView) view, cursor, binding);

            case R.id.message_timestamp:
                return setMessageTimestamp((TextView) view, cursor, binding);

            case R.id.message_state:
                return setMessageState((TextView) view, cursor, binding);

            case R.id.message_text:
                return setMessageText((TextView) view, cursor, binding);

            case R.id.message_metadata:
                return setMessageMetadata((TextView) view, cursor, binding);

            case R.id.message_image:
                return setMessageImage((SimpleDraweeView) view, cursor, binding);

            default:
                return false;
        }
    }

    private boolean setMessageLetter(final TextView view, final Cursor cursor, final Binding binding) {
        final String senderName = cursor.getString(binding.getColumnIndex());
        final String left = StringUtils.left(senderName, 1);
        view.setText(TextUtils.isEmpty(left) ? "?" : left);
        return true;
    }

    private boolean setMessageTimestamp(final TextView view, final Cursor cursor, final Binding binding) {
        final String timestamp = cursor.getString(binding.getColumnIndex());
        if (timestamp != null) {
            view.setText(FORMATTER.format(Long.parseLong(timestamp)));
        }
        return true;
    }

    private boolean setMessageState(final TextView view, final Cursor cursor, final Binding binding) {
        final int state = cursor.getInt(binding.getColumnIndex());
        view.setText(state >= MessageTable.State.RECEIPT ? "✓" : "");
        return true;
    }

    private boolean setMessageText(final TextView view, final Cursor cursor, final Binding binding) {
        final String text = cursor.getString(binding.getColumnIndex());
        final int state = cursor.getInt(cursor.getColumnIndex(ConversationView.Columns._STATE));

        markMessageAsRead(cursor);

        return applyStyle(view, state, text);
    }

    private boolean setMessageMetadata(final TextView view, final Cursor cursor, final Binding binding) {
        final int state = cursor.getInt(cursor.getColumnIndex(ConversationView.Columns._STATE));
        final String type = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.TYPE));

        switch (GcmPayload.Type.valueOf(type)) {
            case THREAD_CREATED:
            case THREAD_RENAMED:
                return setThreadMetadata(view, cursor, state);

            case USER_ADDED:
            case USER_REMOVED:
            case USER_LEFT:
                return setUserMetadata(view, cursor, state);
        }
        return false;
    }

    private boolean setThreadMetadata(final TextView view, final Cursor cursor, final int state) {
        final String metadata = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.METADATA));
        return applyStyle(view, state, metadata);
    }

    private boolean setUserMetadata(final TextView view, final Cursor cursor, final int state) {
        final String metaUser = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.META_USER));
        final String metadata = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.METADATA));

        if (TextUtils.isEmpty(metaUser) && VoltagePreferences.getRegId(view.getContext()).equals(metadata)) {
            return applyStyle(view, state, "You");
        }

        return applyStyle(view, state, metaUser);
    }

    private boolean applyStyle(final TextView view, final int state, final String text) {
        final TextStyle textStyle = TEXT_STYLES.get(state);
        if (textStyle == null) {
            return new TextStyle(Typeface.NORMAL, Color.BLACK).apply(view, text);
        } else {
            return textStyle.apply(view, text);
        }
    }

    private boolean setMessageImage(final SimpleDraweeView view, final Cursor cursor, final Binding binding) {
        final String url = cursor.getString(binding.getColumnIndex());

        final DraweeController controller = Fresco.newDraweeControllerBuilder()
            .setControllerListener(new OverlayListener(view))
            .setUri(Uri.parse(url))
            .build();

        view.setController(controller);

        markMessageAsRead(cursor);
        return true;
    }

    private void markMessageAsRead(final Cursor cursor) {
        final int state = cursor.getInt(cursor.getColumnIndex(ConversationView.Columns._STATE));
        final String uuid = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.MSG_UUID));

        if (state == MessageTable.State.UNREAD) {
            mHandler.sendMessageRead(uuid);
        }
    }

    private static final class TextStyle {

        private int mTypeface;
        private int mColor;

        public TextStyle(final int typeface, final int color) {
            mTypeface = typeface;
            mColor = color;
        }

        public boolean apply(final TextView view, final String text) {
            view.setTypeface(null, mTypeface);
            view.setTextColor(mColor);
            view.setText(text);
            return true;
        }
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
            final View overlay = viewGroup.findViewById(R.id.message_image_overlay);
            overlay.setVisibility(showPlayOverlay ? View.VISIBLE : View.INVISIBLE);

            if (animatable != null && autoPlayGifs) {
                animatable.start();
            }
        }
    }
}
