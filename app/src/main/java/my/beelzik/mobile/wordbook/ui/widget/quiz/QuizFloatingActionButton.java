package my.beelzik.mobile.wordbook.ui.widget.quiz;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 08.01.2016.
 */
public class QuizFloatingActionButton extends FloatingActionButton {

    int mLayoutAnchorResId = View.NO_ID;

    CoordinatorLayout.Behavior mBehavior;

    ColorStateList mDefaultTintList;


    public QuizFloatingActionButton(Context context) {
        this(context,null);
    }

    public QuizFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuizFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoordinatorLayout_LayoutParams);


        mLayoutAnchorResId = a.getResourceId(R.styleable.CoordinatorLayout_LayoutParams_layout_anchor,View.NO_ID);
        mBehavior = new Behavior();
        BeeLog.debug("QuizFloatingActionButton mLayoutAnchor: " + mLayoutAnchorResId + " mLayoutAnchor == View.NO_ID: " + (mLayoutAnchorResId == View.NO_ID));
        a.recycle();

        mDefaultTintList = getBackgroundTintList();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if(params instanceof CoordinatorLayout.LayoutParams){
            mLayoutAnchorResId = ((CoordinatorLayout.LayoutParams) params).getAnchorId();
        }
    }



    public void hideOutOfAnchor(final OnVisibilityChangedListener listener) {
        if(mLayoutAnchorResId != View.NO_ID && getLayoutParams() instanceof CoordinatorLayout.LayoutParams){

            super.hide(new OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {

                    if (listener != null) {
                        listener.onShown(fab);
                    }
                }

                @Override
                public void onHidden(FloatingActionButton fab) {
                    CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) getLayoutParams();
                    p.setBehavior(null); //should disable default animations
                    p.setAnchorId(View.NO_ID); //should let you set visibility
                    QuizFloatingActionButton.super.setLayoutParams(p);
                    if (listener != null) {
                        listener.onHidden(fab);
                    }

                }
            });

            // mAcceptFab.clearAnimation();
        }else{
            super.hide(listener);
        }
    }


    public void hideOutOfAnchor() {
        hideOutOfAnchor(null);
    }


    public void showOutOfAnchor() {
        showOutOfAnchor(null);
    }



    public void showOutOfAnchor(OnVisibilityChangedListener listener) {

        if( getLayoutParams() instanceof CoordinatorLayout.LayoutParams){
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) getLayoutParams();
            p.setBehavior(mBehavior);
            p.setAnchorId(mLayoutAnchorResId);
            setLayoutParams(p);
        }else{
            super.show(listener);
        }


    }


    public void setDefault(){
        setImageResource(R.drawable.ic_submit);
        setBackgroundTintList(mDefaultTintList);
    }

    public void showAnswer(boolean answer){
        if(answer){
            showCorrect();
        }else{
            showIncorrect();
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void showCorrect(){

        final ObjectAnimator animator = ObjectAnimator.ofInt(this,
                "backgroundColor",
                Color.WHITE, Color.GREEN);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(500);

        post(new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        });
    }

    public void showIncorrect(){
        setImageResource(R.drawable.ic_cancel);
        final ObjectAnimator animator = ObjectAnimator.ofInt(this,
                "backgroundColor",
                Color.WHITE, Color.RED);
        animator.setEvaluator(new ArgbEvaluator());


        animator.setDuration(500);
        post(new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        });
    }


}
