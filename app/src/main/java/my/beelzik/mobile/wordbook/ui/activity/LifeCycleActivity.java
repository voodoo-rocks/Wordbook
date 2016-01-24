package my.beelzik.mobile.wordbook.ui.activity;

import android.os.Bundle;

import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 12.01.2016.
 */
public abstract class LifeCycleActivity extends EditWordListActivity {

    private static final String LOG_TAG = "LifeCycleActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeeLog.debug(LOG_TAG,"onCreate "+this.getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();

        BeeLog.debug(LOG_TAG, "onStart "+this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();

        BeeLog.debug(LOG_TAG, "onResume "+this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();

        BeeLog.debug(LOG_TAG, "onPause "+this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();

        BeeLog.debug(LOG_TAG, "onStop "+this.getClass().getSimpleName());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        BeeLog.debug(LOG_TAG, "onDestroy "+this.getClass().getSimpleName());
    }
}
