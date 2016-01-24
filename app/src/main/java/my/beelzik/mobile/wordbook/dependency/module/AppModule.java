package my.beelzik.mobile.wordbook.dependency.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import my.beelzik.mobile.wordbook.App;

/**
 * Created by Andrey on 14.01.2016.
 */
@Module
public class AppModule {

    App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides
    public App provideApp(){
        return mApp;
    }


    @Provides
    public Context provideAppContext(){
        return mApp;
    }

}
