package io.voltage.app.utils;

import android.content.Context;

public class DisplayUtils {

	public static int convertToDp(final Context context, final int value) {
		final float density = context.getResources().getDisplayMetrics().density;
		return (int) (value * density + 0.5f);
	}
	
}
