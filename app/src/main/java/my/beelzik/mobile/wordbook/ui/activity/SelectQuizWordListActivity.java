package my.beelzik.mobile.wordbook.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;
import java.util.Set;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.data.QuizGameOption;
import my.beelzik.mobile.wordbook.db.WordDB;
import my.beelzik.mobile.wordbook.db.table.BaseColumns;
import my.beelzik.mobile.wordbook.ui.widget.word.BaseWordAdapter;
import my.beelzik.mobile.wordbook.ui.widget.word.SelectQuizWordAdapter;
import my.beelzik.mobile.wordbook.ui.widget.word.WordCell;
import my.beelzik.mobile.wordbook.utils.ActivityAnimationUtils;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 16.01.2016.
 */
public class SelectQuizWordListActivity extends BaseSearchWordListActivity {

    public static final int REQUEST_CODE_SELECT_QUIZ_WORD_LIST = 100;

    private static final String SAVE_STATE_SELECTED_DIFFERENT_TYPE = "save_state_selected_different_type";
    private static final String SAVE_STATE_SELECTED_WORD_ID_SET = "save_state_selected_word_id_set";

    @Bind(R.id.fab_accept)
    FloatingActionButton mFab;

    private QuizGameOption mQuizGameOption;

    boolean mFirstAllWordListLoad = true;
    private SelectQuizWordAdapter mWordAdapter;

    private MenuItem mSelectionChangeItem;


    public static void openSelectQuizWordListActivity(Context context, Fragment fragment) {
        Intent intent = new Intent(context, SelectQuizWordListActivity.class);
        fragment.startActivityForResult(intent, REQUEST_CODE_SELECT_QUIZ_WORD_LIST);
        ActivityAnimationUtils.animateActivityStart(fragment.getActivity());
    }

    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activity_select_quiz_word_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuizGameOption = getUserConfig().getGameOptions();
        BeeLog.debug(getClass().getSimpleName() + " mQuizGameOption: " + mQuizGameOption);


        if(savedInstanceState == null){
            setSelectedDifferentType(mQuizGameOption.getWordSelectType());
            if (mQuizGameOption.getSelectDifferenceWordIdList() != null) {
                getSelectedDifferenceWordSet().addAll(mQuizGameOption.getSelectDifferenceWordIdList());
            }
        }else{
            setSelectedDifferentType((WordDB.DifferentType) savedInstanceState.getSerializable(SAVE_STATE_SELECTED_DIFFERENT_TYPE));
            Set<Long> selectedWordIdSet = (Set<Long>) savedInstanceState.getSerializable(SAVE_STATE_SELECTED_WORD_ID_SET);
            if (selectedWordIdSet != null) {
                getSelectedDifferenceWordSet().addAll(selectedWordIdSet);
            }
        }



        setResult(RESULT_CANCELED);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mQuizGameOption.setWordSelectOptions(getSelectedDifferentType(), getSelectedDifferenceWordSet());
                getUserConfig().setGameOptions(mQuizGameOption);

                setResult(RESULT_OK, null);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_SELECTED_DIFFERENT_TYPE,  mWordAdapter.getDifferentTypeSelector().getSelectedDifferentType());
        outState.putSerializable(SAVE_STATE_SELECTED_WORD_ID_SET,(Serializable) mWordAdapter.getDifferentTypeSelector().getSelectedDifferenceWordSet());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFab.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mFab.show();
                    }
                }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFab.hide();
    }

    @Override
    protected void onSetupToolbar(ActionBar supportActionBar) {
        supportActionBar.setTitle("");
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_select_quiz_words_menu, menu);
        super.onCreateOptionsMenu(menu);
        mSelectionChangeItem = menu.findItem(R.id.action_change_selection);

        if (getUserConfig().getGameOptions().getWordSelectType() == WordDB.DifferentType.EXACTLY) {
            mSelectionChangeItem.setTitle(R.string.select_all);
        } else {
            mSelectionChangeItem.setTitle(R.string.unselect_all);
        }

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectionChangeItem.setVisible(false);
                mSearchView.requestFocus();
            }
        });
        return true;
    }

    @Override
    public boolean onClose() {
        mSelectionChangeItem.setVisible(true);
        return super.onClose();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                ActivityAnimationUtils.animateActivityClose(this);
                return true;
            case R.id.action_change_selection:


                switch (getSelectedDifferentType()) {
                    case NONE:
                    case EXCEPT:
                        mWordAdapter.getDifferentTypeSelector().clearSelection();
                        setSelectedDifferentType(WordDB.DifferentType.EXACTLY);
                        mWordAdapter.refreshSelection();
                        item.setTitle(R.string.select_all);
                        break;
                    case EXACTLY:
                    default:
                        mWordAdapter.getDifferentTypeSelector().clearSelection();
                        setSelectedDifferentType(WordDB.DifferentType.NONE);
                        mWordAdapter.refreshSelection();
                        item.setTitle(R.string.unselect_all);
                        break;
                }

                updateTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private WordDB.DifferentType getSelectedDifferentType() {
        return mWordAdapter.getDifferentTypeSelector().getSelectedDifferentType();
    }

    private void setSelectedDifferentType(WordDB.DifferentType type) {
        mWordAdapter.getDifferentTypeSelector().setSelectedDifferentType(type);
    }

    private Set<Long> getSelectedDifferenceWordSet() {
        return mWordAdapter.getDifferentTypeSelector().getSelectedDifferenceWordSet();
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        BeeLog.debug(getClass().getSimpleName() + " onLoadFinished data: " + (data == null ? "NULL" : data.getCount()));
        if (loader.getId() == DEFAULT_WORD_LOADER && data != null) {

            if (mFirstAllWordListLoad) {
                mFirstAllWordListLoad = false;
                updateTitle();
            }
        }
        super.onLoadFinished(loader, data);
    }

    private void updateTitle() {
        if (getSupportActionBar() != null) {

            if (getSelectedDifferenceWordSet().size() == 0 && getSelectedDifferentType() != WordDB.DifferentType.NONE) {
                getSupportActionBar().setTitle(getString(R.string.not_selected).toUpperCase());
            } else if (getSelectedDifferentType() == WordDB.DifferentType.NONE) {
                getSupportActionBar().setTitle(getString(R.string.all_selected).toUpperCase());
            } else if (getSelectedDifferentType() == WordDB.DifferentType.EXCEPT) {
                getSupportActionBar().setTitle(String.format(getString(R.string.all_except_number), getSelectedDifferenceWordSet().size()).toUpperCase());
            } else {
                getSupportActionBar().setTitle(String.valueOf(getSelectedDifferenceWordSet().size()));
            }
        }

    }

    @Override
    protected BaseWordAdapter createWordAdapter() {
        mWordAdapter = new SelectQuizWordAdapter(this, null);
        mWordAdapter.setOnWordItemClickListener(new BaseWordAdapter.OnWordItemClickListener() {
            @Override
            public void onWordItemClick(int position, long id, WordCell cell) {
                updateTitle();
            }
        });
        return mWordAdapter;
    }
}
