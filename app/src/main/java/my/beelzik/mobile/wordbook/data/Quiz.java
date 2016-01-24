package my.beelzik.mobile.wordbook.data;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Andrey on 10.01.2016.
 */
@Accessors(prefix = "m")
public @Data class Quiz implements Serializable{

    private boolean mSolved;

    private List<DictionaryData> mVariantList;
    private DictionaryData mCorrectAnswer;
    private DictionaryData mUserAnswer;

    private DictionaryData.WordType mQuestionWordType;
    private String mQuestion;

    public DictionaryData.WordType getVariantsWordType() {
        if(mQuestionWordType == null){
            return null;
        }
        return mQuestionWordType == DictionaryData.WordType.NATIVE ? DictionaryData.WordType.LEARN : DictionaryData.WordType.NATIVE;
    }



    public boolean isAnswerCorrect(DictionaryData userAnswer){
        return mCorrectAnswer.equals(userAnswer);
    }

    public boolean isCorrectlySolved(){
        return isAnswerCorrect(mUserAnswer);
    }
}
