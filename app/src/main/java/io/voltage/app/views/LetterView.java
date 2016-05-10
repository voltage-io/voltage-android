package io.voltage.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import io.voltage.app.utils.DisplayUtils;

public class LetterView extends TextView {

    public LetterView(final Context context) {
        super(context);
    }

    public LetterView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final int width = DisplayUtils.convertToDp(getContext(), 2);
        final int textColor = getTextColors().getDefaultColor();
        setTextColor(Color.parseColor("#cccccc"));
        getPaint().setStrokeWidth(width);
        getPaint().setStyle(Paint.Style.STROKE);
        super.onDraw(canvas);
        setTextColor(textColor);
        getPaint().setStrokeWidth(0);
        getPaint().setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
    }
}
