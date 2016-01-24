package my.beelzik.mobile.wordbook.ui.widget.word;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.db.WordDB;
import my.beelzik.mobile.wordbook.utils.BeeLog;

/**
 * Created by Andrey on 18.01.2016.
 */
@Accessors(prefix = "m")
public class SelectQuizWordAdapter extends BaseWordAdapter  {

    public SelectQuizWordAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    protected BaseWordSelector createAdapterSelector() {
        return new DifferentTypeSelector();
    }

    public DifferentTypeSelector getDifferentTypeSelector(){
        return ((DifferentTypeSelector) getAdapterSelector());
    }


    @Accessors(prefix = "m")
    public class DifferentTypeSelector extends BaseWordSelector{

        @Getter Set<Long> mSelectedDifferenceWordSet = new HashSet<>();

        @Getter @Setter WordDB.DifferentType mSelectedDifferentType = WordDB.DifferentType.EXACTLY;

        @Override
        public void clearSelection() {
            mSelectedDifferenceWordSet.clear();
        }

        @Override
        public boolean checkWordSelected(long id){

            switch (mSelectedDifferentType){
                case NONE:
                    return true;
                case EXCEPT:
                    return mSelectedDifferenceWordSet == null || !mSelectedDifferenceWordSet.contains(id);
                case EXACTLY:
                default:
                    return mSelectedDifferenceWordSet != null && mSelectedDifferenceWordSet.contains(id);
            }
        }



        @Override
        public void changeSelectionState(int wordPosition, long wordId, WordCell cell) {
            cell.setActivated(!cell.isActivated());

            if (mSelectedDifferentType == WordDB.DifferentType.NONE) {
                mSelectedDifferentType = WordDB.DifferentType.EXCEPT;
            }
            if (mSelectedDifferenceWordSet.contains(cell.getWordId())) {
                mSelectedDifferenceWordSet.remove(cell.getWordId());
            } else {
                mSelectedDifferenceWordSet.add(cell.getWordId());
            }
            if (mSelectedDifferentType == WordDB.DifferentType.EXCEPT && mSelectedDifferenceWordSet.size() == getCount()) {
                mSelectedDifferentType = WordDB.DifferentType.EXACTLY;
                mSelectedDifferenceWordSet.clear();
            }

            if (mSelectedDifferentType == WordDB.DifferentType.EXCEPT && mSelectedDifferenceWordSet.size() == 0) {
                mSelectedDifferentType = WordDB.DifferentType.NONE;
                mSelectedDifferenceWordSet.clear();
            }

            BeeLog.debug("mDifferentType: "+mSelectedDifferentType.name()+" mSelectedDifferenceWordSet.size(): "+mSelectedDifferenceWordSet.size() +" getCount(): "+getCount());
            if (mSelectedDifferentType == WordDB.DifferentType.EXACTLY && mSelectedDifferenceWordSet.size() == getCount()) {
                mSelectedDifferentType = WordDB.DifferentType.NONE;
                mSelectedDifferenceWordSet.clear();
            }

        }

    }
}
