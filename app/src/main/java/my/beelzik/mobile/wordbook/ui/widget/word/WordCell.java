package my.beelzik.mobile.wordbook.ui.widget.word;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.db.table.DictionaryTable;
import my.beelzik.mobile.wordbook.utils.BeeLog;
import my.beelzik.mobile.wordbook.utils.ColorScaleCalculatorUtils;

/**
 * Created by Andrey on 05.01.2016.
 */
@Accessors(prefix = "m")
public class WordCell extends RelativeLayout {

    @Bind(R.id.word)
    TextView mWord;

    @Bind(R.id.translate)
    TextView mTranslate;

    @Bind(R.id.label_flipper)
    ViewFlipper mLabelFlipper;


    FrameLayout mWordLabelFrameLayout;
    TextView mWordLabel;

    FrameLayout mSelectLabelFrameLayout;
    ImageView mSelectLabel;

   Animation mSelectLabelAnimation;

    @Bind(R.id.body)
    RelativeLayout mBody;


    @Getter private long mWordId;
    @Getter private int  mWordPosition;

    Animation mFlipInAnimation;
    Animation mFlipOutAnimation;

    long mFlipAnimationDuration;


    public WordCell(Context context) {
        super(context);
        init();
    }

    public WordCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.cell_word, this);
        ButterKnife.bind(this);

        mWordLabelFrameLayout = (FrameLayout) View.inflate(getContext(),R.layout.view_word_labe_front, null);
        mWordLabelFrameLayout.setBackgroundResource(R.drawable.circle_light_transparent);
        mWordLabel = (TextView) mWordLabelFrameLayout.findViewById(R.id.word_lable_text);


        mSelectLabelFrameLayout = (FrameLayout) View.inflate(getContext(), R.layout.view_word_label_back, null);
        mSelectLabel = (ImageView) mSelectLabelFrameLayout.findViewById(R.id.word_label_select);



        mSelectLabel.setImageResource(R.drawable.ic_action_done);

        initFlipAnimation();
        setFlipAnimation();


        mLabelFlipper.addView(mWordLabelFrameLayout);
        mLabelFlipper.addView(mSelectLabelFrameLayout);

        mSelectLabelAnimation =  AnimationUtils.loadAnimation(getContext(),
                R.anim.scale_up);

    }

    private void initFlipAnimation(){
        mFlipInAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.grow_from_middle_x_axis);
        mFlipOutAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.shrink_to_middle_x_axis);

        mFlipAnimationDuration = mFlipInAnimation.getDuration();

        mFlipInAnimation.setStartOffset(mFlipOutAnimation.getDuration());
        mLabelFlipper.setInAnimation(mFlipInAnimation);
        mLabelFlipper.setOutAnimation(mFlipOutAnimation);
    }


    private void setFlipAnimation(){
        mLabelFlipper.setInAnimation(mFlipInAnimation);
        mLabelFlipper.setOutAnimation(mFlipOutAnimation);

    }

    private void removeFlipAnimation(){

        mLabelFlipper.setInAnimation(null);
        mLabelFlipper.setOutAnimation(null);
    }

    public void bindData(Cursor data) {
        mWordPosition = data.getPosition();
        mWordId = data.getLong(data.getColumnIndex(BaseColumns._ID));
        String nativeWord = data.getString(data.getColumnIndex(DictionaryTable.Columns.NATIVE));
        String learnWord = data.getString(data.getColumnIndex(DictionaryTable.Columns.LEARN));
        int nativeRating = data.getInt(data.getColumnIndex(DictionaryTable.Columns.NATIVE_RATING));
        int learnRating = data.getInt(data.getColumnIndex(DictionaryTable.Columns.LEARN_RATING));

        mWord.setText(nativeWord);
        mTranslate.setText(learnWord);

        if(!TextUtils.isEmpty(nativeWord)){
            mWordLabel.setText(nativeWord.substring(0,1).toUpperCase());
        }



        int[] cellColors = new int[2];
        //Цвет [0] соответстует правому цвету градиента - те цвету ИЗУЧАЕМОГО(learn) языка
        cellColors[0] = ColorScaleCalculatorUtils.calculateHSVScale(100, learnRating);

        //Цвет [1] соответстует левому цвету градиента - те цвету РОДНОГО(native) языка
        cellColors[1] = ColorScaleCalculatorUtils.calculateHSVScale(100, nativeRating);


        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TR_BL,cellColors);
        setBackground(gradientDrawable);
    }

    @Override
    public void setActivated(final boolean activated) {

        if(isActivated() != activated){
            mLabelFlipper.setDisplayedChild(activated ? 1 : 0);
            post(new Runnable() {
                @Override
                public void run() {
                    if (activated) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mSelectLabel.setAlpha(1f);
                                mSelectLabel.startAnimation(mSelectLabelAnimation);
                            }
                        });

                    }
                }
            });
        }

        super.setActivated(activated);


    }

    public void forceActivate(boolean activate) {
        if(isActivated() != activate){
         //   BeeLog.debug("forceActivate(" + mWordId + "): " + activate);
            removeFlipAnimation();
            mLabelFlipper.showNext();
            setFlipAnimation();
            super.setActivated(activate);
        }
    }
}


