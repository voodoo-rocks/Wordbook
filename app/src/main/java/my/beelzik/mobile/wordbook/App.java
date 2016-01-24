package my.beelzik.mobile.wordbook;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.dependency.AppComponent;

import my.beelzik.mobile.wordbook.dependency.DaggerAppComponent;
import my.beelzik.mobile.wordbook.dependency.module.AppModule;
import my.beelzik.mobile.wordbook.dependency.module.StorageModule;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 02.01.2016.
 */
@Accessors(prefix = "m")
public class App extends Application {


    @Getter AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();


        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .storageModule(new StorageModule())
                        .build();
        parseHundredVerbsToJson();

    }


    private void parseHundredVerbsToJson(){

        try {
            Pattern pattern = Pattern.compile("(.+),(.+)");
            Matcher matcher;
            BufferedReader reader = new BufferedReader( new InputStreamReader(getResources().getAssets().open("hundred_verds.txt")));
            String line = null;
            int i = 1;
            while ((line = reader.readLine()) != null){
                matcher = pattern.matcher(line);
                if(matcher.find()){
                    i++;
                    BeeLog.debug("group(0): "+matcher.group(0) + " group(1): "+matcher.group(1));
                }
            }
            BeeLog.debug("TOTAL: "+i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static AppComponent getAppComponent(Context context){
        return ((App) context.getApplicationContext()).mAppComponent;
    }

}
