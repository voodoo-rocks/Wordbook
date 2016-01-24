package my.beelzik.mobile.wordbook.ui.widget.quiz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.R;
import my.beelzik.mobile.wordbook.data.DictionaryData;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 08.01.2016.
 */
@Accessors(prefix = "m")
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.VariantHolder> {

    private static final DictionaryData.WordType DEFAULT_WORD_TYPE = DictionaryData.WordType.NATIVE;
    @Getter
    List<DictionaryData> mData;

    LayoutInflater mInflater;
    Context mContext;

    @Setter @Getter DictionaryData.WordType mWordType = DEFAULT_WORD_TYPE;

    public final static int NO_VARIANT_ITEM_CHECKED = -1;

    @Getter @Setter private int mCheckedVariantItemPosition = NO_VARIANT_ITEM_CHECKED;
    @Getter private DictionaryData mCheckedVariantItemData = null;
    @Getter View mCheckedVariantItemView = null;

    boolean mHasVariantChoiceModeEnable = true;

    public void disableVariantChoiceMode() {
        mHasVariantChoiceModeEnable = false;
    }

    public void enableVariantChoiceMode() {
        mHasVariantChoiceModeEnable = true;
    }

    public interface OnVariantItemClickListener{

        void onVariantItemClick(View itemView, int position, DictionaryData variantData);
    }

    @Setter @Getter OnVariantItemClickListener mOnVariantItemClickListener;


    public QuizAdapter(Context context) {

        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setData(List<DictionaryData> data) {
        mData = data;


    }

    public void clearChoices() {
        mCheckedVariantItemPosition = NO_VARIANT_ITEM_CHECKED;
        mCheckedVariantItemData = null;
    }


    public DictionaryData getDictionaryData(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public VariantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view = mInflater.inflate(R.layout.cell_quiz_variant,parent,false);
        return new VariantHolder(view);
    }

    @Override
    public void onBindViewHolder(VariantHolder holder, int position) {
        holder.mPosition = position;
        if(mWordType == DictionaryData.WordType.NATIVE){
            holder.mVariant.setText(getDictionaryData(position).getNative());
        }else{
            holder.mVariant.setText(getDictionaryData(position).getLearn());
        }
        if(mCheckedVariantItemPosition == holder.mPosition){
            mCheckedVariantItemView =  holder.mItemView;

        }
        holder.mItemView.setActivated(mCheckedVariantItemPosition == holder.mPosition);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }




    protected class VariantHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mVariant;
        View mItemView;

        int mPosition;

        public VariantHolder(View itemView) {
            super(itemView);
            mVariant = (TextView) itemView.findViewById(R.id.variant);

            mItemView = itemView;
            mItemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            BeeLog.debug("VariantHolder onClick: "+mPosition);
            if(mHasVariantChoiceModeEnable){

                mCheckedVariantItemData = getDictionaryData(mPosition);

                if(mCheckedVariantItemPosition != mPosition){
                    if(mCheckedVariantItemView != null){
                        mCheckedVariantItemView.setActivated(false);
                    }
                    mCheckedVariantItemView = mItemView;
                }
                mCheckedVariantItemPosition = mPosition;
                mItemView.setActivated(true);
                BeeLog.debug("mCheckedVariantItemView text: "+ ((TextView) mCheckedVariantItemView.findViewById(R.id.variant)).getText());
                if(mOnVariantItemClickListener != null){
                    mOnVariantItemClickListener.onVariantItemClick(mItemView,mPosition,getDictionaryData(mPosition));
                }
            }

        }
    }

}
