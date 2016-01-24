package my.beelzik.mobile.wordbook.ui.widget.fraction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import my.beelzik.mobile.wordbook.utils.FractionViewUtils;

/**
 * Created by Andrey on 12.01.2016.
 */
public class FractionRecyclerView extends RecyclerView {
    public FractionRecyclerView(Context context) {
        super(context);
    }

    public FractionRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FractionRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getXFraction() {
        return FractionViewUtils.getViewXFraction(this);
    }

    public void setXFraction(float xFraction) {
        FractionViewUtils.setViewXFraction(this, xFraction);
    }
}
