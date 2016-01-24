package my.beelzik.mobile.wordbook.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import my.beelzik.mobile.wordbook.db.WordDB;

/**
 * Created by Andrey on 10.01.2016.
 */
@Accessors(prefix = "m")
public @EqualsAndHashCode @ToString
class QuizGameOption implements Cloneable, Serializable {


    public static final int QUESTION_LANGUAGE_NATIVE = 1;
    public static final int QUESTION_LANGUAGE_LEARN = 2;
    public static final int QUESTION_LANGUAGE_RANDOM = 3;


    public static final int DEFAULT_NUMBER_OF_QUESTION_IN_GAME = 100;
    public static final int NUMBER_OF_QUESTION_IN_GAME_INFINITY = 201;
    public static final int MIN_NUMBER_OF_QUESTION_IN_GAME = 10;


    public static final int DEFAULT_NUMBER_OF_VARIANT_IN_QUESTION = 8;
    public static final int MIN_NUMBER_OF_VARIANT_IN_QUESTION = 4;
    public static final int MAX_NUMBER_OF_VARIANT_IN_QUESTION = 10;


    @Setter @Getter int mQuestionLanguage = QUESTION_LANGUAGE_RANDOM;

    @Setter @Getter int mNumberOfQuestionsInGame = DEFAULT_NUMBER_OF_QUESTION_IN_GAME;

    @Setter @Getter int mNumberOfVariantInQuestion= DEFAULT_NUMBER_OF_VARIANT_IN_QUESTION;

    @Getter
    WordDB.DifferentType mWordSelectType = WordDB.DifferentType.NONE;

    @Getter
    Set<Long> mSelectDifferenceWordIdList;



    public void setWordSelectOptions(WordDB.DifferentType selectType, Set<Long> differenceWordIdList){

        if(selectType == null){
            throw new RuntimeException("selectType cannot be null");
        }
        if(differenceWordIdList == null || differenceWordIdList.size() == 0){
            mWordSelectType = WordDB.DifferentType.NONE;
            mSelectDifferenceWordIdList = null;
            return;
        }

        if(selectType == WordDB.DifferentType.NONE){
            mWordSelectType = WordDB.DifferentType.NONE;
            mSelectDifferenceWordIdList = null;
            return;
        }

        mWordSelectType = selectType;
        mSelectDifferenceWordIdList = differenceWordIdList;


    }

    @Override
    public QuizGameOption clone() throws CloneNotSupportedException {
        return (QuizGameOption) super.clone();
    }
}

