package io.voltage.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ModernCursorAdapter;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.ConversationView;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.utils.ColorUtils;

public class ConversationAdapter extends ModernCursorAdapter {

    private static final String IMAGE_REGEX = "http(s?)://.+\\.(jpeg|jpg|gif|png|webp).*";

    private String mColor;

    public ConversationAdapter(final Context context, final Collection<Binding> bindings) {
        super(context, 0, bindings);

        mColor = VoltagePreferences.getPrimaryColour(context);
    }

    public void setColor(final String color) {
        mColor = color;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return ViewType.values().length;
    }

    @Override
    public int getItemViewType(final int position) {
        return getViewType(position).ordinal();
    }

    private ViewType getViewType(final int position) {
        final Cursor cursor = (Cursor) getItem(position);
        final String type = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.TYPE));
        return ViewType.MESSAGE.name().equals(type) ? getMessageType(cursor) : ViewType.ACTION;
    }

    private ViewType getMessageType(final Cursor cursor) {
        final String text = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.TEXT));
        return text.matches(IMAGE_REGEX) ? ViewType.IMAGE : ViewType.MESSAGE;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final ViewType viewType = getViewType(cursor.getPosition());
        final LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(viewType.getLayout(), parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);

        final ViewType type = getViewType(cursor.getPosition());

        if (type != ViewType.ACTION) {
            setBackgroundColor(view, context, cursor);
        }
    }

    private void setBackgroundColor(final View view, final Context context, final Cursor cursor) {
        final View container = view.findViewById(R.id.message_container);
        final GradientDrawable drawable = (GradientDrawable) container.getBackground();

        final String senderIdColumn = ConversationView.Columns.SENDER_ID;
        final String senderId = cursor.getString(cursor.getColumnIndex(senderIdColumn));

        if (senderId.equals(VoltagePreferences.getRegId(context))) {
            view.findViewById(R.id.message_letter).setVisibility(View.GONE);
            drawable.setColor(ColorUtils.alphaColor(mColor, 0.25f));
            view.setPadding(120, 0, 0, 0);
        } else {
            view.findViewById(R.id.message_letter).setVisibility(View.VISIBLE);
            drawable.setColor(ColorUtils.alphaColor(Color.LTGRAY, 0.25f));
            view.setPadding(0, 0, 40, 0);
        }

        // view.findViewById(R.id.message_info).clearAnimation();
    }

    public enum ViewType {
        ACTION(R.layout.list_item_action),
        MESSAGE(R.layout.list_item_message),
        IMAGE(R.layout.list_item_image);

        private int mLayout;

        ViewType(final int layout) {
            mLayout = layout;
        }

        public int getLayout() {
            return mLayout;
        }

        public Binding newBinding(final int viewId, final String column) {
            return new Binding(ordinal(), viewId, column);
        }
    }
}