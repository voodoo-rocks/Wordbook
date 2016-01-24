package my.beelzik.mobile.wordbook.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.db.AsyncWordDBWriter;

/**
 * Created by Andrey on 06.01.2016.
 */
public class AddWordActivity extends BaseBoundDBServiceActivity{

    @Bind(R.id.native_word)
    EditText mNativeWord;

    @Bind(R.id.learn_word)
    EditText mLearnWord;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.add_word)
    FloatingActionButton mAddWordFab;

    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activiy_add_word;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAddWordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewWord();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAddWordFab.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAddWordFab.show();
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAddWordFab.hide();
    }

    private void insertNewWord() {
        if(!TextUtils.isEmpty(mNativeWord.getText()) && !TextUtils.isEmpty(mLearnWord.getText())){
            if (isDBServiceConnected()){
                getDBService().getWordDB().insertWord(mNativeWord.getText().toString(), mLearnWord.getText().toString(),
                        new AsyncWordDBWriter.InsertCallback() {
                            @Override
                            public void insertSuccess(Long id) {

                                mNativeWord.setText(null);
                                mLearnWord.setText(null);
                                Snackbar.make(mCoordinatorLayout,getString(R.string.word_successfully_added),Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void insertFailed() {
                                Snackbar.make(mCoordinatorLayout,R.string.word_add_was_failed,Snackbar.LENGTH_LONG)
                                        .setAction(R.string.retry, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                insertNewWord();
                                            }
                                        })
                                        .show();
                            }
                        });
            }
        }
    }
}
