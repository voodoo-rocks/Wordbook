package my.beelzik.mobile.wordbook.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;

/**
 * Created by Andrey on 02.01.2016.
 */
public abstract  class BaseToolbarActivity extends BaseBoundDBServiceActivity {

    @Nullable
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mToolbar != null){
            setSupportActionBar(mToolbar);
            onSetupToolbar(getSupportActionBar());
        }

    }

    protected  void onSetupToolbar(ActionBar supportActionBar) {

    }
}
