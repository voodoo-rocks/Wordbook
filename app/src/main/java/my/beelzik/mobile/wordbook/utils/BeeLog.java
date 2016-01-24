package my.beelzik.mobile.wordbook.utils;

import android.util.Log;

import my.beelzik.mobile.wordbook.storage.AppConfig;

/**
 * Класс для логирования приложения
 * <p/>
 * Created by Andrey on 25.09.2015.
 */
public class BeeLog {


    private static final String DEFAULT_LOG_TAG = "beeLog";

    public static void info(String label, String text) {
        info(DEFAULT_LOG_TAG, label, text);
    }

    public static void info(String tag, String label, String text) {
        if (AppConfig.DEBUG) {
            Log.i(tag, label + ": " + text);
        }
    }

    public static void debug(String tag, String text) {
        if (AppConfig.DEBUG) {
            Log.d(tag, "debug: " + text);
        }
    }

    public static void debug(String text) {
        debug(DEFAULT_LOG_TAG, text);
    }


    public static void error(String text) {
        error(DEFAULT_LOG_TAG, text);
    }

    public static void error(String tag, String text) {
        if (AppConfig.DEBUG) {
            if (text != null) {
                Log.i(tag, "error: " + text);
                printStackTrace();
            }
        }

    }

    private static void printStackTrace(String tag) {
        if (AppConfig.DEBUG) {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                Log.i(tag, ste.toString());
            }
        }
    }


    private static void printStackTrace() {
        printStackTrace(DEFAULT_LOG_TAG);
    }
}
