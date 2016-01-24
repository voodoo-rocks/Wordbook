package my.beelzik.mobile.wordbook.db.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.db.WordDB;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 02.01.2016.
 */
@Accessors(prefix = "m")
public class DBService extends Service {

    Binder mBinder = new DBBinder();


    private static final String LOG_TAG = "DBService";


    @Getter
    WordDB mWordDB;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BeeLog.debug(LOG_TAG,"DBService onCreate");
        if(mWordDB == null){
            mWordDB = new WordDB(this);
        }


        mWordDB.open();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        BeeLog.debug(LOG_TAG,"DBService onDestroy");
        mWordDB.close();
    }

    public void registerWordDBObserver(WordDB.WordDBObserver observer) {
        mWordDB.registerWordDBObserver(observer);
    }

    public void unregisterWordDBObserver(WordDB.WordDBObserver observer) {
        mWordDB.unregisterWordDBObserver(observer);
    }



    public class DBBinder extends Binder{

        public DBService getService(){
            return DBService.this;
        }

    }
}
