package my.beelzik.mobile.wordbook.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import my.beelzik.mobile.wordbook.data.QuizGameOption;
import my.beelzik.mobile.wordbook.db.WordDB;

/**
 * Created by Andrey on 13.01.2016.
 */
public class UserConfig {


    private static final String PREF_QUIZ_GAME_OPTION = "pref_quiz_game_option";
    private static final String PREF_CONTENT_SORT_TYPE = "pref_content_sort_type";
    private static final String PREF_USER_OPTIONS_SELECT_AS_DEFAULT = "pref_user_options_select_as_default";
    private static final String PREF_APP_FIRST_OPEN = "pref_user_options_select_as_default";
    private static final String PREF_INSERT_HUNDRED_VERBS_SOLVED = "pref_insert-hundred_verbs_solved";

    private static final QuizGameOption   DEFAULT_GAME_OPTIONS = new QuizGameOption();
    private static final WordDB.SortType DEFAULT_SORT_TYPE =  WordDB.SortType.DATE_DESC;




    SharedPreferences mUserPreference;
    Gson mGson;

    public UserConfig(Context context) {

        mGson = new Gson();
        mUserPreference =  PreferenceManager.getDefaultSharedPreferences(context);

    }

    public QuizGameOption getGameOptions(){
        QuizGameOption quizGameOption = mGson.fromJson(mUserPreference.getString(PREF_QUIZ_GAME_OPTION, null), QuizGameOption.class);
        if(quizGameOption == null){
            try {
                return DEFAULT_GAME_OPTIONS.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return quizGameOption;
    }

    public WordDB.SortType getSortType(){
        WordDB.SortType sortType = mGson.fromJson(mUserPreference.getString(PREF_CONTENT_SORT_TYPE, null), WordDB.SortType.class);
        if(sortType == null){
            return DEFAULT_SORT_TYPE;
        }
        return sortType;
    }


    public void setSortType(WordDB.SortType sortType){
        String optionJson = mGson.toJson(sortType, WordDB.SortType.class);
        SharedPreferences.Editor editor =  mUserPreference.edit();
        editor.putString(PREF_CONTENT_SORT_TYPE,optionJson);
        editor.commit();
    }


    public void setGameOptions(QuizGameOption quizGameOption){
        String optionJson = mGson.toJson(quizGameOption, QuizGameOption.class);
        SharedPreferences.Editor editor =  mUserPreference.edit();
        editor.putString(PREF_QUIZ_GAME_OPTION,optionJson);
        editor.commit();
    }

    public boolean isUserOptionSelectAsDefault() {
        return mUserPreference.getBoolean(PREF_USER_OPTIONS_SELECT_AS_DEFAULT, false);
    }

    public void setUserOptionSelectAsDefault(boolean select) {
        SharedPreferences.Editor editor =  mUserPreference.edit();
        editor.putBoolean(PREF_USER_OPTIONS_SELECT_AS_DEFAULT,select);
        editor.commit();
    }

    public boolean isAppOpenFirstTime() {
        return mUserPreference.getBoolean(PREF_APP_FIRST_OPEN, true);
    }

    public void setAppOpenedFirstTime() {
        SharedPreferences.Editor editor =  mUserPreference.edit();
        editor.putBoolean(PREF_APP_FIRST_OPEN,false);
        editor.commit();
    }

    public boolean isInsertHundredVerbsSolved() {
        return mUserPreference.getBoolean(PREF_INSERT_HUNDRED_VERBS_SOLVED, false);
    }


    public void setInsertHundredVerbsSolved() {
        SharedPreferences.Editor editor =  mUserPreference.edit();
        editor.putBoolean(PREF_INSERT_HUNDRED_VERBS_SOLVED,true);
        editor.commit();
    }

}
