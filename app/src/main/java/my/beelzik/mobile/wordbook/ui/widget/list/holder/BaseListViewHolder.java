package my.beelzik.mobile.wordbook.ui.widget.list.holder;

import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by Andrey on 03.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseListViewHolder {

    @Getter
    public View mItemView;

    @Getter(AccessLevel.PROTECTED)
    protected Context mContext;

    public BaseListViewHolder(View itemView) {
        mItemView = itemView;


        ButterKnife.bind(this, mItemView);
        mItemView.setTag(this);


        mContext = mItemView.getContext();
    }


}
