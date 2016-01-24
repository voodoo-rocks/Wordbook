package my.beelzik.mobile.wordbook.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;

import java.text.MessageFormat;
import java.util.Set;

import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.db.AsyncWordDBWriter;
import my.beelzik.mobile.wordbook.db.table.DictionaryTable;
import my.beelzik.mobile.wordbook.ui.widget.word.BaseWordAdapter;
import my.beelzik.mobile.wordbook.ui.widget.word.ExactlySelectWordAdapter;
import my.beelzik.mobile.wordbook.ui.widget.word.WordCell;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 02.01.2016.
 */
public  class EditWordListActivity extends BaseSearchWordListActivity {

    ActionMode.Callback mCallback;

    ActionMode mActionMode;
    private ExactlySelectWordAdapter mWordAdapter;

    private boolean mAnimateUnselect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.action_mode_select_words, menu);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    //set your gray color
                   getWindow().setStatusBarColor(getResources().getColor(R.color.colorDarkGray));
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                mode.setTitle(String.valueOf(getSelectedWordIdSet().size()));
                menu.findItem(R.id.action_edit).setVisible(getSelectedWordIdSet().size() == 1);

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_edit:

                        BeeLog.debug("getSelectedWordPositionSet().size(): "+getSelectedWordPositionSet().size());
                        if(getSelectedWordPositionSet().size() == 1){
                            Cursor cursor = (Cursor)
                                    getCursorAdapter().getItem(getSelectedWordPositionSet().iterator().next());

                            View view = View.inflate(EditWordListActivity.this,R.layout.dialog_edit,null);

                            final EditText nativeWord = (EditText) view.findViewById(R.id.native_word);
                            final EditText learnWord = (EditText) view.findViewById(R.id.learn_word);

                            nativeWord.setText(cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE)));
                            learnWord.setText(cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.LEARN)));
                            AlertDialog.Builder adb = new AlertDialog.Builder(EditWordListActivity.this);

                            adb.setView(view);
                            adb.setTitle(R.string.dialog_ttl_edit_word);


                            adb.setPositiveButton(R.string.yes_edit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tryToEditWords(nativeWord.getText().toString(), learnWord.getText().toString());
                                }
                            });

                            adb.setNegativeButton(R.string.no_do_not,null);
                            adb.show();

                        }



                        break;
                    case R.id.action_delete:

                        AlertDialog.Builder adb = new AlertDialog.Builder(EditWordListActivity.this);
                        adb.setTitle(R.string.dialog_ttl_delete_word);
                        adb.setMessage(String.format(getString(R.string.dialog_msg_delete_word),getSelectedWordIdSet().size()));
                        adb.setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tryToDeleteWords();
                            }
                        });
                        adb.setNegativeButton(R.string.no_do_not, null);
                        adb.show();
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mWordAdapter.getExactlySelector().clearSelection();
                if(mAnimateUnselect){
                    mWordAdapter.refreshSelection();
                }else{
                    mAnimateUnselect = true;
                    mWordAdapter.notifyDataSetChanged();
                }

                mActionMode = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        };
    }


      private void tryToEditWords(final String nativePerformance,final String learnPerformance) {
        if(isDBServiceConnected()){

            long id = getSelectedWordIdSet().iterator().next();
            getDBService().getWordDB().editWord(id, nativePerformance, learnPerformance,
                    new AsyncWordDBWriter.UpdateDeleteCallback() {
                        @Override
                        public void modificationSuccess(Integer count) {
                            mActionMode.finish();
                            getCursorAdapter().notifyDataSetChanged();
                            Snackbar.make(getCoordinatorLayout(),R.string.word_was_edited, Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void modificationFailed() {
                            Snackbar.make(getCoordinatorLayout(), R.string.word_edit_was_failed, Snackbar.LENGTH_SHORT)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            tryToDeleteWords();
                                        }
                                    })
                                    .show();
                        }
                    });
        }
    }


    private void tryToDeleteWords() {
        if(isDBServiceConnected()){

            Long[] ids = new Long[getSelectedWordIdSet().size()];
            ids =  getSelectedWordIdSet().toArray(ids);
            getDBService().getWordDB().deleteWords(ids, new AsyncWordDBWriter.UpdateDeleteCallback() {
                @Override
                public void modificationSuccess(Integer count) {
                    mWordAdapter.getExactlySelector().clearSelection();
                    getCursorAdapter().notifyDataSetChanged();
                    mAnimateUnselect = false;
                    mActionMode.finish();
                    Snackbar.make(getCoordinatorLayout(), String.format(getString(R.string.number_words_was_deleted), count), Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void modificationFailed() {
                    Snackbar.make(getCoordinatorLayout(), R.string.words_delete_was_failed, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tryToDeleteWords();
                                }
                            })
                            .show();
                }
            });
        }
    }



    @Override
    protected BaseWordAdapter createWordAdapter() {

        mWordAdapter =  new ExactlySelectWordAdapter(this,null);
        mWordAdapter.setOnWordItemClickListener(new BaseWordAdapter.OnWordItemClickListener() {
            @Override
            public void onWordItemClick(int position, long id, WordCell cell) {
                updateActionMode();
            }
        });
        return mWordAdapter;
    }

    private Set<Long> getSelectedWordIdSet(){
        return  mWordAdapter.getExactlySelector().getSelectedWordIdSet();
    }


    public Set<Integer> getSelectedWordPositionSet() {
        return mWordAdapter.getExactlySelector().getSelectedWordPositionSet();
    }

    private void updateActionMode(){
        if(getSelectedWordIdSet().size() == 1){
            if(mActionMode == null){
                mActionMode = startSupportActionMode(mCallback);
            }else{
                mActionMode.invalidate();
            }


        }else if(getSelectedWordIdSet().size() > 1){
            mActionMode.invalidate();
        }else if(mActionMode != null){
            mActionMode.finish();
        }
    }


}
