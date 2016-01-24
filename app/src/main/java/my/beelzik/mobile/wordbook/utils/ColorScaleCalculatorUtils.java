package my.beelzik.mobile.wordbook.utils;

import android.graphics.Color;

/**
 * Created by Beelzik on 31.05.2015.
 */
public class ColorScaleCalculatorUtils {

    private final static String LOG_TAG = "ColorScaleCalculatorUtils";

    public static int calculateRGBScale(int count, int position) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (position != count - 1) {
            int total = count - 1;
            int step = 100 / total;
            int n = step * (position);
            r = (255 * n) / 100;
            g = (255 * (100 - n)) / 100;
            b = 0;
        }


        return Color.argb(255, r, g, b);
    }

    public static int calculateHSVScale(int count, int position) {


        int color = Color.BLACK;
        if (position != 0) {
            int h = 0;
            int s = 1;
            int v = 1;

            int total = count - 1;
            int step = 120 / total;
            int n = step * (position);
            h = n;
            float[] hsv = new float[3];
            hsv[0] = h;
            hsv[1] = s;
            hsv[2] = v;
            color = Color.HSVToColor(255, hsv);

        }
        return color;
    }
}
