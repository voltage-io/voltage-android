package io.voltage.app.utils;

import android.graphics.Color;

public class ColorUtils {

    private interface Components {
        int R = 16, G = 8, B = 0;
    }

    public static int lightenColor(final String color, final float multiplier) {
        return lightenColor(Color.parseColor(color), multiplier);
    }

    public static int lightenColor(final int color, final float multiplier) {
        final int red = lighten(color, Components.R, multiplier);
        final int green = lighten(color, Components.G, multiplier);
        final int blue = lighten(color, Components.B, multiplier);
        return Color.argb(255, red, green, blue);
    }

    public static int darkenColor(final String color, final float multiplier) {
        return darkenColor(Color.parseColor(color), multiplier);
    }

    public static int darkenColor(final int color, final float multiplier) {
        final int red = darken(color, Components.R, multiplier);
        final int green = darken(color, Components.G, multiplier);
        final int blue = darken(color, Components.B, multiplier);
        return Color.argb(255, red, green, blue);
    }

    public static int alphaColor(final String color, final float multiplier) {
        return alphaColor(Color.parseColor(color), multiplier);
    }

    public static int alphaColor(final int color, final float multiplier) {
        final int red = component(color, Components.R);
        final int green = component(color, Components.G);
        final int blue = component(color, Components.B);
        return Color.argb((int) (255 * multiplier), red, green, blue);
    }

    private static int lighten(final int color, final int component, final float multiplier) {
        return (int) Math.min(255, component(color, component) + 255 * multiplier);
    }

    private static int darken(final int color, final int component, final float multiplier) {
        return (int) Math.max(0, component(color, component) - 255 * multiplier);
    }

    private static int component(final int color, final int component) {
        return (color >> component) & 0xFF;
    }
}
