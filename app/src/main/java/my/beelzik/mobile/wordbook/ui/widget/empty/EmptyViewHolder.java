package my.beelzik.mobile.wordbook.ui.widget.empty;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.ui.widget.list.holder.BaseListViewHolder;

/**
 * Created by Andrey on 03.01.2016.
 */
public class EmptyViewHolder extends BaseListViewHolder {

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    @Bind(R.id.empty_image)
    ImageView mEmptyImage;


    public EmptyViewHolder(View itemView) {
        super(itemView);
    }


    public void setEmptyImageDrawable(Drawable drawable){
        mEmptyImage.setImageDrawable(drawable);
    }

    public void setEmptyImageBitmap(Bitmap bitmap){
        mEmptyImage.setImageBitmap(bitmap);
    }

    public void setEmptyText(CharSequence text){
        mEmptyText.setText(text);
    }


    public void setVisibility(boolean visible){
        if(visible){
            getItemView().setVisibility(View.VISIBLE);
        }else{
            getItemView().setVisibility(View.GONE);
        }

    }
}
