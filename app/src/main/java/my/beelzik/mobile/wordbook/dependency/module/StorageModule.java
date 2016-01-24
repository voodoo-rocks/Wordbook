package my.beelzik.mobile.wordbook.dependency.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import my.beelzik.mobile.wordbook.storage.UserConfig;

/**
 * Created by Andrey on 14.01.2016.
 */
@Module
public class StorageModule {

    @Provides @Singleton
    public UserConfig provideUserConfig(Context context){
        return new UserConfig(context);
    }
}
