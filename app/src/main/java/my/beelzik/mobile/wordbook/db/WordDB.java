package my.beelzik.mobile.wordbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.Observable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.data.DictionaryData;
import my.beelzik.mobile.wordbook.db.dao.DictionaryDAO;
import my.beelzik.mobile.wordbook.db.table.BaseColumns;
import my.beelzik.mobile.wordbook.db.table.DictionaryTable;
import my.beelzik.mobile.wordbook.utils.BeeLog;
import my.beelzik.mobile.wordbook.utils.DBUtils;

/**
 * Created by Andrey on 02.01.2016.
 */
@Accessors(prefix = "m")
public class WordDB implements DictionaryDAO {



    private Context mContext;
    protected WordDBHelper mHelper;

    private SQLiteDatabase mDb;

    private AsyncWordDBWriter mAsyncWordDBWriter;
    private WordDBObservable mWordDBObservable;


    private IDictionaryDAO mIDictionaryDAO;

    public WordDB(Context context) {
        mContext = context;
        mHelper = new WordDBHelper(context);
        mWordDBObservable = new WordDBObservable();
        mAsyncWordDBWriter = new AsyncWordDBWriter(mWordDBObservable);
        mIDictionaryDAO = new IDictionaryDAO();
    }


    public enum DifferentType{
        EXACTLY ,
        EXCEPT ,
        NONE
    }

    public enum SortType{
        FROM_A_TO_Z_NATIVE,
        FROM_Z_TO_A_NATIVE,
        FROM_A_TO_Z_LEARN,
        FROM_Z_TO_A_LEARN,
        DATE_ASC,
        DATE_DESC
    }


    public void registerWordDBObserver(WordDB.WordDBObserver observer) {

        mWordDBObservable.registerObserver(observer);
    }

    public void unregisterWordDBObserver(WordDB.WordDBObserver observer) {
        mWordDBObservable.unregisterObserver(observer);
    }

    public synchronized void open(){

            mDb =  mHelper.getWritableDatabase();


    }

    public synchronized void close(){

            mHelper.close();

    }

    public boolean importDatabase(String path)  {
        if(mHelper.importDatabase(path)){
            open();
            mWordDBObservable.notifyChanged();
            return true;
        }

       return false;
    }

    public void insertHundredVerbs(AsyncWordDBWriter.InsertCallback callback){

        mAsyncWordDBWriter.insetRow(new AsyncWordDBWriter.InsertAction() {
            @Override
            public Long insert() {

                List<ContentValues> valuesList = new ArrayList<ContentValues>(100);
                try {
                    Pattern pattern = Pattern.compile("(.+),(.+)");
                    Matcher matcher;
                    BufferedReader reader = new BufferedReader( new InputStreamReader(mContext.getResources().getAssets().open("hundred_verds.txt")));
                    String line = null;
                    int i = 1;
                    while ((line = reader.readLine()) != null){
                        matcher = pattern.matcher(line);
                        if(matcher.find()){
                            ContentValues cv = new ContentValues();
                            cv.put(DictionaryTable.Columns.NATIVE,matcher.group(2).toLowerCase());
                            cv.put(DictionaryTable.Columns.LEARN,matcher.group(1).toLowerCase());
                            cv.put(DictionaryTable.Columns.NATIVE_RATING,50);
                            cv.put(DictionaryTable.Columns.LEARN_RATING, 50);
                            valuesList.add(cv);
                        }
                    }
                    BeeLog.debug("TOTAL: "+valuesList.size());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mDb.beginTransaction();
                long resultId = -1;
                try{

                    for ( ContentValues cv : valuesList ) {
                        resultId =  mDb.insert(DictionaryTable.NAME,null,cv);
                        BeeLog.debug("resultId: "+resultId);
                    }
                    mDb.setTransactionSuccessful();
                }finally {
                    mDb.endTransaction();
                }
                return resultId;
            }
        },callback);
    }

    public boolean exportDatabase(String name){
        return mHelper.exportDatabase(name);
    }

    public Cursor getAllWord(SortType type){
       return mDb.query(DictionaryTable.NAME, null, null, null, null, null, orderBySorType(type));
    }

    private String orderBySorType(SortType type){
        if(type == null){
            return null;
        }

        BeeLog.debug("orderBySorType: "+type.name());
        StringBuilder order = new StringBuilder();
        switch (type){
            case FROM_A_TO_Z_NATIVE:
                order.append(DictionaryTable.Columns.NATIVE);
                order.append(" ASC ");
                break;
            case FROM_Z_TO_A_NATIVE:
                order.append(DictionaryTable.Columns.NATIVE);
                order.append(" DESC ");
                break;
            case FROM_A_TO_Z_LEARN:
                order.append(DictionaryTable.Columns.LEARN);
                order.append(" ASC ");
                break;
            case FROM_Z_TO_A_LEARN:
                order.append(DictionaryTable.Columns.LEARN);
                order.append(" DESC ");
                break;
            case DATE_ASC:
                order.append(DictionaryTable.Columns.ID);
                order.append(" ASC ");
                break;
            case DATE_DESC:
            default:
                order.append(DictionaryTable.Columns.ID);
                order.append(" DESC ");
                break;
        }
        return order.toString();
    }

    public Cursor getAllWordLike(SortType type, String likeWut){


        if(!TextUtils.isEmpty(likeWut)){
            StringBuilder where  = new StringBuilder();
            where.append(DictionaryTable.Columns.NATIVE);
            where.append(" LIKE ? ");
            where.append(" OR ");
            where.append(DictionaryTable.Columns.LEARN);
            where.append(" LIKE ? ");

            String[] args = new String[2];
            args[0] = "%"+likeWut+"%";
            args[1] = "%"+likeWut+"%";
            return mDb.query(DictionaryTable.NAME, null, where.toString(), args, null, null, orderBySorType(type));
        }else{
            return getAllWord(type);
        }


    }


 /*   public Cursor getAllWord(){

        return getAllWord(null);
    }*/

    public Cursor getRandomWords(int count, DifferentType wordSelectionType, Set<Long> differentSet){

        StringBuilder sb = new StringBuilder(120);

        sb.append("SELECT * FROM ");
        sb.append(DictionaryTable.NAME);

        if(wordSelectionType != null && wordSelectionType != DifferentType.NONE  && differentSet != null && differentSet.size() > 0){
            sb.append(" WHERE ");
            sb.append(BaseColumns.ID);

            if(wordSelectionType == DifferentType.EXACTLY){
                sb.append(" IN ");
                sb.append(DBUtils.getSqlNumberSet(differentSet));
            }else{
                sb.append(" NOT IN ");
                sb.append(DBUtils.getSqlNumberSet(differentSet));
            }
        }


        sb.append(" ORDER BY RANDOM() LIMIT ");
        sb.append(count);
        BeeLog.debug("getRandomWords SQL: " + sb.toString());

        return  mDb.rawQuery(sb.toString(), null);
    }

    public void insertWord(final String nativePerformance,final String learnPerformance, AsyncWordDBWriter.InsertCallback callback){
        mAsyncWordDBWriter.insetRow(new AsyncWordDBWriter.InsertAction() {
            @Override
            public Long insert() {
                ContentValues cv = new ContentValues();
                cv.put(DictionaryTable.Columns.NATIVE, nativePerformance);
                cv.put(DictionaryTable.Columns.LEARN, learnPerformance);

                cv.put(DictionaryTable.Columns.NATIVE_RATING, 50);
                cv.put(DictionaryTable.Columns.LEARN_RATING, 50);

                return mDb.insert(DictionaryTable.NAME, null, cv);
            }
        }, callback);
    };

    public void deleteWords(final Long[] ids,AsyncWordDBWriter.UpdateDeleteCallback callback) {

        mAsyncWordDBWriter.updateDelete(new AsyncWordDBWriter.UpdateDeleteAction() {
            @Override
            public Integer modify() {
                BeeLog.debug("DBUtils.getSqlNumberSet " + DBUtils.getSqlNumberSet(ids));
                return mDb.delete(DictionaryTable.NAME, DictionaryTable.Columns.ID + " in " + DBUtils.getSqlNumberSet(ids), null);
            }
        }, callback);
    }

    public void editWord(final Long id , final String nativePerformance,final String learnPerformance, AsyncWordDBWriter.UpdateDeleteCallback callback) {

        mAsyncWordDBWriter.updateDelete(new AsyncWordDBWriter.UpdateDeleteAction() {
            @Override
            public Integer modify() {

                ContentValues cv = new ContentValues();
                cv.put(DictionaryTable.Columns.NATIVE, nativePerformance);
                cv.put(DictionaryTable.Columns.LEARN, learnPerformance);
                return mDb.update(DictionaryTable.NAME, cv, DictionaryTable.Columns.ID + " = " + id, null);
            }
        }, callback);
    }


    public void updateWordRating(final long wordId,final int difference,final DictionaryData.WordType type, AsyncWordDBWriter.UpdateDeleteCallback callback){


       mAsyncWordDBWriter.updateDelete(new AsyncWordDBWriter.UpdateDeleteAction() {
           @Override
           public Integer modify() {
               if(type == null){
                   throw new RuntimeException("DictionaryData.WordType type may not be null");
               }

               Cursor cursor =  mDb.query(DictionaryTable.NAME, null, DictionaryTable.Columns.ID + " = " + wordId, null, null, null, null, null);
               cursor.moveToFirst();
               BeeLog.debug("updateWordRating" +
                       " \nword(native): "+cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE))+
                               " \nword(learn): "+cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.LEARN))+
                               " \nnative rating: "+cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE_RATING))+
                               " \nlearn rating: "+cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.LEARN_RATING))
               );
               cursor.close();
               mDb.acquireReference();
               try {
                   StringBuilder sql = new StringBuilder(120);
                   sql.append("UPDATE ");

                   sql.append(DictionaryTable.NAME);
                   sql.append(" SET ");

                   String changeColumn;
                   if(type == DictionaryData.WordType.NATIVE){
                       changeColumn = DictionaryTable.Columns.NATIVE_RATING;
                   }else {
                       changeColumn = DictionaryTable.Columns.LEARN_RATING;
                   }
            sql.append(changeColumn);
                   sql.append(" = ");
                   sql.append(" CASE WHEN ");

                   sql.append("(");
                   sql.append(changeColumn);
                   sql.append(" + ?");
                   sql.append(")");
                   sql.append(" > 100 ");
                   sql.append(" THEN ");
                   sql.append(" 100 ");
                   sql.append(" WHEN ");

                   sql.append("(");
                   sql.append(changeColumn);
                   sql.append(" + ?");
                   sql.append(")");
                   sql.append(" < 0 ");
                   sql.append(" THEN ");
                   sql.append(" 0 ");
                   sql.append(" ELSE ");

                   sql.append(changeColumn);
                   sql.append(" + ?");
                   sql.append(" END ");
                   sql.append(" WHERE ");
                   sql.append(DictionaryTable.Columns.ID);
                   sql.append(" = ?");

                   BeeLog.debug("sql: "+sql.toString());
                   SQLiteStatement statement = mDb.compileStatement(sql.toString());
                   statement.bindString(1, String.valueOf(difference));
                   statement.bindString(2, String.valueOf(difference));
                   statement.bindString(3, String.valueOf(difference));

                   statement.bindLong(4, wordId);



                   try {
                       return statement.executeUpdateDelete();
                   } finally {
                       statement.close();
                       cursor = mDb.query(DictionaryTable.NAME, null, DictionaryTable.Columns.ID + " = " + wordId, null, null, null, null, null);
                       cursor.moveToFirst();
                       BeeLog.debug("changeColumn " + changeColumn);
                       BeeLog.debug("(!) finally updateWordRating" +
                                       " \nword(native): "+cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE))+
                                       " \nword(learn): "+cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.LEARN))+
                                       " \nnative rating: "+cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE_RATING))+
                                       " \nlearn rating: "+cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.LEARN_RATING))
                       );
                       cursor.close();
                   }
               } finally {
                   mDb.releaseReference();
               }

           }
       },callback);
    }

    public interface WordDBObserver{

        void onWordDBChanged();
    }

    class WordDBObservable extends Observable<WordDBObserver> {


        @Override
        public void registerObserver(WordDBObserver observer) {
            if (observer == null) {
               return;
            }
            synchronized(mObservers) {
                if (!mObservers.contains(observer)) {
                    mObservers.add(observer);
                }

            }
        }

        @Override
        public void unregisterObserver(WordDBObserver observer) {
            if (observer == null) {
               return;
            }
            synchronized(mObservers) {
                int index = mObservers.indexOf(observer);
                if (index == -1) {
                   return;
                }
                mObservers.remove(index);
            }
        }

        public void notifyChanged() {
            synchronized(mObservers) {
                for (WordDBObserver observer :mObservers) {
                    BeeLog.debug("nWordDBChanged", " notifyChanged observer: "+observer.getClass().getSimpleName());
                    observer.onWordDBChanged();
                }
            }
        }
    }

    @Override
    public List<DictionaryData> getRandomWordList(int count,WordDB.DifferentType differentType, Set<Long> differentWordIdSet ){
        return mIDictionaryDAO.getRandomWordList(count, differentType, differentWordIdSet);
    }

    public class IDictionaryDAO implements DictionaryDAO {

        @Override
        public List<DictionaryData> getRandomWordList(int count,WordDB.DifferentType differentType, Set<Long> differentWordIdSet ) {
            ArrayList<DictionaryData> dictionaryDataList = new ArrayList<>();

            BeeLog.debug(" getRandomWordList "+count);
            Cursor cursor = getRandomWords(count, differentType, differentWordIdSet);

            while (cursor.moveToNext()){
                BeeLog.debug("getRandomWordList cursor.moveToNext()");
                DictionaryData data = new DictionaryData();
                data.setId(cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.ID)));

                data.setNative(cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE)));
                data.setLearn(cursor.getString(cursor.getColumnIndex(DictionaryTable.Columns.LEARN)));
                data.setNativeRating(cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.NATIVE_RATING)));
                data.setLearnRating(cursor.getInt(cursor.getColumnIndex(DictionaryTable.Columns.LEARN_RATING)));
                dictionaryDataList.add(data);
            }
            BeeLog.debug(" getRandomWordList dictionaryDataList.size "+dictionaryDataList.size());
            cursor.close();
            return dictionaryDataList;
        }
    }


}
