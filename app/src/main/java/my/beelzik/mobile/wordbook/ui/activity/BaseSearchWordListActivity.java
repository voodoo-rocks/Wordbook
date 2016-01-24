package my.beelzik.mobile.wordbook.ui.activity;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.ui.widget.word.BaseWordAdapter;

/**
 * Created by Andrey on 22.01.2016.
 */
public abstract class BaseSearchWordListActivity extends BaseWordListActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String SAVE_STATE_SEARCH_QUERY = "save_state_search_query";


    private static final int SEARCH_LOADER = 300;
    SearchView mSearchView;
    private String mSearchQuery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SEARCH_LOADER, null, this);
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mSearchQuery = savedInstanceState.getString(SAVE_STATE_SEARCH_QUERY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.test, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();

        if(!TextUtils.isEmpty(mSearchQuery)){
            mSearchView.setQuery(mSearchQuery,false);
            mSearchView.setIconified(false);
        }

        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnCloseListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        cancelSearch();
        return false;
    }

    @Override
    protected void onSelectSortType() {
        if(mSearchQuery != null){
            restartSearchLoader();
        }else{
            super.onSelectSortType();
        }

    }

    @Override
    protected void firstConnectionLoad() {
        if(mSearchQuery != null){
            restartSearchLoader();
        }else{
            super.firstConnectionLoad();
        }

    }

    @Override
    public void loadChangedData() {
        if(mSearchQuery != null){
            restartSearchLoader();
        }else{
            super.loadChangedData();
        }

    }

    private void cancelSearch(){
        mSearchQuery = null;
        restartDefaultLoader();
    }

    private void search(String query){
        mSearchQuery = query;
        restartSearchLoader();
    }

    private void restartSearchLoader() {
        getLoaderManager().restartLoader(SEARCH_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == SEARCH_LOADER){

            return new CursorLoader(this){

                @Override
                public Cursor loadInBackground() {
                    if(isDBServiceConnected()){
                        return getDBService().getWordDB().getAllWordLike(getUserConfig().getSortType(), mSearchQuery);
                    }
                    return null;

                }
            };
        }
        return super.onCreateLoader(id, args);
    }


}
