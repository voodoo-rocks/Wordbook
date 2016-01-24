package my.beelzik.mobile.wordbook.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Set;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.data.QuizGameOption;
import my.beelzik.mobile.wordbook.db.WordDB;
import my.beelzik.mobile.wordbook.ui.activity.QuizActivity;
import my.beelzik.mobile.wordbook.ui.activity.SelectQuizWordListActivity;
import my.beelzik.mobile.wordbook.ui.widget.seekbar.SimpleOnSeekBarChangeListener;
import my.beelzik.mobile.wordbook.utils.BeeLog;
import my.beelzik.mobile.wordbook.utils.SeekBarUtils;

/**
 * Created by Andrey on 11.01.2016.
 */
public class QuizOptionFragment extends BaseFragment {

    FloatingActionButton mPlayFab;

    @Bind(R.id.textNumberQuestion)
    TextView mTextNumberQuestion;

    @Bind(R.id.barNumberQuestion)
    SeekBar mBarNumberQuestion;

    @Bind(R.id.textNumberVariant)
    TextView mTextNumberVariant;

    @Bind(R.id.barNumberVariant)
    SeekBar mBarNumberVariant;

    @Bind(R.id.radioGroupLanguage)
    RadioGroup mRadioGroupLanguage;

    @Bind(R.id.selectWord)
    View mSelectWordView;

    @Bind(R.id.textSelectWord)
    TextView mTextSelectWordView;

    @Bind(R.id.boxUseAsDefault)
    CheckBox mBoxUseAsDefault;

    QuizGameOption mGameOption;



    private void initView() {
        if (getActivity() instanceof QuizActivity) {
            QuizActivity quizActivity = (QuizActivity) getActivity();
            mPlayFab = quizActivity.mPlayFab;
        }
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_game_option;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();


    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTextSelectWordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectQuizWordListActivity.openSelectQuizWordListActivity(getActivity(), QuizOptionFragment.this);
            }
        });

        mPlayFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!getUserConfig().getGameOptions().equals(mGameOption)) {
                            getUserConfig().setGameOptions(mGameOption);
                        }


                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fragment_anim_slide_in_left, R.anim.fragment_anim_slide_out_right, 0, 0)
                                .replace(R.id.container, new QuizFragment(), QuizFragment.class.getSimpleName())
                                .commit();
                    }
                }
        );

        mBarNumberQuestion.setOnSeekBarChangeListener(
                new SimpleOnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int realCurrentProgress = SeekBarUtils.getCurrentSeekBarProgress(seekBar, QuizGameOption.MIN_NUMBER_OF_QUESTION_IN_GAME,
                                QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY);
                        if (realCurrentProgress == QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY) {
                            mTextNumberQuestion.setText(getString(R.string.infinity_game).toUpperCase());
                            mGameOption.setNumberOfQuestionsInGame(realCurrentProgress);
                        } else {
                            mGameOption.setNumberOfQuestionsInGame(realCurrentProgress);
                            mTextNumberQuestion.setText(String.valueOf(realCurrentProgress));
                        }

                    }
                }
        );

        mBarNumberVariant.setOnSeekBarChangeListener(
                new SimpleOnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int realCurrentProgress = SeekBarUtils.getCurrentSeekBarProgress(seekBar, QuizGameOption.MIN_NUMBER_OF_VARIANT_IN_QUESTION,
                                QuizGameOption.MAX_NUMBER_OF_VARIANT_IN_QUESTION);
                        mGameOption.setNumberOfVariantInQuestion(realCurrentProgress);
                        mTextNumberVariant.setText(String.valueOf(realCurrentProgress));
                    }
                }
        );

        mRadioGroupLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioNative:
                        mGameOption.setQuestionLanguage(QuizGameOption.QUESTION_LANGUAGE_NATIVE);
                        break;
                    case R.id.radioLearn:
                        mGameOption.setQuestionLanguage(QuizGameOption.QUESTION_LANGUAGE_LEARN);
                        break;
                    case R.id.radioRandom:
                        mGameOption.setQuestionLanguage(QuizGameOption.QUESTION_LANGUAGE_RANDOM);
                        break;
                }
            }
        });

        mBoxUseAsDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BeeLog.debug("onCheckedChanged: "+isChecked);
                if(getUserConfig().isUserOptionSelectAsDefault() != isChecked){
                    getUserConfig().setUserOptionSelectAsDefault(isChecked);
                }
            }
        });


        displayWithGameOption();

        mBoxUseAsDefault.setChecked(getUserConfig().isUserOptionSelectAsDefault());

    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayFab.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayFab.show();
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayFab.hide();
    }

    public void displayWithGameOption() {
        mGameOption = getUserConfig().getGameOptions();
        BeeLog.debug("mGameOption: " + mGameOption);

        mTextNumberVariant.setText(String.valueOf(mGameOption.getNumberOfVariantInQuestion()));
        if(mGameOption.getNumberOfQuestionsInGame() == QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY){
            mTextNumberQuestion.setText(getString(R.string.infinity_game).toUpperCase());
        }else{
            mTextNumberQuestion.setText(String.valueOf(mGameOption.getNumberOfQuestionsInGame()));
        }



        if(mGameOption.getNumberOfQuestionsInGame() == QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY){
            mTextNumberQuestion.setText(getString(R.string.infinity_game).toUpperCase());
        }else{
            mTextNumberQuestion.setText(String.valueOf(mGameOption.getNumberOfQuestionsInGame()));
        }
        displayCurrentSelectedWords();
        displayCurrentVariantSeekBarProgress();
        displayCurrentQuestionSeekBarProgress();
        displayCurrentQuestionLanguage();

    }

    private void  displayCurrentSelectedWords(){
        if(mGameOption.getWordSelectType() == WordDB.DifferentType.NONE){
            mTextSelectWordView.setText(getString(R.string.all_words_selected).toUpperCase());
        }else  if(mGameOption.getWordSelectType() == WordDB.DifferentType.EXCEPT){
            mTextSelectWordView.setText(String.format(getString(R.string.all_expect_numer),mGameOption.getSelectDifferenceWordIdList().size()));
        }else{
            mTextSelectWordView.setText(String.valueOf(mGameOption.getSelectDifferenceWordIdList().size()));
        }
    }


    private void  displayCurrentQuestionLanguage() {
        switch (mGameOption.getQuestionLanguage()) {
            case QuizGameOption.QUESTION_LANGUAGE_NATIVE:
                mRadioGroupLanguage.check(R.id.radioNative);
                break;
            case QuizGameOption.QUESTION_LANGUAGE_LEARN:
                mRadioGroupLanguage.check(R.id.radioLearn);
                break;
            case QuizGameOption.QUESTION_LANGUAGE_RANDOM:
            default:
                mRadioGroupLanguage.check(R.id.radioRandom);
                break;

        }
    }

    private void  displayCurrentQuestionSeekBarProgress() {
        SeekBarUtils.setCurrentSeekBarProgress(mBarNumberQuestion, QuizGameOption.MIN_NUMBER_OF_QUESTION_IN_GAME,
                QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY, mGameOption.getNumberOfQuestionsInGame());
    }

    private void displayCurrentVariantSeekBarProgress() {
        SeekBarUtils.setCurrentSeekBarProgress(mBarNumberVariant, QuizGameOption.MIN_NUMBER_OF_VARIANT_IN_QUESTION,
                QuizGameOption.MAX_NUMBER_OF_VARIANT_IN_QUESTION, mGameOption.getNumberOfVariantInQuestion());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SelectQuizWordListActivity.REQUEST_CODE_SELECT_QUIZ_WORD_LIST && resultCode == Activity.RESULT_OK){
            mGameOption = getUserConfig().getGameOptions();
            displayCurrentSelectedWords();
        }
    }
}
