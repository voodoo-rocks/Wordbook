package my.beelzik.mobile.wordbook.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.ui.fragment.QuizFragment;
import my.beelzik.mobile.wordbook.ui.fragment.QuizOptionFragment;
import my.beelzik.mobile.wordbook.ui.widget.quiz.QuizFloatingActionButton;
import my.beelzik.mobile.wordbook.utils.ActivityAnimationUtils;

/**
 * Created by Andrey on 07.01.2016.
 */
public class QuizActivity extends BaseToolbarActivity {

    @Bind(R.id.app_bar)
    public AppBarLayout mAppBarLayout;

    @Bind(R.id.fab_accept)
    public QuizFloatingActionButton mAcceptFab;

    @Bind(R.id.fab_play)
    public FloatingActionButton mPlayFab;

    @Bind(R.id.coordinator_layout)
    public CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.toolbar_layout)
    public CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.correctAnswersCountIndicator)
    public TextView mCorrectIndicator;

    @Bind(R.id.incorrectAnswersCountIndicator)
    public TextView mIncorrectIndicator;

    @Bind(R.id.currentQuizNumber)
    public TextView mCurrentQuizNumberIndicator;

    @Bind(R.id.totalQuizNumber)
    public TextView mTotalQuizNumberIndicator;

    @Bind(R.id.gameProgress)
    public ProgressBar mGameProgressBar;

    public static void openQuizActivity(Activity activity){
        Intent intent = new Intent(activity, QuizActivity.class);
        activity.startActivity(intent);
        ActivityAnimationUtils.animateActivityStart(activity);
    }

    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activity_quiz_tree;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppBarLayout.setExpanded(false, false);

        mAcceptFab.hideOutOfAnchor();
        mPlayFab.hide();

        if (savedInstanceState == null) {

            if(getUserConfig().isUserOptionSelectAsDefault()){
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new QuizFragment(), QuizFragment.class.getSimpleName())
                        .commit();
            }else{
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new QuizOptionFragment(), QuizOptionFragment.class.getSimpleName())
                        .commit();
            }

        }
    }


    @Override
    protected void onSetupToolbar(ActionBar supportActionBar) {
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setTitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            ActivityAnimationUtils.animateActivityClose(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


}
