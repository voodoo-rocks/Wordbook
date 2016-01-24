package my.beelzik.mobile.wordbook.ui.activity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import butterknife.Bind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.ui.widget.empty.EmptyViewHolder;

/**
 * Created by Andrey on 05.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseListActivity extends BaseToolbarActivity {


    @Bind(R.id.list_view)
    @Getter(AccessLevel.PROTECTED)
    ListView mListView;

    @Bind(R.id.empty_view)
    @Getter(AccessLevel.PROTECTED)
    RelativeLayout mEmptyView;

    @Bind(R.id.coordinator_layout)
    @Getter(AccessLevel.PROTECTED)
    CoordinatorLayout mCoordinatorLayout;

    @Getter(AccessLevel.PROTECTED)
    EmptyViewHolder mEmptyViewHolder;


    ListAdapter mAdapter;

    @Override
    protected int getContentViewLayoutRes() {
        return R.layout.activity_list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = createListAdapter();
        mListView.setAdapter(mAdapter);

        mEmptyViewHolder = new EmptyViewHolder(mEmptyView);

        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmptyState();
            }
        });

        checkEmptyState();
    }

    protected abstract ListAdapter createListAdapter();

    private void checkEmptyState() {
        mEmptyViewHolder.setVisibility(mAdapter.getCount() == 0);
    }
}
