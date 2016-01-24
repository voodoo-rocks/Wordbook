package my.beelzik.mobile.wordbook.db.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Andrey on 02.01.2016.
 */
public class DictionaryTable {

    public final static String NAME = "Dictionary";

    /*
       CREATE TABLE Dictionary (_id integer primary key autoincrement, native text not null, native_rating real default 0, learn text not null, learn_rating real default 0)
       */
    public final static String CREATE_SQL = "CREATE TABLE "
            + NAME+" ("
            + Columns.ID + " integer primary key autoincrement, "
            + Columns.NATIVE + " text not null, "

            + Columns.NATIVE_RATING + " real default 0, "
            + Columns.LEARN + " text not null, "
            + Columns.LEARN_RATING + " real default 0)"

            ;





    public final class Columns extends BaseColumns{

        public final static String NATIVE = "native";
        public final static String LEARN = "learn";
        public final static String NATIVE_RATING = "native_rating";
        public final static String LEARN_RATING = "learn_rating";

    }

    public static void onCreate(SQLiteDatabase db) {
       db.execSQL(CREATE_SQL);
        //TODO тут должен быть запрос на создание этой таблицы
    }

    public static void onUpgrade(SQLiteDatabase db) {
        //TODO
    }

}
