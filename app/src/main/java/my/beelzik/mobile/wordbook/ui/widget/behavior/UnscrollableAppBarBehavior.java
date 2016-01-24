package my.beelzik.mobile.wordbook.ui.widget.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Andrey on 19.01.2016.
 */
public class UnscrollableAppBarBehavior extends AppBarLayout.Behavior {

    public UnscrollableAppBarBehavior() {
        init();
    }


    public UnscrollableAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        super.setDragCallback(
                new DragCallback() {
                    @Override
                    public boolean canDrag(AppBarLayout appBarLayout) {
                        return false;
                    }
                }
        );
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return false;
    }




}
