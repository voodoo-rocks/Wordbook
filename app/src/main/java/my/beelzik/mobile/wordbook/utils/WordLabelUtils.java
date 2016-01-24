package my.beelzik.mobile.wordbook.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;


import java.util.HashMap;

import my.beelzik.mobile.wordbook.R;

/**
 * Created by Andrey on 04.01.2016.
 */
public class WordLabelUtils {

    private static final HashMap<String,Drawable> sWordLabelMap = new HashMap<>();


    public static Drawable getWordLabelDrawable(Context context,String word){

        if(TextUtils.isEmpty(word)){
            return null;
        }


        String letter = word.substring(0,1).toUpperCase();



        return sWordLabelMap.get(letter);
    }
}
