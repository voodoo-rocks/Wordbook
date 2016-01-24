package my.beelzik.mobile.wordbook.ui.widget.fraction;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import my.beelzik.mobile.wordbook.utils.FractionViewUtils;

/**
 * Created by Andrey on 12.01.2016.
 */
public class FractionScrollView extends ScrollView {


    public FractionScrollView(Context context) {
        super(context);
    }

    public FractionScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FractionScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getXFraction() {
        return FractionViewUtils.getViewXFraction(this);
    }

    public void setXFraction(float xFraction) {
        FractionViewUtils.setViewXFraction(this,xFraction);
    }
}
