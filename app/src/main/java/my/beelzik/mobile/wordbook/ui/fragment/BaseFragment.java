package my.beelzik.mobile.wordbook.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.App;
import my.beelzik.mobile.wordbook.storage.UserConfig;

/**
 * Created by Andrey on 13.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseFragment extends LifeCycleFragment {

    @Inject
    @Getter(AccessLevel.PROTECTED)
    UserConfig mUserConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayoutRes(),container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    protected abstract @LayoutRes int getContentLayoutRes();
}
