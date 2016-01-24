package my.beelzik.mobile.wordbook.utils;

import android.view.View;

/**
 * Created by Andrey on 12.01.2016.
 */
public class FractionViewUtils {

    public static float getViewXFraction(View view) {
        final int width = view.getWidth();
        return (width > 0) ? (view.getX() / width) : (view.getX());
    }

    public static void setViewXFraction(View source,float xFraction) {
        final int width = source.getWidth();
        source.setX((width > 0) ? (xFraction * width) : -9999);
    }
}
