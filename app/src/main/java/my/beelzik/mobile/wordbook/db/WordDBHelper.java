package my.beelzik.mobile.wordbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import my.beelzik.mobile.wordbook.db.table.DictionaryTable;
import my.beelzik.mobile.wordbook.utils.BeeLog;
import my.beelzik.mobile.wordbook.utils.FileUtils;

/**
 * Created by Andrey on 02.01.2016.
 */
public class WordDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "wordbook.db";

    public static final int DB_VERSION = 1;

    Context mContext;


    public WordDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     */
    public boolean importDatabase(String dbPath) {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        try {
            close();
            File newDb = new File(dbPath);
            File oldDb = mContext.getDatabasePath(DB_NAME);
            BeeLog.debug("newDb dbPath: " + dbPath);
            BeeLog.debug("newDb exists: " + newDb.exists());
            if (newDb.exists()) {

                FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));

                // Access the copied database so SQLiteHelper will cache it and mark
                // it as created.

                getWritableDatabase().close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     */
    public boolean exportDatabase(String name) {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        try {
            File oldDb = mContext.getDatabasePath(DB_NAME);
            File backupDb = new File(Environment.getExternalStorageDirectory(), name);

            if (backupDb.exists()) {
                backupDb.delete();
            }
            BeeLog.debug("exportDatabase: oldDb.exists() ? " + oldDb.exists());

            if (oldDb.exists()) {

                BeeLog.debug("exportDatabase: oldDb path " + oldDb.getAbsolutePath());
                BeeLog.debug("context class " + mContext.getClass().getSimpleName());
                FileUtils.copyFile(new FileInputStream(oldDb), new FileOutputStream(backupDb));
                // Access the copied database so SQLiteHelper will cache it and mark
                // it as created.

                BeeLog.debug("exportDatabase success: backupDb.exists(): "+backupDb.exists());
                BeeLog.debug("backupDb.getAbsolutePath: " + backupDb.getAbsolutePath());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        BeeLog.debug("onCreate DATABASE <==========================================================================");
        DictionaryTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        BeeLog.debug("onUpgrade DATABASE <==========================================================================");
    }
}
