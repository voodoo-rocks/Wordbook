package my.beelzik.mobile.wordbook.ui.widget.word;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by Andrey on 18.01.2016.
 */
@Accessors(prefix = "m")
public class ExactlySelectWordAdapter extends BaseWordAdapter {

    public ExactlySelectWordAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    protected BaseWordSelector createAdapterSelector() {
        return new ExactlySelector();
    }

    public ExactlySelector getExactlySelector(){
        return ((ExactlySelector) getAdapterSelector());
    }


    @Accessors(prefix = "m")
    public class ExactlySelector extends BaseWordSelector{

        @Getter Set<Long> mSelectedWordIdSet = new HashSet<>();
        @Getter Set<Integer> mSelectedWordPositionSet = new HashSet<>();

        @Override
        public void clearSelection(){
            mSelectedWordIdSet.clear();
            mSelectedWordPositionSet.clear();
        }

        @Override
        public boolean checkWordSelected(long id) {
            return mSelectedWordIdSet.contains(id);
        }

        @Override
        public void changeSelectionState(int wordPosition, long wordId, WordCell cell) {
            cell.setActivated(!cell.isActivated());
            if (mSelectedWordIdSet.contains(cell.getWordId())) {
                mSelectedWordIdSet.remove(cell.getWordId());
            } else {
                mSelectedWordIdSet.add(cell.getWordId());
            }

            if (mSelectedWordPositionSet.contains(cell.getWordPosition())) {
                mSelectedWordPositionSet.remove(cell.getWordPosition());
            } else {
                mSelectedWordPositionSet.add(cell.getWordPosition());
            }

        }
    }
}
