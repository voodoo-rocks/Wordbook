package my.beelzik.mobile.wordbook.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import butterknife.ButterKnife;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.App;
import my.beelzik.mobile.wordbook.storage.UserConfig;
import my.beelzik.mobile.wordbook.utils.ActivityAnimationUtils;

/**
 * Created by Andrey on 02.01.2016.
 */
@Accessors(prefix = "m")
public abstract  class BaseActivity extends AppCompatActivity {


    @Inject
    @Getter(AccessLevel.PROTECTED) UserConfig mUserConfig;


    protected abstract @LayoutRes int getContentViewLayoutRes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutRes());
        ButterKnife.bind(this);
        App.getAppComponent(this).inject(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isTaskRoot()){
            ActivityAnimationUtils.animateActivityClose(this);
        }

    }
}
