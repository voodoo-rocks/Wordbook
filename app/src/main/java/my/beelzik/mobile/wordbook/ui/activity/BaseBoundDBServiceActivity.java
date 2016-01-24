package my.beelzik.mobile.wordbook.ui.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.db.service.DBService;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 11.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseBoundDBServiceActivity extends BaseActivity implements ServiceConnection {

    @Getter
    DBService mDBService;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DBService.class);
        bindService(intent,this, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        mDBService = null;
    }

    public boolean isDBServiceConnected(){
        return mDBService != null;
    }


    public void onServiceConnected(ComponentName name, IBinder service) {
        mDBService = ((DBService.DBBinder) service).getService();
        BeeLog.debug(BaseBoundDBServiceActivity.this.getClass().getSimpleName() + " onServiceConnected: isDBServiceConnected ? " + isDBServiceConnected());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mDBService = null;
        BeeLog.debug(BaseBoundDBServiceActivity.this.getClass().getSimpleName() + " onServiceDisconnected: isDBServiceConnected ? "+isDBServiceConnected());
    }
}
