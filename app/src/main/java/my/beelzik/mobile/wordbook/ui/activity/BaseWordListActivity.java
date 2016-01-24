package my.beelzik.mobile.wordbook.ui.activity;

import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;

import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.db.WordDB;
import my.beelzik.mobile.wordbook.db.table.DictionaryTable;
import my.beelzik.mobile.wordbook.ui.widget.word.BaseWordAdapter;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 05.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseWordListActivity extends BaseCursorLoadListActivity implements WordDB.WordDBObserver {


    protected static final int DEFAULT_WORD_LOADER = 100;

    private boolean mWordDBChanged = false;

    @Getter
    boolean mActivityResumed = false;
    private MenuItem mSortMenuDropdown;


    @Override
    public int getCursorLoaderId() {
        return DEFAULT_WORD_LOADER;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        getDBService().registerWordDBObserver(this);
        checkDataUpdates();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        super.onServiceDisconnected(name);
        mWordDBChanged = true;
        BeeLog.debug("nWordDBChanged", "onServiceDisconnected");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityResumed = true;
        checkDataUpdates();

    }

    private void checkDataUpdates(){
        if(isDBServiceConnected()){
            if(mWordDBChanged){
                loadChangedData();
            }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        mActivityResumed = false;

    }




    @Override
    protected CursorAdapter createCursorAdapter() {
        return createWordAdapter();
    }

    protected abstract BaseWordAdapter createWordAdapter();

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {

                if(isDBServiceConnected()){
                    return getDBService().getWordDB().getAllWord(getUserConfig().getSortType());
                }
                return null;

            }
        };
    }

    public void loadChangedData(){
        BeeLog.debug("loadChangedData");
        restartDefaultLoader();
        mWordDBChanged = false;
    }

    @Override
    public void onWordDBChanged() {
        BeeLog.debug("nWordDBChanged","BASEWORDLISTACTIVITY onWordDBChanged");
        if(isActivityResumed()){
            loadChangedData();
        }else{
            mWordDBChanged = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mSortMenuDropdown =  menu.findItem(R.id.menu_group_sort);
        initSortMenuDropdownTitle();
        return true;
    }

    private void initSortMenuDropdownTitle(){
        switch (getUserConfig().getSortType()){
            case FROM_A_TO_Z_NATIVE:
                mSortMenuDropdown.setTitle(R.string.sort_native_az);
                break;
            case FROM_Z_TO_A_NATIVE:
                mSortMenuDropdown.setTitle(R.string.sort_native_za);
                break;
            case FROM_A_TO_Z_LEARN:
                mSortMenuDropdown.setTitle(R.string.sort_learn_az);
                break;
            case FROM_Z_TO_A_LEARN:
                mSortMenuDropdown.setTitle(R.string.sort_learn_za);
                break;
            case DATE_ASC:
                mSortMenuDropdown.setTitle(R.string.sort_adding_asc);
                break;
            case DATE_DESC:
            default:
                mSortMenuDropdown.setTitle(R.string.sort_adding_desc);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getGroupId() == R.id.group_sort){
            WordDB.SortType currentSortType = getUserConfig().getSortType();
            WordDB.SortType selectedSortType;
            switch (item.getItemId()){
                case R.id.sort_native_az:
                    selectedSortType = WordDB.SortType.FROM_A_TO_Z_NATIVE;
                    break;
                case R.id.sort_native_za:
                    selectedSortType = WordDB.SortType.FROM_Z_TO_A_NATIVE;
                    break;
                case R.id.sort_learn_az:
                    selectedSortType = WordDB.SortType.FROM_A_TO_Z_LEARN;
                    break;
                case R.id.sort_learn_za:
                    selectedSortType = WordDB.SortType.FROM_Z_TO_A_LEARN;
                    break;
                case R.id.sort_date:
                    selectedSortType = WordDB.SortType.DATE_ASC;
                    break;
                case R.id.sort_date_invert:
                default:
                    selectedSortType = WordDB.SortType.DATE_DESC;
                    break;
            }
            BeeLog.debug("selectedSortType: "+selectedSortType.name());
            if(currentSortType != selectedSortType){
                mSortMenuDropdown.setTitle(item.getTitle());
                getUserConfig().setSortType(selectedSortType);
                onSelectSortType();
                BeeLog.debug("reload with new sort: " + selectedSortType.name());
            }
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    protected void onSelectSortType(){
       restartDefaultLoader();
    }
}
