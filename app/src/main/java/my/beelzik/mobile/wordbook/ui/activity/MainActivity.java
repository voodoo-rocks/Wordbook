package my.beelzik.mobile.wordbook.ui.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.db.AsyncWordDBWriter;
import my.beelzik.mobile.wordbook.db.table.DictionaryTable;
import my.beelzik.mobile.wordbook.utils.ActivityAnimationUtils;
import my.beelzik.mobile.wordbook.utils.BeeLog;

public class MainActivity extends LifeCycleActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    TextView mTotalWordsNavigationLabel;



    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
                startActivity(intent);
                ActivityAnimationUtils.animateActivityStart(MainActivity.this);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        mTotalWordsNavigationLabel = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.totalWordsNavigationLabel);
        mNavigationView.setNavigationItemSelectedListener(this);

        if(getUserConfig().isAppOpenFirstTime()){
            AboutActivity.openAboutActivity(this);
        }

    }



    private void showInsertHundredVerbsSolutionDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.dialog_ttl_hundred_verbs);
        adb.setMessage(R.string.dialog_msg_hundred_verbs);
        adb.setPositiveButton(R.string.yes_insert, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertHundredVerbs();
            }
        });
        adb.setNegativeButton(R.string.no_do_not, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO  даже по нажатию на нет слова встаятся ¯\_(ツ)_/¯
                insertHundredVerbs();

            }
        });
        adb.show();
    }

    private void insertHundredVerbs(){
        if(isDBServiceConnected()){
            getDBService().getWordDB().insertHundredVerbs(new AsyncWordDBWriter.InsertCallback() {
                @Override
                public void insertSuccess(Long id) {
                    getUserConfig().setInsertHundredVerbsSolved();
                    Snackbar.make(getCoordinatorLayout(), getString(R.string.hundred_verbs_inserted), Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void insertFailed() {
                    Snackbar.make(getCoordinatorLayout(),getString(R.string.insert_failed),Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    insertHundredVerbs();
                                }
                            }).show();
                }
            });
        }else{
            insertHundredVerbs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mFab.isShown()){
            mFab.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFab.show();
                }
            }, 500);
        }

        if(!getUserConfig().isAppOpenFirstTime() && !getUserConfig().isInsertHundredVerbsSolved()){
            showInsertHundredVerbsSolutionDialog();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == DEFAULT_WORD_LOADER && data != null){
            mTotalWordsNavigationLabel.setText(String.format(getString(R.string.total_words_count),data.getCount()));
        }
        super.onLoadFinished(loader, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFab.hide();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        BeeLog.debug("onNavigationItemSelected ");
        if (id == R.id.nav_gallery) {
            QuizActivity.openQuizActivity(this);

        } else if (id == R.id.nav_export_import) {
            ImportExportDBActivity.openImportExportDBActivity(this);

        } else if (id == R.id.nav_about) {
            AboutActivity.openAboutActivity(this);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
