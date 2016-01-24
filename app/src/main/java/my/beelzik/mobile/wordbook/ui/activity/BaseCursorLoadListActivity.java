package my.beelzik.mobile.wordbook.ui.activity;

import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 05.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseCursorLoadListActivity extends BaseListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Getter(AccessLevel.PROTECTED)
    CursorAdapter mCursorAdapter;


    private int loaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getLoaderManager().getLoader(getCursorLoaderId()) == null){
            getLoaderManager().initLoader(getCursorLoaderId(), null, this);
        }

    }

    @Override
    protected ListAdapter createListAdapter() {
        mCursorAdapter = createCursorAdapter();
        return mCursorAdapter;
    }

    public abstract int getCursorLoaderId();

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (mCursorAdapter.getCount() == 0) {
            firstConnectionLoad();
        }
    }

    protected void firstConnectionLoad(){
        restartDefaultLoader();
    }

    protected void restartDefaultLoader() {
        getLoaderManager().restartLoader(getCursorLoaderId(), null, this);
    }

    protected abstract CursorAdapter createCursorAdapter();





    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null){
            mCursorAdapter.swapCursor(data);
            mCursorAdapter.notifyDataSetChanged();
        }

        BeeLog.debug("onLoadFinished data: " + (data == null ? null : data.getCount()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
