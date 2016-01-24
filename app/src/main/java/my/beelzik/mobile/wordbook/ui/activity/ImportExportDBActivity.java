package my.beelzik.mobile.wordbook.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.utils.ActivityAnimationUtils;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 19.01.2016.
 */
//TODO Доделать
public class ImportExportDBActivity extends BaseToolbarActivity {

    @Bind(R.id.importDb)
    Button mImportDb;

    @Bind(R.id.exportDb)
    Button mExportDb;

    public static void openImportExportDBActivity(Activity activity) {
        Intent intent = new Intent(activity, ImportExportDBActivity.class);
        activity.startActivity(intent);
        ActivityAnimationUtils.animateActivityStart(activity);
    }

    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activity_import_export_db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImportDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryImportDb();
            }
        });

        mExportDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryExportDb();
            }
        });
    }

    //TODO доделать проверки на наличие файла/ фейла импорта
   private void tryImportDb(){

       if(isDBServiceConnected()){
               getDBService().getWordDB().importDatabase(Environment.getExternalStorageDirectory() + File.separator + "WORD_DB_BACKUP.db");

       }else{

       }
   }

    //TODO проверки на фейл экспорта
    private void tryExportDb(){
        if(isDBServiceConnected()){
                getDBService().getWordDB().exportDatabase("WORD_DB_BACKUP.db");
        }else{

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            ActivityAnimationUtils.animateActivityClose(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSetupToolbar(ActionBar supportActionBar) {
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }


}
