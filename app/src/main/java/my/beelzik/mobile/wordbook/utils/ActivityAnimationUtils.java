package my.beelzik.mobile.wordbook.utils;

import android.app.Activity;
import android.content.Intent;

import my.beelzik.mobile.wordbook.R;

/**
 * Created by Andrey on 24.01.2016.
 */
public class ActivityAnimationUtils {

    public static void animateActivityStart(Activity activity){
        activity.overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_scale_down);
    }


    public static void animateActivityClose(Activity activity){
        activity.overridePendingTransition(R.anim.activity_scale_up, R.anim.activity_slide_in_right);
    }
}
