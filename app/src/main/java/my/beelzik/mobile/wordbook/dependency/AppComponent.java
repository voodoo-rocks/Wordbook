package my.beelzik.mobile.wordbook.dependency;

import javax.inject.Singleton;

import dagger.Component;
import my.beelzik.mobile.wordbook.dependency.module.AppModule;
import my.beelzik.mobile.wordbook.dependency.module.StorageModule;
import my.beelzik.mobile.wordbook.ui.activity.BaseActivity;
import my.beelzik.mobile.wordbook.ui.activity.SelectQuizWordListActivity;
import my.beelzik.mobile.wordbook.ui.fragment.BaseFragment;

/**
 * Created by Andrey on 14.01.2016.
 */
@Singleton
@Component(modules = {
        AppModule.class,
        StorageModule.class
})
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

}
