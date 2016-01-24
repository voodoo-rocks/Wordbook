package my.beelzik.mobile.wordbook.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 12.01.2016.
 */
public class LifeCycleFragment extends Fragment{

    private static final String LOG_TAG = "LifeCycleFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BeeLog.debug(LOG_TAG, "onAttach "+this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeeLog.debug(LOG_TAG, "onCreate "+this.getClass().getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BeeLog.debug(LOG_TAG, "onCreateView "+this.getClass().getSimpleName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        BeeLog.debug(LOG_TAG, "onViewCreated "+this.getClass().getSimpleName());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        BeeLog.debug(LOG_TAG, "onActivityCreated "+this.getClass().getSimpleName());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        BeeLog.debug(LOG_TAG, "onStart "+this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();

        BeeLog.debug(LOG_TAG, "onResume "+this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();

        BeeLog.debug(LOG_TAG, "onPause "+this.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();

        BeeLog.debug(LOG_TAG, "onStop "+this.getClass().getSimpleName());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        BeeLog.debug(LOG_TAG, "onDestroy "+this.getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BeeLog.debug(LOG_TAG, "onDetach "+this.getClass().getSimpleName());
    }
}
