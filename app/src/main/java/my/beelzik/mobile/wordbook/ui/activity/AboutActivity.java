package my.beelzik.mobile.wordbook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import my.beelzik.mobile.wordbook.R;

/**
 * Created by Andrey on 24.01.2016.
 */
//TODO сделать этот экран
public class AboutActivity extends BaseToolbarActivity {


    public static void openAboutActivity(Context context ){
        Intent intent = new Intent(context,AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserConfig().setAppOpenedFirstTime();
    }

    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activity_about;
    }
}
