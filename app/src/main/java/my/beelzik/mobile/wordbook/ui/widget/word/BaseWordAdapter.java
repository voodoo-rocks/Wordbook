package my.beelzik.mobile.wordbook.ui.widget.word;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.db.WordDB;
import my.beelzik.mobile.wordbook.db.table.BaseColumns;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 02.01.2016.
 */
@Accessors(prefix = "m")
public abstract class BaseWordAdapter extends CursorAdapter {

    List<WordCell> mWordCellList = new ArrayList<>();

    BaseWordSelector mBaseWordAdapterSelector;


    public interface OnWordItemClickListener{
        void onWordItemClick(int position, long id, WordCell cell);
    }

    private OnWordItemClickListener mOnWordItemClickListener;

    public void setOnWordItemClickListener(OnWordItemClickListener onWordItemClickListener) {
        mOnWordItemClickListener = onWordItemClickListener;
    }

    public BaseWordAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        mBaseWordAdapterSelector = createAdapterSelector();
    }

    protected abstract BaseWordSelector createAdapterSelector();


    protected BaseWordSelector getAdapterSelector(){
        return mBaseWordAdapterSelector;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        WordCell view =  new WordCell(context);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordCell cell = (WordCell) v;
                mBaseWordAdapterSelector.changeSelectionState(cell.getWordPosition(), cell.getWordId(), cell);
                if (mOnWordItemClickListener != null) {
                    mOnWordItemClickListener.onWordItemClick(cell.getWordPosition(), cell.getWordId(), cell);
                }
            }
        });
        mWordCellList.add(view);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((WordCell) view).bindData(cursor);
        ((WordCell) view).forceActivate(checkWordSelected(cursor.getLong(cursor.getColumnIndex(BaseColumns.ID))));
    }

    public void refreshSelection(){
        mBaseWordAdapterSelector.refreshSelection();
    }

    protected boolean checkWordSelected(long id){
       return mBaseWordAdapterSelector.checkWordSelected(id);
    }



    protected abstract class BaseWordSelector{

        public abstract boolean checkWordSelected(long id);

        public abstract void clearSelection();

        public abstract void changeSelectionState(int wordPosition, long wordId, WordCell cell);

        public void refreshSelection(){
            for (WordCell cell : mWordCellList) {
                cell.setActivated(checkWordSelected(cell.getWordId()));
            }
        }

    }


}
