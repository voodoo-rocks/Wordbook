package my.beelzik.mobile.wordbook.ui.fragment;

import android.app.LoaderManager;
import android.app.Service;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.QuizFactory;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.data.DictionaryData;
import my.beelzik.mobile.wordbook.data.Quiz;
import my.beelzik.mobile.wordbook.data.QuizGameOption;
import my.beelzik.mobile.wordbook.db.service.DBService;
import my.beelzik.mobile.wordbook.ui.activity.QuizActivity;
import my.beelzik.mobile.wordbook.ui.activity.SelectQuizWordListActivity;
import my.beelzik.mobile.wordbook.ui.widget.quiz.QuizAdapter;
import my.beelzik.mobile.wordbook.ui.widget.quiz.QuizFloatingActionButton;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 10.01.2016.
 */
@Accessors(prefix = "m")
public class QuizFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<DictionaryData>>, ServiceConnection {


    AppBarLayout mAppBarLayout;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    QuizFloatingActionButton mAcceptFab;
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;


    public TextView mCorrectIndicator;
    public TextView mIncorrectIndicator;
    public TextView mCurrentQuizNumberIndicator;
    public TextView mTotalQuizNumberIndicator;
    public ProgressBar mGameProgressBar;

    public static final String SAVE_STATE_QUIZ = "save_state_quiz";
    public static final String SAVE_STATE_GAME_STARTED = "save_state_game_started";
    public static final String SAVE_STATE_ANSWER_LIST = "save_state_answer_list";
    public static final String SAVE_STATE_SELECTED_VARIANT_POSITION = "save_state_selected_variant_position";
    public static final String SAVE_STATE_CORRECT_ANSWERS_COUNT = "save_state_correct_answers_count";


    public static final int QUIZ_LOADER = 200;

    QuizAdapter mAdapter;

    private Quiz mQuiz;

    QuizFactory mQuizFactory;

    private List<Quiz> mAnsweredQuizList;

    private QuizGameOption mQuizGameOption;

    @Getter
    DBService mDBService;

    boolean mGameStarted = false;

    private int mCorrectAnswersCount = 0;

    Handler mHandler = new Handler();

    @Override
    public void onDetach() {
        super.onDetach();
        mCollapsingToolbarLayout.setTitle(null);
    }


    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_quiz;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mQuizGameOption = getUserConfig().getGameOptions();
        if(savedInstanceState == null){


            BeeLog.debug("===========> mQuizGameOption: \n"+mQuizGameOption);
            mAnsweredQuizList = new ArrayList<>(mQuizGameOption.getNumberOfQuestionsInGame() == QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY ?
                    0 : mQuizGameOption.getNumberOfQuestionsInGame());

        }
        mQuizFactory = new QuizFactory(mQuizGameOption);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_ANSWER_LIST, (Serializable) mAnsweredQuizList);
        outState.putSerializable(SAVE_STATE_QUIZ, mQuiz);
        outState.putBoolean(SAVE_STATE_GAME_STARTED, mGameStarted);
        outState.putInt(SAVE_STATE_SELECTED_VARIANT_POSITION, mAdapter.getCheckedVariantItemPosition());
        outState.putInt(SAVE_STATE_CORRECT_ANSWERS_COUNT, mCorrectAnswersCount);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_quiz_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_options){

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_slide_in_right,R.anim.fragment_anim_slide_out_left)

                    .replace(R.id.container, new QuizOptionFragment()).commit();

            mAppBarLayout.post(new Runnable() {
                @Override
                public void run() {
                    mAppBarLayout.setExpanded(false, true);
                }
            });


            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }


    }

    private void initView(){
        if(getActivity() instanceof QuizActivity){
            QuizActivity quizActivity = (QuizActivity) getActivity();

            mAppBarLayout = quizActivity.mAppBarLayout;
            mAcceptFab = quizActivity.mAcceptFab;
            mCoordinatorLayout = quizActivity.mCoordinatorLayout;
            mCollapsingToolbarLayout = quizActivity.mCollapsingToolbarLayout;

            mCorrectIndicator = quizActivity.mCorrectIndicator;
            mIncorrectIndicator = quizActivity.mIncorrectIndicator;
            mCurrentQuizNumberIndicator = quizActivity.mCurrentQuizNumberIndicator;
            mTotalQuizNumberIndicator = quizActivity.mTotalQuizNumberIndicator;
            mGameProgressBar = quizActivity.mGameProgressBar;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        if(mQuizGameOption.getNumberOfQuestionsInGame() == QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY){
            mGameProgressBar.setVisibility(View.INVISIBLE);
            mTotalQuizNumberIndicator.setText(String.format(getString(R.string.total_number_indicator), getString(R.string.infinity)));
        }else{
            mGameProgressBar.setMax(mQuizGameOption.getNumberOfQuestionsInGame());
            mTotalQuizNumberIndicator.setText(String.format(getString(R.string.total_number_indicator),String.valueOf(mQuizGameOption.getNumberOfQuestionsInGame())));
        }

        mAdapter = new QuizAdapter(getActivity());

        if(savedInstanceState == null){
            mAcceptFab.hideOutOfAnchor();
            mAppBarLayout.post(new Runnable() {
                @Override
                public void run() {
                    mAppBarLayout.setExpanded(true, true);
                }
            });
        }else{
            mAppBarLayout.setExpanded(true, false);

            mGameStarted = savedInstanceState.getBoolean(SAVE_STATE_GAME_STARTED);
            mAnsweredQuizList = (List<Quiz>) savedInstanceState.getSerializable(SAVE_STATE_ANSWER_LIST);
            mQuiz = (Quiz) savedInstanceState.getSerializable(SAVE_STATE_QUIZ);
            mCorrectAnswersCount = savedInstanceState.getInt(SAVE_STATE_CORRECT_ANSWERS_COUNT);

            int checkedVariantPosition = savedInstanceState.getInt(SAVE_STATE_SELECTED_VARIANT_POSITION, QuizAdapter.NO_VARIANT_ITEM_CHECKED);
            mAdapter.setCheckedVariantItemPosition(checkedVariantPosition);
            if(checkedVariantPosition != QuizAdapter.NO_VARIANT_ITEM_CHECKED){
                mAcceptFab.showOutOfAnchor();
            }
            if(hasQuiz()){
                displayQuiz();
            }
        }


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter.setWordType(DictionaryData.WordType.LEARN);

        mAdapter.setOnVariantItemClickListener(new QuizAdapter.OnVariantItemClickListener() {
            @Override
            public void onVariantItemClick(View itemView, int position, DictionaryData variantData) {
                if (!mAcceptFab.isShown()) {
                    mAcceptFab.showOutOfAnchor();
                }
            }
        });

        mAcceptFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (hasQuiz() && mAdapter.getCheckedVariantItemPosition() != QuizAdapter.NO_VARIANT_ITEM_CHECKED) {
                    acceptAnswer();
                }
            }
        });
        updateQuizIndicators();
    }


    public void  updateQuizIndicators(){

        mCorrectIndicator.setText(String.format( getString(R.string.correct_number_indicator), mCorrectAnswersCount));
        mIncorrectIndicator.setText(String.format(getString(R.string.incorrect_number_indicator), mAnsweredQuizList.size() - mCorrectAnswersCount));
        int currentNumber;
        currentNumber = mAnsweredQuizList.size() + 1;

        if(mQuizGameOption.getNumberOfQuestionsInGame() != QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY){

            mGameProgressBar.setProgress(currentNumber - 1);
        }

        if(mQuizGameOption.getNumberOfQuestionsInGame() != QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY &&
                mQuizGameOption.getNumberOfQuestionsInGame() < (mAnsweredQuizList.size() + 1)){
            mCurrentQuizNumberIndicator.setText(String.format(getString(R.string.current_number_indicator),mQuizGameOption.getNumberOfQuestionsInGame()));
        }else{
            mCurrentQuizNumberIndicator.setText(String.format(getString(R.string.current_number_indicator),currentNumber));
        }

    }


    private boolean hasQuiz() {
        return mQuiz != null;
    }

    private void acceptAnswer() {


        final int answerPosition = mAdapter.getCheckedVariantItemPosition();

        mQuiz.setUserAnswer(mQuiz.getVariantList().get(answerPosition));

        if (mQuiz.isCorrectlySolved()) {

            mCorrectAnswersCount++;
            final View userAnswerView = mRecyclerView.getChildAt(answerPosition);
            BeeLog.debug("userAnswerView text: "+ ((TextView) userAnswerView.findViewById(R.id.variant)).getText());
            userAnswerView.setBackgroundColor(Color.GREEN);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isAdded()){
                        userAnswerView.setActivated(false);
                        userAnswerView.setBackground(getResources().getDrawable(R.drawable.cell_quiz_variant_background));
                        continueGame();
                    }


                }
            }, 2000);
            getDBService().getWordDB().updateWordRating(mQuiz.getUserAnswer().getId(), 10, mQuiz.getQuestionWordType(), null);
        } else {

            final View userAnswerView =  mRecyclerView.getChildAt(answerPosition);
            BeeLog.debug("userAnswerView text: " + ((TextView) userAnswerView.findViewById(R.id.variant)).getText());

            final View correctAnswerView = mRecyclerView.getChildAt(mQuiz.getVariantList().indexOf(mQuiz.getCorrectAnswer()));
            BeeLog.debug("correctAnswerView text: " + ((TextView) correctAnswerView.findViewById(R.id.variant)).getText());
            final AnimationDrawable incorrectAnim = new AnimationDrawable();
            incorrectAnim.addFrame(new ColorDrawable(Color.RED), 200);
            incorrectAnim.addFrame(new ColorDrawable(Color.RED), 400);
            incorrectAnim.setOneShot(true);
            userAnswerView.setBackground(incorrectAnim);
            incorrectAnim.start();
            final AnimationDrawable drawable = new AnimationDrawable();

            drawable.addFrame(new ColorDrawable(Color.GREEN), 400);
            drawable.addFrame(new ColorDrawable(Color.TRANSPARENT), 400);
            drawable.addFrame(new ColorDrawable(Color.GREEN), 400);
            drawable.addFrame(new ColorDrawable(Color.TRANSPARENT), 400);
            drawable.addFrame(new ColorDrawable(Color.GREEN), 400);
            drawable.setOneShot(false);
            correctAnswerView.setBackground(drawable);
            drawable.start();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        userAnswerView.setBackground(getResources().getDrawable(R.drawable.cell_quiz_variant_background));
                        correctAnswerView.setBackground(getResources().getDrawable(R.drawable.cell_quiz_variant_background));
                        drawable.stop();
                        continueGame();
                    }

                }
            }, 2000);

            getDBService().getWordDB().updateWordRating(mQuiz.getUserAnswer().getId(), -10, mQuiz.getVariantsWordType(), null);

            getDBService().getWordDB().updateWordRating(mQuiz.getCorrectAnswer().getId(), - 10, mQuiz.getQuestionWordType(), null);
        }
        mAdapter.clearChoices();
        mAnsweredQuizList.add(mQuiz);
        mAcceptFab.showAnswer(mQuiz.isCorrectlySolved());
        BeeLog.debug("answerPosition before clear choice: " + answerPosition);
        mAdapter.disableVariantChoiceMode();
        BeeLog.debug("answerPosition after clear choice: " + answerPosition);
        mQuiz = null;
        updateQuizIndicators();
    }

    private void continueGame(){
        if(isGamePassed()){
            endGame();
        }else{
            nextQuestion();
        }
    }

    private void endGame() {
        //TODO статистика
        Snackbar.make(getView(),"GAME COMPLEATED !!! ",Snackbar.LENGTH_LONG).show();
    }


    private boolean   isGamePassed(){
        if(mQuizGameOption.getNumberOfQuestionsInGame() == QuizGameOption.NUMBER_OF_QUESTION_IN_GAME_INFINITY){
            return false;
        }
        return mAnsweredQuizList.size() == mQuizGameOption.getNumberOfQuestionsInGame();
    }


    @Override
    public Loader<List<DictionaryData>> onCreateLoader(int id, Bundle args) {
        BeeLog.debug("onCreateLoader");
        return new AsyncTaskLoader<List<DictionaryData>>(getActivity()) {
            @Override
            public List<DictionaryData> loadInBackground() {
                if (isDBServiceConnected()) {
                    return getDBService().getWordDB().getRandomWordList(mQuizGameOption.getNumberOfVariantInQuestion(),
                            mQuizGameOption.getWordSelectType(),mQuizGameOption.getSelectDifferenceWordIdList() );
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<DictionaryData>> loader, List<DictionaryData> data) {
        BeeLog.debug("Quiz onLoadFinished");

        newQuestion(data);


    }

    @Override
    public void onLoaderReset(Loader<List<DictionaryData>> loader) {

    }

    private void nextQuestion() {
        mAcceptFab.hideOutOfAnchor();
        mAcceptFab.setDefault();
        getLoaderManager().restartLoader(QUIZ_LOADER, null, this).forceLoad();
    }

    private void newQuestion(List<DictionaryData> data) {
        if (data != null && data.size() > 0) {
            mGameStarted = true;
            mQuiz =  mQuizFactory.createQuiz(data);
            mAdapter.enableVariantChoiceMode();
            displayQuiz();
        }else{
            Snackbar.make(getView(),getString(R.string.cannnot_find_selected_words),Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.reselect), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO сделать возможным выбор слов отсюда
                            SelectQuizWordListActivity.openSelectQuizWordListActivity(getActivity(),QuizFragment.this);
                        }
                    })
                    .show();
        }
    }

    private void displayQuiz(){

            mAdapter.setWordType(mQuiz.getVariantsWordType());
            mAdapter.setData(mQuiz.getVariantList());
            BeeLog.debug("Question: " + mQuiz.getQuestion());
            mCollapsingToolbarLayout.setTitle(mQuiz.getQuestion());
            mAdapter.notifyDataSetChanged();


    }

    public void startGame(){
        if (getLoaderManager().getLoader(QUIZ_LOADER) != null) {
            getLoaderManager().destroyLoader(QUIZ_LOADER);
        }
        getLoaderManager().initLoader(QUIZ_LOADER, null, this).forceLoad();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), DBService.class);
        getActivity().bindService(intent, this, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(this);
        mDBService = null;
    }

    public boolean isDBServiceConnected(){
        return mDBService != null;
    }


    public void onServiceConnected(ComponentName name, IBinder service) {
        mDBService = ((DBService.DBBinder) service).getService();

        if(!mGameStarted) {

            startGame();
        }else if(!hasQuiz()){
            continueGame();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mDBService = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQuizGameOption = getUserConfig().getGameOptions();
    }
}
